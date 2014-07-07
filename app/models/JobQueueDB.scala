package models

import scala.io._
import java.io._
import java.util.UUID
import play.api.db._
import play.api.Play.current
import play.api.mvc._
import anorm._
import helper._
import core.types.JOB_Types._

object JobQueueDB {
  val column_name = List(
      "job_id", "user_id", "job_xml_ref", 
      "status", "name", "validation", 
      "timestamp", "endtime", "starttime", 
      "useragent", "userip", "workflow_id"
      )

  def enqueueJob(userId: UUID, workflowId: UUID = null, jobId: UUID = null, jobDescription: scala.xml.Node) {
    val name = (jobDescription \ "name")(0).text
    val jobtype =
      if ((jobDescription \ "jobtype")(0).text == "javascript") {
        JOB_Javascript
      }
      else if ((jobDescription \ "jobtype")(0).text == "webcl") {
        JOB_WebCL
      }
      else {
        JOB_Invalid
      }
    val isAFinalNode =
      if ((jobDescription \ "finalnode")(0).text == "true") {
        true
      }
      else {
        false
      }

    insertRecord(userId, workflowId, jobId, name, isAFinalNode, jobtype)
    fileio.writeJobInstance(workflowId, jobId, jobDescription)
  }
  def insertRecord(userId: UUID, workflowId: UUID = null, jobId: UUID = null, name: String, finalNode: Boolean, jobtype: JOB) = {
    val job_id =
      if (jobId == null) {
        UUID.randomUUID().toString
      }
      else {
        jobId.toString
      }
    val user_id = userId.toString
    val workflow_id = workflowId
    val isAFinalNode =
      if (finalNode == true) 1
      else 0

    DB.withConnection {
      implicit c =>
        SQL("INSERT INTO runqueue (job_id, workflow_id, user_id, job_xml_ref, name, status, validation, webcl, final)" +
          "VALUES ('" + job_id + "', '" + workflow_id + "', '" + user_id + "', '" +
          job_id + ".xml', '" + name + "', 0, 1, " + jobTypeStringToColumn(jobTypeToString(jobtype)) +
          ", " + isAFinalNode + ")").execute()

    }
  }
  def dequeueJob(userAgent: String, userIpAddr: String, webcl: Boolean) = {
    val conn = DB.getConnection()
    SQL("LOCK TABLES runqueue WRITE").execute()(conn)
    val res1 =
      if (webcl == true) {
        SQL("SELECT * FROM runqueue WHERE status=0 LIMIT 1")
      }
    else {
        SQL("SELECT * FROM runqueue WHERE status=0 AND webcl=0 LIMIT 1")
      }

    var xml_out = <root></root>
    try {
      val job = res1()(conn).head
      val job_id = job[String]("job_id")
      SQL(
        "UPDATE runqueue " +
          "SET status=1, starttime=CURRENT_TIMESTAMP, useragent='" + userAgent + "', userip='" + userIpAddr + "'" +
          "WHERE job_id='" + job_id + "'"
      ).execute()(conn)

      SQL("UNLOCK TABLES").execute()(conn)
      conn.close()
      val job_xml_ref = job[String]("job_xml_ref")
      val workflowId = job[String]("workflow_id")
      val jobDir = util.getWorkflowResultDirectory(UUID.fromString(workflowId)) + "/"
      val fh = Source.fromFile(jobDir + job_xml_ref)
      xml_out = scala.xml.XML.loadString(fh.mkString)
    }
    catch {
      case empty: NoSuchElementException =>
        SQL("UNLOCK TABLES").execute()(conn)
        conn.close()
        xml_out = <sleep>10</sleep>
      case badpath: FileNotFoundException =>
        conn.close()
        badpath.printStackTrace()
    }
    xml_out
  }
  def submitJobWithNoWorkflow(jobId: String, status: Int) = {
    //TODO: put in JobQueueDB
    DB.withConnection {
      implicit c =>
        SQL("UPDATE runqueue SET status=2 WHERE job_id='" + jobId + "'").execute()
        if (status == 2) {
          SQL("UPDATE runqueue SET status=2, endtime=CURRENT_TIMESTAMP WHERE job_id='" + jobId + "'").execute()
        }
        if (status == 3) {
          SQL("UPDATE runqueue SET status=2 WHERE, endtime=CURRENT_TIMESTAMP job_id='" + jobId + "'").execute()
        }
    }
  }
  def submitJobInAWorkflow(workflowId: String, jobId: String, status: Int) = {
    DB.withConnection {
      implicit c =>
        SQL("UPDATE runqueue SET status=2 WHERE job_id='" + jobId + "' AND workflow_id='" + workflowId + "'").execute() // TODO: check

        if (status == 2) {
          SQL("UPDATE runqueue SET status=2, endtime=CURRENT_TIMESTAMP WHERE job_id='" + jobId + "'").execute()
        }
        if (status == 3) {
          SQL("UPDATE runqueue SET status=2 WHERE, endtime=CURRENT_TIMESTAMP job_id='" + jobId + "'").execute()
        }
    }
  }
  def end(userId: String) = {
    /*
    DB.withConnection {
      implicit c =>
        SQL("DELETE FROM runqueue WHERE user_id='" + userId + "'").execute
    }*/
  }
  def truncate = {
    DB.withConnection {
      implicit c =>
        SQL("TRUNCATE TABLE runqueue").execute()
        
    }
  }
}

