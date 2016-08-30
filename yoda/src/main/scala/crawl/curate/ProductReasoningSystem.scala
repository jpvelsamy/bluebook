package crawl.curate

import java.io.File
import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonNode

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.Patterns
import akka.util.Timeout
import util.ProductSpecUtil
import nlp.JaccardDistance
import nlp.HumingoLevensteinDistance

case class ScoringComplete(category: String, score: Float)
case class StartScoring(wipContent: WipContent)
case class ScoreUsingTemplate(wipContent: WipContent, template: JsonNode)

object ProductReasoningSystem {
  //private val actorSystem = ActorSystem("Reasoning-Subsystem")

  
  def elect(wipContent: WipContent): (String, Double) = {
    val folderPath = this.getClass.getClassLoader.getResource("spec.pointer").getFile
    val fileList = {
      val d = new File(folderPath).getParentFile
      if (d.exists && d.isDirectory)
        d.listFiles.filter(_.isFile).filter(_.getName.contains(".spec.json")).toList
      else
        List[File]()
    }
    val splitterPattern = "(%|-|\\s|/|;|:|#|,|\\{|\\})+"
    val templateFile = fileList(0).getAbsolutePath
    val template = ProductSpecUtil.loadJson(templateFile)
    val contentTokens = wipContent.title.split(splitterPattern)
    val brandKeywords = ProductSpecUtil.brandKeywords(template)
    val brandMatchSeq = ScoringApparutus.findScore(brandKeywords, contentTokens, Integer.MAX_VALUE)
    val chosenBrand = ScoringApparutus.rankAndChooseMoniker(brandMatchSeq)
    val categoryKeywords = ProductSpecUtil.categoryKeywords(template)
    val categoryMatchSeq = ScoringApparutus.findScore(categoryKeywords, contentTokens, Integer.MAX_VALUE - 1)
    val chosenCateogoryName = ScoringApparutus.rankAndChooseMoniker(categoryMatchSeq)
    val confirmSelection = ScoringApparutus.reaffirm(chosenCateogoryName._1, chosenBrand._1)

   //I love this way of progranming, lazy prepping the code to ascertain something
    val chosenNameFromItemProp = {
      if(!wipContent.itemProp.isEmpty)
      {
         val itemPropName: String=wipContent.itemProp.get("name").get
         val itemPropToken = itemPropName.split(splitterPattern)
         val itemPropNameSeq = ScoringApparutus.findScore(brandKeywords, itemPropToken, Integer.MAX_VALUE - 2)
        ScoringApparutus.rankAndChooseMoniker(itemPropNameSeq)._2
      }
      else
        0.0
    }

    val totalScore = confirmSelection + chosenBrand._2 + chosenCateogoryName._2 + chosenNameFromItemProp
    (templateFile, totalScore)
  }

  /*
   //Ask multiple product templates to score the content and select one template
  // Election process makes sense because we can find a shortest possible way to determine what type of product the content represents sooner
  //instead of using a more guaranteed approach(just do the parsing in totality)
   * def elect(wipContent: WipContent): (String,Float) = {
    val scoringActor = actorSystem.actorOf(Props[ProductReasoningParent], "score-keeper")
    var winningTemplate: String = null
    var winningValue: Float = 0
    val timeout = new Timeout(5, TimeUnit.SECONDS);
    val response = Patterns.ask(scoringActor, StartScoring(wipContent), timeout)
    response.onSuccess {
      case result: (ScoringComplete) ⇒ {
        winningTemplate = result.category
        winningValue = result.score
      }
    }
    (winningTemplate, winningValue)
  }*/

}

class ProductReasoningParent extends Actor with ActorLogging {
  import context._
  private val folderPath = this.getClass.getClassLoader.getResource("spec.pointer").getFile
  private val fileList = {
    val d = new File(folderPath).getParentFile
    if (d.exists && d.isDirectory)
      d.listFiles.filter(_.isFile).filter(_.getName.contains(".spec.json")).toList
    else
      List[File]()
  }

  private var scoreList: Seq[ScoringComplete] = IndexedSeq.empty[ScoringComplete]

  def receive = {
    case score: ScoringComplete => {
      scoreList = scoreList :+ (ScoringComplete(score.category, score.score))
    }
    case input: StartScoring => {
      fileList.foreach { file =>
        {
          val template = ProductSpecUtil.loadJson(file)
          val reasoningunit = context.system.actorOf(Props(new ProductReasoningUnit), file.getName)
          reasoningunit ! ScoreUsingTemplate(input.wipContent, template)
        }
      }
    }
  }
  def OnSuccess = {
    scoreList.maxBy { score => score.score }
  }
}

class ProductReasoningUnit extends Actor with ActorLogging {

  final val splitterPattern = "(%|-|\\s|/|;|:|#|,|\\{|\\})+"

  def receive = {
    case wip: ScoreUsingTemplate => {
      val template = wip.template
      val wipContent = wip.wipContent
      val contentTokens = wipContent.title.split(splitterPattern)
      val brandKeywords = ProductSpecUtil.brandKeywords(template)
      val brandMatchSeq = ScoringApparutus.findScore(brandKeywords, contentTokens, Integer.MAX_VALUE)
      val chosenBrand = ScoringApparutus.rankAndChooseMoniker(brandMatchSeq)

      val categoryKeywords = ProductSpecUtil.categoryKeywords(template)
      val categoryMatchSeq = ScoringApparutus.findScore(categoryKeywords, contentTokens, Integer.MAX_VALUE - 1)
      val chosenCateogoryName = ScoringApparutus.rankAndChooseMoniker(categoryMatchSeq)
      val confirmSelection = ScoringApparutus.reaffirm(chosenCateogoryName._1, chosenBrand._1)

      val itemPropName: String = wip.wipContent.itemProp.get("name").get
      val itemPropToken = itemPropName.split(splitterPattern)
      val itemPropNameSeq = ScoringApparutus.findScore(brandKeywords, itemPropToken, Integer.MAX_VALUE - 2)
      val chosenNameFromItemProp = ScoringApparutus.rankAndChooseMoniker(itemPropNameSeq)

      val totalScore = confirmSelection + chosenBrand._2 + chosenCateogoryName._2 + chosenNameFromItemProp._2
      sender ! ScoringComplete(chosenCateogoryName._1, totalScore) //the value for score needs to come from choseMoniker

    }
  }

}