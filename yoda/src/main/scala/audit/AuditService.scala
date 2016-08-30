package audit

import config.ConfigurationService
import java.sql.DriverManager
import java.sql.Statement

/**
 * @author jpvel
 */

object AuditService {

  val globalConfig = ConfigurationService.getGlobalconfig()
  val url = globalConfig.get("audit_log_jdbc_url").get
  val driver = globalConfig.get("audit_log_jdbc_driver").get
  val user = globalConfig.get("audit_log_jdbc_user").get
  val password = globalConfig.get("audit_log_jdbc_password").get
  Class.forName(driver)

  def startAudit(processName: String, auditData: String): Integer = {
    val conn = DriverManager.getConnection(url, user, password)
    conn.setAutoCommit(false)
    val st = conn.prepareStatement("insert into process_audit (process_name, start_time, process_audit_text_start, status) values (?,now(),?,?)",Statement.RETURN_GENERATED_KEYS)
    try {
      st.setString(1, processName)
      st.setString(2, auditData)
      st.setString(3, "started")
      val rowsUpdated = st.executeUpdate()
      conn.commit();
      val rs = st.getGeneratedKeys
      rs.next
      val pk = rs.getInt(1)
      rs.close()
      pk
    } catch {
      case t: Throwable =>
        t.printStackTrace()
        0
    } finally {
      st.close();
      conn.close()
    }
  }

  def endAudit(processId: Integer, auditData: String): Unit = {
    val conn = DriverManager.getConnection(url, user, password)
    conn.setAutoCommit(false)
    val st = conn.prepareStatement("update process_audit set end_time=now(), process_audit_text_end=?, status=? where process_audit_id=?")
    try {
      st.setString(1, auditData)
      st.setString(2, "completed")
      st.setInt(3, processId)
      val rowsUpdated = st.executeUpdate()
      conn.commit();
    } catch {
      case t: Throwable =>
        t.printStackTrace()
    } finally {
      st.close();
      conn.close()
    }
  }
}