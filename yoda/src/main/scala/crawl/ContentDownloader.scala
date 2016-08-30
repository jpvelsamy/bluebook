package crawl

import akka.actor.ActorLogging
import akka.actor.Actor


import akka.actor.Props
import com.humingo.crawl.PageFetcher
import java.net.URL
import com.humingo.model.CrawlContext

class ContentDownloader extends Actor with ActorLogging{
  val downloadContent = new PageFetcher()
  import context._
  val storeDataActor = actorOf(Props[Datasaver], "CrawledContentStorer")
  def receive ={
    case ContentDownload(url)=>{
      var context = new CrawlContext(new URL(url))
      downloadContent.fetch(context)
      sender!CompletedDownload(context)
      storeDataActor!PersistCrawledContent(context)
      
    }
  }
  
}