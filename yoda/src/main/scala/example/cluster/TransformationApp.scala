package example.cluster

import example.cluster.sample.cluster.transformation.TransformationFrontend

object TransformationApp {

  def main(args: Array[String]): Unit = {
    // starting 2 frontend nodes and 3 backend nodes
    TransformationFrontend.main(Seq("18066").toArray)    
    TransformationBackend.main(Seq("36132").toArray)
    /*TransformationBackend.main(Array.empty)
    TransformationBackend.main(Array.empty)
    TransformationFrontend.main(Array.empty)*/
  }

}