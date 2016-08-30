package crawl.curate

import org.junit.Test
import org.jsoup.Jsoup

class CondenseTest {
  @Test def testExecute = {
  }
  
  @Test def condenseFlipkart={
    val inputStream = this.getClass.getResourceAsStream("onida1-flipkart.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense
    val output=curateContent.cleanedUpText
    println(output.mkString("\n"))
  }
  
  @Test def condenseSnapdeal={
     val inputStream = this.getClass.getResourceAsStream("onida3-snapdeal.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense
    val output=curateContent.cleanedUpText
    println(output.mkString("\n"))
  }
  
  @Test def condenseAmazon={
     val inputStream = this.getClass.getResourceAsStream("onida2-amazon.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense
    val output=curateContent.cleanedUpText
    println(output.mkString("\n"))
  }
  
   @Test def condenseCroma={
     val inputStream = this.getClass.getResourceAsStream("onida4-croma.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense
    val output=curateContent.cleanedUpText
    println(output.mkString("\n"))
  }
  
}