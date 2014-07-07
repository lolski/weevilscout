package models

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 10/25/12
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */

class WorkflowRecord(val id: String, val job_start_id: String, val job_end_id: String, val status: Int) {
  def insert = 0;
  def delete = 0;
  def finish = 0;
}
