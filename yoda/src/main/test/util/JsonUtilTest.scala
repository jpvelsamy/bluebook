package util

import org.junit.Test

class JsonUtilTest {
  @Test def testExecute=
  {     
  }
  @Test def loadTVSpec=
  {
    val input = this.getClass.getClassLoader.getResourceAsStream("ledtv.spec.json")
    val parent = ProductSpecUtil.loadJson(input)
    println(parent)
  }
  
  @Test def category=
  {
    val input = this.getClass.getClassLoader.getResourceAsStream("ledtv.spec.json")
    val parent = ProductSpecUtil.loadJson(input)
    val keywords=ProductSpecUtil.categoryKeywords(parent)
    println(keywords.mkString("\n"))
  }
  
  @Test def brand=
  {
    val input = this.getClass.getClassLoader.getResourceAsStream("ledtv.spec.json")
    val parent = ProductSpecUtil.loadJson(input)
    val keywords=ProductSpecUtil.brandKeywords(parent)
    println(keywords.mkString("\n"))
  }
  
  @Test def specdetail={
    val input = this.getClass.getClassLoader.getResourceAsStream("ledtv.spec.json")
    val parent = ProductSpecUtil.loadJson(input)
    val details=ProductSpecUtil.detailedSpec(parent)
    println(details.mkString("\n"))  
  }
  
  @Test def specgeneral={
    val input = this.getClass.getClassLoader.getResourceAsStream("ledtv.spec.json")
    val parent = ProductSpecUtil.loadJson(input)
    val path="product.spec.general"
    val details=ProductSpecUtil.generalSpec(parent)
    println(details.mkString("\n"))
  }
  
}