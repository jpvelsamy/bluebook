package crawl.curate

import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable.HashMap
import nlp.HumingoLevensteinDistance
import nlp.JaccardDistance
import util.ProductSpecUtil

case class WipContent(orderedText: Seq[(Int, String)], itemProp: HashMap[String, String], metaInfo: HashMap[String, String], title: String)

class ContentCuration {
  var orderedText: Seq[(Int, String)] = IndexedSeq.empty[(Int, String)]
  var cleanedUpText: Seq[(Int, String)] = scala.collection.mutable.IndexedSeq.empty[(Int, String)]
  var usedUpText: Seq[(Int, String)] = scala.collection.mutable.IndexedSeq.empty[(Int, String)]
  var itemPropMap: HashMap[String, String] = HashMap.empty[String, String]
  var metaInfo: HashMap[String, String] = HashMap.empty[String, String]
  var productMeta: ProductSpec = new ProductSpec //final output
  var title: String = _
  val counter = new AtomicInteger()
  var winningTemplateName: String = null
  var winningTemplateScore: Double = 0.0f
  final val levEngine = new HumingoLevensteinDistance();

  def curate(doc: org.jsoup.nodes.Document): Unit =
    {
      title = doc.title
      this.productMeta.setName(title)
      this.productMeta.setUrl(title)
      this.productMeta.setSpecType("tv")
      val body = doc.body
      val head = doc.head
      prune(body).extract(body).extract(head).cleanse.condense.elect.map

    }

  def extract(tree: org.jsoup.nodes.Element): ContentCuration = {
    ingestMeta(tree).ingestItemProp(tree)
    this
  }

  def ingestMeta(tree: org.jsoup.nodes.Element): ContentCuration = {
    val metaIterator = tree.getElementsByTag("meta").listIterator()
    while (metaIterator.hasNext) {
      val element = metaIterator.next
      val key = element.attr("name")
      val value = element.attr("content")
      if (!key.isEmpty() && !value.isEmpty())
        metaInfo.put(key, value)
    }
    this
  }

  def ingestItemProp(tree: org.jsoup.nodes.Element): ContentCuration = {
    val itemPropiterator = tree.getElementsByAttribute("itemprop").iterator
    while (itemPropiterator.hasNext) {
      val element = itemPropiterator.next
      val key = element.attr("itemprop")
      val value = element.ownText.trim
      if (!key.isEmpty() && !value.isEmpty())
        metaInfo.put(key, value)
    }
    this
  }

  def prune(tree: org.jsoup.nodes.Element): ContentCuration = {

    val branchList: org.jsoup.select.Elements = tree.children
    val iterator = branchList.iterator()
    while (iterator.hasNext()) {
      val element = iterator.next();
      if (!tree.ownText().isEmpty()) {
        val currentCounterVal = counter.incrementAndGet().intValue()
        orderedText = orderedText :+ (currentCounterVal, tree.ownText())
      } else {
        val textNodeList = element.textNodes();
        val textNodeIter = textNodeList.iterator();
        while (textNodeIter.hasNext()) {
          val currentCounterVal = counter.incrementAndGet().intValue()
          val textNode = textNodeIter.next()
          orderedText = orderedText :+ (currentCounterVal, textNode.text())
        }
        prune(element)
      }
    }
    this
  }
  //this method is about removing the not needed data
  def cleanse(): ContentCuration = {
    orderedText = orderedText.filter(_._2.trim.length > 0)
    this
  }

