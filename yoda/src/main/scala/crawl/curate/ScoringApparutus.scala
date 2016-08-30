package crawl.curate

import nlp.JaccardDistance
import nlp.HumingoLevensteinDistance

object ScoringApparutus {
  
  final val  levEngine = new HumingoLevensteinDistance();
  
  def findScore(templateKeywords: Seq[String], contentTokens: Array[String], lineIndex:Int) = {
    var productMatchSeq :Seq[ProductMatch]=scala.collection.immutable.IndexedSeq.empty[ProductMatch]
    //check for brand affinity and product category affinity, if there is affinity, then great if not go for introspecting the itemprop, followed by meta tag followed by rest of the content
    //println("template="+templateKeywords+",content="+contentTokens.mkString(",")+",index="+lineIndex)
    templateKeywords.foreach { templateRefToken => {
        val productMatch = new ProductMatch
        productMatch.templateWord=templateRefToken
        productMatch.templateName = "notfilled"
        contentTokens.view.zipWithIndex.foreach { case (value, index) => {
            val titleToken = value.toString
            val templateTokenChar = templateRefToken.toLowerCase.toCharArray
            val titleTokenChar = titleToken.toLowerCase.toCharArray
            val simpleCoeffs = JaccardDistance.jaccard(templateTokenChar, titleTokenChar);
            val levCoeffs = levEngine.getDistance(titleToken.toLowerCase, templateRefToken.toLowerCase)
            if(simpleCoeffs>0.4 && levCoeffs>0.4){
                val matchingWord = new MatchingWord
                matchingWord.indexOfLine=lineIndex
                matchingWord.indexnLine = index
                matchingWord.jaccardScore=simpleCoeffs
                matchingWord.levScore=levCoeffs
                matchingWord.matchedLine=contentTokens.mkString("-")
                matchingWord.matchingWord=value
                productMatch.firstLineIndex = {
                  if(!productMatch.matchingWordSeq.isEmpty)
                  {
                    var minlineMatchingWord = productMatch.matchingWordSeq.minBy { matchingWordInSeq => matchingWord.indexOfLine }
                    minlineMatchingWord.indexOfLine
                  }
                  else
                    lineIndex
                }
                productMatch.sumJaccardScore+=matchingWord.jaccardScore
                productMatch.sumLevScore+=matchingWord.levScore
                productMatch.netScore = productMatch.sumJaccardScore+productMatch.sumLevScore
                productMatch.matchingWordSeq = productMatch.matchingWordSeq:+(matchingWord)
            }
            
          }
        }
        productMatchSeq = productMatchSeq:+productMatch
      }      
    }
    productMatchSeq
  }
  
  
  
//Moniker - The representative token that has the highest score
  def rankAndChooseMoniker(productMatchSeq: Seq[ProductMatch]):(String,Float) = {
    var bestMatch = productMatchSeq.maxBy { productMatchinSeq => {
      productMatchinSeq.netScore
    } }
    val chosenMoniker = bestMatch.templateWord
    val score = bestMatch.netScore
    
    (chosenMoniker,score)
  }

  def reaffirm(chosenCateogoryName: String, chosenBrand: String):Int = {
    0
  }
  
    def rankAndFilter(referenceToken: String, inputTextSequence: Seq[(Int, String)] ):Seq[(Int,String)] = {
    val splitterPattern = "(%|-|\\s|/|;|:|#|,|\\{|\\})+"
    val filteredSequence = inputTextSequence.filter(p=>{
       var score: Seq[(String, String, Float, Float)] = IndexedSeq.empty[(String, String, Float, Float)]
     
       if(p._2.isEmpty())
         false
       else {
              val inputContent = p._2       
              val titleLowerCase = referenceToken.toLowerCase
              val inputLowerCase = inputContent.toLowerCase
              val titleChar = titleLowerCase.toCharArray
              val inputChar =inputLowerCase.toCharArray
              val simpleCoeffs = JaccardDistance.jaccard(titleChar, inputChar);
              val levCoeffs = levEngine.getDistance(titleLowerCase, inputLowerCase)
             
              score=score:+(referenceToken, inputContent, simpleCoeffs, levCoeffs)     
              val scoreValue=score.maxBy { score => score._3 }
              if(scoreValue._3>0.7 && scoreValue._4>0.4)
              {
                println("Incoming text="+p._2+", jaccard ="+simpleCoeffs+", levCoeffs="+levCoeffs)
                println("score="+scoreValue)
              }
              (scoreValue._3>0.7 && scoreValue._4>0.4)       
       }
     })
     filteredSequence
  }
}