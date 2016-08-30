package crawl

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import scala.collection.mutable.HashMap
import com.humingo.model.CrawlContext



class Contentparser extends Actor with ActorLogging{
  import context._
  val organizeActor = actorOf(Props[Organizer], "Organizer")
  val storeDataActor = actorOf(Props[Datasaver], "CuratedContentStorer")
  def receive = {
    case CurateContent(context:CrawlContext)=>{
      val contentMap:HashMap[String,String] = HashMap.empty[String, String]
      organizeActor!OrganizeContent(context.getURLString,contentMap)
      storeDataActor!PersistCuratedContent(context.getURLString,context.getContentString)
    }
    
  }
}

class Organizer extends Actor with ActorLogging{
  import context._
  val scoringActor = actorOf(Props[MetricsScorer], "ScoreDeterniner")
  val storeDataActor = actorOf(Props[Datasaver], "OrganizedContentStorer")
  def receive = {
    case OrganizeContent(url:String, contentMap:HashMap[String,String])=>{
      scoringActor!ScoreContent(url,contentMap)
      storeDataActor!PersistOrganizedContent(url,contentMap)
    }
  }
}

class MetricsScorer extends Actor with ActorLogging{
  import context._
  val storeDataActor = actorOf(Props[Datasaver], "ScoreStorer")
  def receive={
    case ScoreContent(url:String, contentMap:HashMap[String,String]) =>{
      val score=""
      storeDataActor!PersistScore(url,contentMap,score)
    }
  }
}