  /**
   *  This method is to go after the needed data
   * //1.find the essence of product related information from the present content
   * //2.use the title and see where the tokens present in the title are densely available in rest of the content - keep track of the index
   * //3.find the product spec keywords densely matching the content - keep track of the index
   * //4.see if the indexes from 2 and 3 are closer
   * //5.if closer find the least minimal value and use it earmark further introspection
   * //orderedText.filter(_._2.trim.length > 0)
   * //THE INDEX OF HIGHEST SCORE OF MATCHING TITLE TOKEN WITH INDEPENDENT LINES IN THE TEXT
   * //similarly i need to do the same for the chopping of unnecessary text at the bottom
   *
   */
  def condense(): ContentCuration = {

    val potentialStartMarkers = ScoringApparutus.rankAndFilter(this.title, this.orderedText)
    val startingPoint = {
      if (potentialStartMarkers.isEmpty)
        0
      else
        potentialStartMarkers.minBy { text => text._1 }._1
    }
    val index = startingPoint

    this.cleanedUpText = orderedText.filter(p => { p._1 >= index })
    val potentialEndMarkers = ScoringApparutus.rankAndFilter("Have you used or purchased this product or item", this.cleanedUpText) //scoreValue._3>0.7 && scoreValue._4>0.4    
    val endingPoint = {
      if (potentialEndMarkers.isEmpty)
        this.cleanedUpText.last._1
      else
        potentialEndMarkers.maxBy { text => text._1 }._1
    }
    this.cleanedUpText = cleanedUpText.filter(p => { p._1 <= endingPoint })
    this
  }

  def elect(): ContentCuration = {
    val winningTemplateAndItsScore = ProductReasoningSystem.elect(WipContent(orderedText, itemPropMap, metaInfo, title))
    winningTemplateName = winningTemplateAndItsScore._1
    winningTemplateScore = winningTemplateAndItsScore._2
    this
  }

  /**
   * //for every line that is available in the in-bound content, find the max scoring line for every attribute and pick the next index of the content as the value
   * //ensure that the succeeding line is actually specification by again running it with the template and make sure that it is not meta-data
   * //can we fill in the instance data in the template for the above part
   * //though we have taken care of guaranteeing the quantity of the specification, quality of the specification could be still improved. In other
   * //words we still have not figured how to ensure that the value of an attribute if it goes beyond a single line, what would be the case
   * //you need to keep the cumulative ranking so that if there are two lines that pass for the same spec, then add it as two different spec
   * 	//todo - can we go with an assumption that the first match is the best match, i think we should and if its not ,then train the template data further
   * //with richer semantics
   * //For now we will also assume that the immediate line and only the immediate line will represent specification
   */
  def map(): ContentCuration = {
    val splitterPattern = "(%|-|\\s|/|;|:|#|,|\\{|\\})+"
    val template = ProductSpecUtil.loadJson(winningTemplateName)
    val detailedSpec = ProductSpecUtil.detailedSpec(template)    
    specMap(detailedSpec)
    val generalSpec = ProductSpecUtil.generalSpec(template)
    specMap(generalSpec)
    val reviewSpec = ProductSpecUtil.reviewSpec(template)
    specMap(reviewSpec)
    this
  }
  
  def specMap(spec:HashMap[String, Seq[String]]){
    var indexCounter: Int = 0
    for (indexCounter <- 0 until this.cleanedUpText.length) {
      val row = this.cleanedUpText(indexCounter)
      val lineIndex = row._1
      val lineItem = row._2.toString
      var contentTokens = Array.empty[String] :+ lineItem
      
      spec.keys.view.zipWithIndex.foreach {
        templateLineItem =>
          {
              val templateTokenKey = templateLineItem._1
              val templateTokenIndex = templateLineItem._2
              val templateValue = spec.get(templateTokenKey).get
              val templateKeywords = templateValue
              val matchSequence = ScoringApparutus.findScore(templateKeywords, contentTokens, lineIndex)
              val contentRank = ScoringApparutus.rankAndChooseMoniker(matchSequence)
              if (contentRank._2>0.8 &&  indexCounter < this.cleanedUpText.length - 3) {
                val nextItem = this.cleanedUpText(indexCounter + 1)
                val placeholder = contentRank._1
                val description =nextItem._2
                val rank = contentRank._2
                this.productMeta.add(templateTokenKey, placeholder, description, rank)
              } 
          }

      }

    }
  }

}