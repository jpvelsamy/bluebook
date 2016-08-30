package crawl

import akka.actor.Actor
import akka.actor.ActorLogging

import scala.collection.mutable.LinearSeq
import akka.actor.Props
import com.humingo.crawl.URLExtractorUtil
import com.humingo.model.CrawlContext
import java.net.URL

class UrlExtractor extends Actor with ActorLogging{
  val extractUrl = new URLExtractorUtil()
  import context._
  val storeDataActor = actorOf(Props[Datasaver], "UrlStorer")
  def receive={
    case ExtractUrl(context)=>{
      val urlList = extractUrl.extractOutgoingUrls(context)
      sender!UrlsExtracted(context.getURLString,urlList)
      storeDataActor!PersistExtractedUrl(context.getURLString,urlList)
    }
  }
  
}