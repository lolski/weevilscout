package controllers

import play.api.mvc._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 1/8/13
 * Time: 4:09 AM
 * To change this template use File | Settings | File Templates.
 */
object WorkerFrontend extends Controller {
  def index = Action {
    req =>
      Ok(views.html.worker_index())
  }
}
