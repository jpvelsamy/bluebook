package util

import java.io.InputStream
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import scala.collection.mutable.HashMap
import java.io.File
import java.io.FileInputStream

object ProductSpecUtil {

  def loadJson(file:String):JsonNode={
    val filePath = new File(file)
    loadJson(filePath)
  }
  
  def loadJson(file:File):JsonNode={
    val inputStream = new FileInputStream(file)
    loadJson(inputStream)
  }
  
  def loadJson(inputStream: InputStream): JsonNode = {
    val objectMapper = new ObjectMapper()
    val parent = objectMapper.readTree(inputStream)
    parent
  }

  def categoryKeywords(parent: JsonNode): Seq[String] = {
    return getKeywords(parent, "category.keywords")
  }
  
  def brandKeywords(parent:JsonNode):Seq[String]={
    return getKeywords(parent, "brand.keywords")
  }
  
  private def getKeywords(parent:JsonNode,path:String):Seq[String]={
    val categoryList = parent.path(path)
    var keywordList: Seq[String] = IndexedSeq.empty[String]
    val catIter = categoryList.elements
    while (catIter.hasNext) {
      val value = catIter.next
      keywordList = keywordList :+ value.textValue()
    }
    return keywordList
  }
  
  def generalSpec(parent:JsonNode): HashMap[String, Seq[String]]={    
    detailedProductSpect(parent,"product.spec.general")
  }
  
  def detailedSpec(parent:JsonNode): HashMap[String, Seq[String]]={
   detailedProductSpect(parent,"product.spec.detail") 
  }
  
  def reviewSpec(parent:JsonNode): HashMap[String, Seq[String]]={
   detailedProductSpect(parent,"review.detail") 
  }
  
  private def detailedProductSpect(parent: JsonNode, path: String): HashMap[String, Seq[String]] =
    {
      var specMap: HashMap[String, Seq[String]] = HashMap()
      val specList = parent.path(path)
      val specListIter = specList.fields()
      while (specListIter.hasNext) {
        val fieldEntry = specListIter.next()
        val key = fieldEntry.getKey
        val value = fieldEntry.getValue
        val specKeywordsIter = value.elements
        var keywordList: Seq[String] = IndexedSeq.empty[String]
        while (specKeywordsIter.hasNext) {
          keywordList = keywordList :+ specKeywordsIter.next.asText
        }
        specMap.put(key, keywordList)
      }
      return specMap
    }

}