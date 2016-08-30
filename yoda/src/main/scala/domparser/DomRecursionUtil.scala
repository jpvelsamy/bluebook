package domparser

import scala.annotation.tailrec
import scala.xml.Text

object DomRecursionUtil {

  def factorial(number: Int): Int = {
    @tailrec
    def factorialWithAccumulator(accumulator: Int, number: Int): Int = {
      if (number == 1)
        return accumulator
      else
        factorialWithAccumulator(accumulator * number, number - 1)
    }
    factorialWithAccumulator(1, number)
  }

  
  def curate(tree:scala.xml.Node):Unit=
  {
      val children = tree.child
      children.foreach { branch => {
          if(branch.isInstanceOf[Text])
            println(branch.text)
          else            
            curate(branch)
        }
      }
      
  }
}