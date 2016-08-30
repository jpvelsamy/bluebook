package crawl.curate

class ProductMatch {
  var templateWord:String =_
  var templateName:String=_
  var sumJaccardScore:Float=_
  var sumLevScore:Float=_
  var firstLineIndex:Int=_//need to revisit this attribute
  var netScore:Float=_
  var matchingWordSeq:Seq[MatchingWord]=scala.collection.immutable.IndexedSeq.empty[MatchingWord]
  val wipContent:WipContent=null
}

class MatchingWord{
  var matchingWord:String=_
  var matchedLine:String=_
  var indexOfLine:Int=_
  var lineFrequency:Int=_
  var indexnLine:Int=_
  var jaccardScore:Float=_
  var levScore:Float=_
}