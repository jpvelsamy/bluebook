package config

import java.sql._



import org.slf4j.Logger

import com.typesafe.config._
import com.typesafe.scalalogging.LazyLogging


/**
 * @author jpvel
    TODO I would like to use immutable map instead of mutable map
    
 */
object ConfigurationService extends LazyLogging
{
  
  private val conf = ConfigFactory.parseResources("configstore.props")
  logger.info("Initializing the config store from config file"+ conf.origin().url())
  private val url = conf.getString("mysql.url")
  private val driver = conf.getString("mysql.driver")
  private val user = conf.getString("mysql.user")
  private val password = conf.getString("mysql.passwd")
  private val keyQueryBatch = conf.getString("mysql.key_global_sql")
  private val keyQueryProces = conf.getString("mysql.key_process_sql")
  private val allQueryBatch = conf.getString("mysql.batch_sql")
  private val allQueryProcess= conf.getString("mysql.process_sql")
  
  Class.forName(driver)  
  
  def getConfig(processName:String, key:String): String = 
  {
    val conn = DriverManager.getConnection(url,user, password) 
    var value:String=null
    try 
    {
      value = getProcessConfigValue(conn, processName, key)
      if(value==null || value.isEmpty())
      value=  getBatchConfigValue(conn, key)
       
    } catch 
    {
     case t: Throwable => logger.error("Error reading information from config store{}", url,t)
    }
    finally
    {
     conn.close()  
    }
    value
  }
  
  def getGlobalconfig():scala.collection.immutable.Map[String, String]=   {
    val conn = DriverManager.getConnection(url,user, password) 
     val valueList= getAllBatchConfigValue(conn)
     valueList
  }
  def getAllConfig(processName:String):scala.collection.immutable.Map[String, String]=   {
     val conn = DriverManager.getConnection(url,user, password) 
     val valueList= getAllBatchConfigValue(conn)++getAllProcessConfigValue(conn, processName)
     valueList
  }
  
  private def getBatchConfigValue(conn:Connection, key:String):String =  {
      val stmt = conn.prepareStatement(keyQueryBatch)
      var valueRet:String=null
      try {
        stmt.setString(1, key)
        val rs = stmt.executeQuery()        
        try {
          if(rs.next()) {
            val value = rs.getString(1)
            valueRet=value
          }
        }
        finally {
          rs.close()
        }
      }
      finally{
        stmt.closeOnCompletion()
      }
      valueRet
 }
 
 private def getProcessConfigValue(conn:Connection, processName:String, key:String):String= {
   val stmt = conn.prepareStatement(keyQueryProces)
   var valueRet:String=null
   try      {
        stmt.setString(1, processName)
        stmt.setString(2,key)
        val rs = stmt.executeQuery()
        try        {
          if(rs.next())          {
            val value = rs.getString(1) 
            valueRet=value
          }
        }
        finally        {
          rs.close()
        }
      }
      finally      {
        stmt.closeOnCompletion()
      }
      valueRet
 }
 
 private def getAllBatchConfigValue(conn:Connection):scala.collection.immutable.HashMap[String, String]=
 {
   val stmt=conn.createStatement()
   var configInfo = scala.collection.immutable.HashMap[String,String]()
   try
   {
     val rs = stmt.executeQuery(allQueryBatch)
     try{
      while(rs.next()){
            configInfo+=((rs.getString(1), rs.getString(2)))
      }
    }finally{
      rs.close()
    }
   }finally{
     stmt.closeOnCompletion()
   }
   configInfo
 }

private  def getAllProcessConfigValue(conn:Connection, processFqn:String):scala.collection.immutable.HashMap[String, String]=
 {
    val stmt = conn.prepareStatement(allQueryProcess)
    var configInfo = scala.collection.immutable.HashMap[String,String]()
      try {
        stmt.setString(1, processFqn)
        val rs = stmt.executeQuery()        
        try {
          while(rs.next())
          {
             configInfo+=((rs.getString(1), rs.getString(2)))
          }
        }
        finally {
          rs.close()
        }
      }
      finally{
        stmt.closeOnCompletion()
      }
     configInfo
 }
}