package crawl.curate

import org.jsoup.Jsoup
import org.junit.Test

class CleanseTest {
  @Test def testExecute = {
  }

  @Test def cleanseVanilla = {
    val inputStream = this.getClass.getResourceAsStream("onida1-flipkart.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse
    val output = curateContent.orderedText
    println(output.mkString("\n"))

  }
}