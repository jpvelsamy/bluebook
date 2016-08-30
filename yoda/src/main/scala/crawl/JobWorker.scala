package crawl

import akka.actor.Actor
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.ClusterDomainEvent
import akka.cluster.Member
import akka.actor.RootActorPath
import akka.actor.Props
import akka.actor.ActorLogging
import java.util.concurrent.atomic.AtomicInteger
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

class JobWorker extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  import context._
  val contentDownloader = actorOf(Props[ContentDownloader], "ContentDownloader")
  val urlExtractor = actorOf(Props[UrlExtractor], "UrlExtractor")
  //val domParser = actorOf(Props[Contentparser],"ContentParser")

  override def preStart(): Unit = {
    //I am really not sure about what am i doing here, i.e subscribing to so many event types knowing clearly that
    //they are pretty much the same
    cluster.subscribe(self, classOf[MemberUp], classOf[MemberEvent], classOf[ClusterDomainEvent])
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

    case MemberUp(m) => { register(m) }

    case StartJob(seedUrl: String) => {
      log.info("Initiating crawl for seed url {}", seedUrl)
      contentDownloader ! ContentDownload(seedUrl)
    }

    case CompletedDownload(context) => {
      urlExtractor ! ExtractUrl(context)
    }

    case urlExtract: UrlsExtracted => {
      sender forward urlExtract
    }

    case ContentCurated => {

    }

  }

  def register(member: Member): Unit = {
    if (member.hasRole("jobmanager"))
      context.actorSelection(RootActorPath(member.address) / "user" / "jobmanager") !
        JobWorkerRegistered
  }
}

object JobWorkerFacade {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [jobworker]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[JobWorker], name = "backend")
  }
}