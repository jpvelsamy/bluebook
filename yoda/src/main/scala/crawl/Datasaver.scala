package crawl

import akka.actor.Actor
import akka.actor.ActorLogging


class Datasaver  extends Actor with ActorLogging{
  
  def receive = {
    case crawlData:PersistCrawledContent=>{
      val key = crawlData.context.getURLString
      val info =crawlData.context.getContentString
    }
    case curatedData:PersistCuratedContent=>{
      val key = curatedData.url
      val value = curatedData.content
    }
    case organizedData:PersistOrganizedContent=>{
      val key = organizedData.url
      val value = organizedData.contentMap
    }
    case score:PersistScore=>{
      val key = score.url
      val value = score.contentMap
    }
  }
}