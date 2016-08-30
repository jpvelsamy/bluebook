package crawl

object JobApp {
  def main(args: Array[String]): Unit = {
    JobWorkerFacade.main(Seq("36132").toArray)
    JobManagerFacade.main(Seq("18066").toArray)
    
  }
}