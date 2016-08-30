package crawl.curate

import org.junit.Test
import org.jsoup.Jsoup
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper

class MapTest {
  @Test def testExecute = {
  }

  @Test def testFlipkartMap = {
    val inputStream = this.getClass.getResourceAsStream("onida1-flipkart.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense.elect.map
    val output = curateContent.productMeta
    //println(output)
     val json = curateContent.productMeta.toJson() 
     println(json)
  }

  @Test def testAmazonMap = {
    val inputStream = this.getClass.getResourceAsStream("onida2-amazon.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense.elect.map
    val output = curateContent.productMeta
     val json = curateContent.productMeta.toJson() 
     println(json)
  }

  @Test def testSnapdealMap = {
    val inputStream = this.getClass.getResourceAsStream("onida3-snapdeal.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense.elect.map
    val output = curateContent.productMeta
   val json = curateContent.productMeta.toJson() 
     println(json)
  }

  @Test def testCromaMap = {
    val inputStream = this.getClass.getResourceAsStream("onida4-croma.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense.elect.map
    val output = curateContent.productMeta
    val mapper = new ObjectMapper();
    val json = mapper.writeValueAsString(output);
    println(json)
  }

  @Test def testpaytmMap = {
    val inputStream = this.getClass.getResourceAsStream("onida5-paytm.html")
    val doc = Jsoup.parse(inputStream, "UTF-8", "")
    val curateContent = new ContentCuration
    curateContent.title = doc.title()
    curateContent.prune(doc.body).extract(doc.body).extract(doc.head).cleanse.condense.elect.map
    val output = curateContent.productMeta
    val mapper = new ObjectMapper();
    val json = mapper.writeValueAsString(output);
    println(json)
  }
}