package crawl.curate

import org.jsoup.Jsoup
import org.junit.Test

class ExtractTest {
  
   @Test def testExecute = {
  }
   
  @Test def testExtractVanilla = {
    val inputStream = this.getClass.getResourceAsStream("onida1-flipkart.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head)
    val output = curateContent.itemPropMap
    println(output.mkString("\n"))
    val metaOut = curateContent.metaInfo
    println(metaOut.mkString("\n"))
  }
  
  @Test def testExtractMeta = {
    val inputStream = this.getClass.getResourceAsStream("onida3-snapdeal.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head)
    val output = curateContent.itemPropMap
    println(output.mkString("\n"))
    val metaOut = curateContent.metaInfo
    println(metaOut.mkString("\n"))
  }
}