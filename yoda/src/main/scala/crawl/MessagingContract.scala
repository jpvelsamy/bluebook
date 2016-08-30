package crawl

import scala.collection.mutable.LinearSeq
import scala.collection.mutable.HashMap
import com.humingo.model.CrawlContext

sealed trait YodaMessage


  case class StartJob(url:String) extends YodaMessage
  case class ContentDownload(url:String) extends YodaMessage
  case class CompletedDownload(context:CrawlContext) extends YodaMessage
  case class ExtractUrl(context:CrawlContext) extends YodaMessage
  case class UrlsExtracted(url:String, urlList:java.util.List[CrawlContext]) extends YodaMessage
  case class CurateContent(context:CrawlContext) extends YodaMessage
  case class ContentCurated(context:CrawlContext) extends YodaMessage
  case class OrganizeContent(url:String,contentMap:HashMap[String,String]) extends YodaMessage
  case class ScoreContent(url:String,contentMap:HashMap[String,String]) extends YodaMessage
  case class PersistCrawledContent(context:CrawlContext) extends YodaMessage
  case class PersistExtractedUrl(url:String, urlList:java.util.List[CrawlContext]) extends YodaMessage
  case class PersistCuratedContent(url:String, content:String) extends YodaMessage
  case class PersistOrganizedContent(url:String, contentMap:HashMap[String,String]) extends YodaMessage
  case class PersistScore(url:String,  contentMap:HashMap[String,String], score:String) extends YodaMessage
  case object JobWorkerRegistered
  final case class JobFailed(reason: String, job: StartJob)
