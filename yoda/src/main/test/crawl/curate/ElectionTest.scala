package crawl.curate

import org.jsoup.Jsoup
import org.junit.Test

class ElectionTest {
  @Test def testExecute = {
  }
  
  @Test def electFlipkart={
     val inputStream = this.getClass.getResourceAsStream("onida1-flipkart.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense.elect
    val output=(curateContent.winningTemplateName, curateContent.winningTemplateScore)
    println(output)
  }
}