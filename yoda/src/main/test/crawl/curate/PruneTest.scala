package crawl.curate

import org.junit.Test
import org.jsoup.Jsoup

class PruneTest {
  @Test def testExecute = {
  }

  @Test def testPruneVanilla = {
    val inputStream = this.getClass.getResourceAsStream("onida1-flipkart.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.prune(doc.body)
    val output = curateContent.orderedText
    println(output.mkString("\n"))
  }

  
  
}