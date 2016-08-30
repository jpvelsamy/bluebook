package crawl

import language.postfixOps
import scala.concurrent.duration._
import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.Props
import akka.pattern.ask
import java.util.concurrent.atomic.AtomicInteger
import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.util.Timeout

class JobManager extends Actor with ActorLogging {
  var backends = IndexedSeq.empty[ActorRef]
  val crawlThreshold = 10
  val urlCount = new AtomicInteger(0)
  def receive = {

    case job: StartJob if backends.isEmpty =>
      sender() ! JobFailed("Service unavailable, try again later", job)

    case job: StartJob => {
      log.info("Starting job for {}", job.url)
      urlCount.addAndGet(1)
      backends(urlCount.intValue() % backends.size) forward job
    }

    case JobWorkerRegistered if !backends.contains(sender()) =>
      context watch sender()
      backends = backends :+ sender()

    case urlExtract: UrlsExtracted => {
      val iterator = urlExtract.urlList.iterator()
      while(iterator.hasNext())
       {
        val url = iterator.next()
        urlCount.addAndGet(1)
        backends(urlCount.intValue() % backends.size) ! StartJob(url.getURLString)
      }
      if (urlCount.intValue() > 10) {
        //Not the right thing to do, but for now it should suffice
        java.lang.System.exit(0)
      }
    }

  }
}

object JobManagerFacade {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [jobmanager]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    val frontend = system.actorOf(Props[JobManager], name = "jobmanager")
    import system.dispatcher
    implicit val timeout = Timeout(5 seconds)
    frontend ? StartJob("http://www.flipkart.com/onida-80cm-32-hd-ready-smart-led-tv/p/itme9k68f93wxmhz") onSuccess {
      case result => println(result)
    }

    /*val counter = new AtomicInteger
    import system.dispatcher
    system.scheduler.schedule(2.seconds, 2.seconds) {
      implicit val timeout = Timeout(5 seconds)
      (frontend ? TransformationJob("hello-" + counter.incrementAndGet())) onSuccess {
        case result => println(result)
      }
    }*/

  }
}