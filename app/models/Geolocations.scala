package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import scala.collection.mutable._
import java.sql._
import models.TimestampColumn.rowToTimestamp
import collection.mutable.{MultiMap, Set}

case class Geolocations(id: String, timestamp: String, loc: String, uagent: String, ip: String, joined: String, active: Boolean, flops: Double)

object Geolocations {
  def heartbeat(id: String, latlog: String, flops: String, uagent: String, ip: String, webcl: Boolean, returnAllRecords: Boolean) = {

    DB.withConnection {
      implicit c =>
        val loc_stmt = SQL("SELECT loc FROM geolocations where id='" + id + "'")()
        val count = loc_stmt.count(row => true)
        if (count > 0) {
          SQL("UPDATE geolocations SET loc='" + latlog + "', timestamp=CURRENT_TIMESTAMP, flops='" + flops + "' WHERE id='" + id + "'").execute()
          SQL("UPDATE geolocations SET active=false WHERE timestamp < (NOW() - INTERVAL 2 MINUTE)").execute()
        }
        else {
          val isWebClCapable =
            if (webcl == true) {
              1
            }
            else {
              0
            }
          val status = SQL("INSERT INTO geolocations (id, loc, timestamp, useragent, userip, flops, webcl) VALUES ('"
            + id + "', '" + latlog + "', CURRENT_TIMESTAMP, '" +uagent + "', '" + ip + "', " + flops + ", " + isWebClCapable + ")").execute()
        }
        val active_stmt = SQL("SELECT loc FROM geolocations WHERE active=true")

        val count_active = active_stmt().count(row => true)
        lazy val flops_stmt = SQL("SELECT SUM(flops) as total FROM geolocations WHERE active=true")
        val flops_new = flops_stmt().head[Option[Double]]("total").getOrElse("<N/A>")

        val xml = <root>
          {
            for (row <- active_stmt()) yield {
              <geop>{row[String]("loc")}</geop>
            }
          }
          <csize>{ count_active } </csize>
          <flops>{ flops_new }</flops>

          {
            val jobsByThisUser = SQL("SELECT job_id,status,name,timestamp,TIMEDIFF(endtime,starttime) AS duration FROM runqueue WHERE user_id='" + id + "' AND workflow_id='null'") // TODO: 'null' is a string! how to really denote it as null?
            for (row <- jobsByThisUser()) yield {
              <weevil>{ row[String]("job_id") + "##" + row[Long]("status") + "##" + row[String]("name") + "##" + row[Timestamp]("timestamp") + "##" + row[Timestamp]("duration") } </weevil>
            }
          }
          <workflows>
          {
            //TODO: sql needs to be refined
            val workflowByThisUser =
              if (!returnAllRecords) {
                SQL("SELECT user_id, workflow_id, job_id, status, name, timestamp FROM runqueue WHERE workflow_id != 'null'")
              }
              else {
                SQL("SELECT user_id, workflow_id, job_id, status, name, timestamp FROM runqueue WHERE user_id='" + id + "' AND workflow_id != 'null'")
              }
            val workflowMap = new HashMap[String, Set[Row]] with MultiMap[String, Row];
            for (row <- workflowByThisUser()) {
              // populate map
              val key = row[String]("workflow_id")
              workflowMap.addBinding(key, row);
            }
            for ((key, entry) <- workflowMap) yield {
              <workflow>
                <workflow-id>{key}</workflow-id>
                <start-node-id></start-node-id>
                <end-node-id></end-node-id>
                <jobs>
                  {
                    for (row <- entry) yield {
                      <job>
                        <job-id>{ row[String]("job_id") }</job-id>
                        <status>{ row[Long]("status") }</status>
                        <name>{ row[String]("name") }</name>
                        <start>{ row[Timestamp]("timestamp") } </start>
                        <duration>{ /*row[Option[Timestamp]]("duration").getOrElse("<N/A>") */ }</duration>
                      </job>
                    }
                  }
                </jobs>
              </workflow>
            }
          }
          </workflows>
        </root>
        xml
    }
  }
  def setClientStateDeactivated(userId: String) = {
    DB.withConnection {
      implicit c =>
        val setAsInactive = "UPDATE geolocations SET active=false WHERE id='" + userId + "'"
        SQL(setAsInactive).execute()
    }
  }
  def selectAllRows() = {
    DB.withConnection {
      implicit c =>
        val resultset = new ListBuffer[Geolocations]()
        val select = SQL("SELECT * FROM geolocations")
        select().foreach {
          row =>
            resultset += new Geolocations(
                row[String]("id"), row[String]("timestamp"), row[String]("loc"),
            	row[String]("uagent"), row[String]("ip"), row[String]("joined"),
            	row[Boolean]("active"), row[Double]("flops")
            )
            resultset.toList
        }
    }
  }
  def truncate = {
    DB.withConnection {
      implicit c =>
        SQL("TRUNCATE TABLE geolocations").execute()
        
    }
  }
}
