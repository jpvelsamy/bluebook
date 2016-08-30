package crawl

import akka.actor.ActorSystem
import akka.actor.Actor
import scala.annotation.tailrec
import akka.actor.ActorRef
import akka.actor.Props
import akka.routing.RoundRobinRoutingLogic
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.actor.Terminated
import scala.concurrent.duration.Duration
import akka.actor.actorRef2Scala

object PiCalculator extends App {

  calculate(numberOfWorkers = 4, numberOfElements = 10000, numberOfMessages = 10000)

  def calculate(numberOfWorkers: Int, numberOfElements: Int, numberOfMessages: Int) {
    val system = ActorSystem("PiActorSystem")
    val listener = system.actorOf(Props[Listener], name = "ResultsListener")
    val master   = system.actorOf(Props(new Master(numberOfWorkers, numberOfMessages, numberOfElements, listener)), name = "MasterNode")
    master ! Calculate

  }

  sealed trait PiMessage
  case object Calculate extends PiMessage
  case class Work(start: Int, numberOfElements: Int) extends PiMessage
  case class Result(value: Double) extends PiMessage
  case class ApproximatedPi(pi: Double, duration: Duration)

  class Worker extends Actor {
    @tailrec
    private def calculatePiFor(start: Int, limit: Int, acc: Double): Double =
      start match {
        case x if x == limit ⇒ acc
        case _               ⇒ calculatePiFor(start + 1, limit, acc + 4.0 * (1 - (start % 2) * 2) / (2 * start + 1))
      }

    def receive =
      {
        case Work(start, numberOfElements) ⇒ sender ! Result(calculatePiFor(start, start + numberOfElements - 1, 0.0))
      }

  }

  class Master(numberOfWorkers: Int, numberOfMessages: Int, numberOfElements: Int, listener: ActorRef) extends Actor {
    var pi: Double = _
    var numberOfResults: Int = _
    val start: Long = System.currentTimeMillis

    var router = {
      val routees = Vector.fill(5) {
        val r = context.actorOf(Props[Worker])
        context watch r
        ActorRefRoutee(r)
      }
      Router(RoundRobinRoutingLogic(), routees)
    }

    def receive = {      
      case Calculate => {
        var i = 0;
        // for loop execution with a range
        for (i <- 1 until numberOfMessages) {
          router.route(Work(i * numberOfElements, numberOfElements), sender)
        }
      }
      case Result(value)=>{
        pi += value
        numberOfResults += 1
        numberOfResults match {
                                    case x if x == numberOfMessages =>{
                                      listener ! ApproximatedPi(pi, duration = Duration.apply(System.currentTimeMillis()-start, "millis"))
                                      context.stop(self)
                                    }
                                    case _                ⇒ 
                                 }
      }
      case Terminated(a) => {
        router = router.removeRoutee(a)
        val r = context.actorOf(Props[Worker])
        context watch r
        router = router.addRoutee(r)
      }
    }

  }
  class Listener extends Actor {
        def receive = {
            case ApproximatedPi(pi, duration) ⇒ println(s"\n\tPi approximation: $pi, took: $duration ")
                                                 context.system.shutdown()
        }
    }

}