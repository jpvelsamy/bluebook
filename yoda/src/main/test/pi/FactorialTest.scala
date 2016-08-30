package pi

import org.junit.Test
import domparser.DomRecursionUtil;

class FactorialTest {
  @Test def testExecute(){ 
    val output= DomRecursionUtil.factorial(8)
    println(output)
  }
}