package controllers

import play.api.mvc._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 1/8/13
 * Time: 4:20 AM
 * To change this template use File | Settings | File Templates.
 */
object AdminFrontend extends Controller {
  def index = Action {
    req =>
      Ok(views.html.admin_index())
  }
}
