package crawl.curate

import scala.io.Source
import scala.xml.XML

import org.jsoup.Jsoup
import org.junit.Test

import domparser.DomRecursionUtil

class ParserTest {
  
  @Test def testExecute=
  {
    /*val inputStream = this.getClass.getResource("sample.html").getFile()
    val crawlContent = Source.fromFile(inputStream).getLines.mkString
    println(crawlContent)*/
        
  }
  
  @Test def fileReading=
  {
    val inputFile = this.getClass.getResource("sample.html").getFile()
    val crawlContent = Source.fromFile(inputFile).getLines.mkString
    println(crawlContent)
  }
  
  @Test def testCurate=
  {
    val dom = XML.load(this.getClass.getResource("sample.html"))
    DomRecursionUtil.curate(dom)
  }
  
  @Test def testJsoupParsingFlipkart=
  {
    val inputStream = this.getClass.getResourceAsStream("onida1-flipkart.html")    
     doStuff(inputStream)
  }
  
  @Test def testJsoupParsingmazon=
  {
    val inputStream = this.getClass.getResourceAsStream("onida2-amazon.html")    
     doStuff(inputStream)
  }
  
  @Test def testJsoupParsingSnapdeal=
  {
    val inputStream = this.getClass.getResourceAsStream("onida3-snapdeal.html")    
     doStuff(inputStream)
  }
  
  @Test def testJsoupParsingCroma=
  {
    val inputStream = this.getClass.getResourceAsStream("onida4-croma.html")    
    doStuff(inputStream)
  }

  def doStuff(inputStream: java.io.InputStream) = {
    val doc = Jsoup.parse(inputStream, "UTF-8", "")    
    val curateContent = new ContentCuration
    curateContent.curate(doc)
    val output = curateContent.orderedText
    println(output.mkString("\n"))
  }
}
