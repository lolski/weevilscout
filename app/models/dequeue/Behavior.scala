package models.dequeue

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._

object Behavior {
	def getOneFromTop(useragent: String, userip: String) = {
	  DB.withConnection {
		  implicit c =>
		  	  SQL("LOCK TABLES runqueue WRITE").execute()
			  val res1 = SQL("SELECT * FROM runqueue WHERE status=0 ORDER BY timestamp LIMIT 1")
			  val job = res1().head
			  val job_id = job[String]("job_id")
			  SQL("""
			          UPDATE runqueue SET status=1,starttime=CURRENT_TIMESTAMP, useragent={useragent}, userip={userip}
			        	WHERE job_id={job_id}"
			          """).on("useragent" -> useragent, "userip" -> userip, "job_id" -> job_id)
	          SQL("UNLOCK TABLES").execute()
	          job[String]("job_xml_ref")
	  }
	}
	def getRandom = 0;
	def getMostPowerful = 0;
	def getNearest = 0;
}
