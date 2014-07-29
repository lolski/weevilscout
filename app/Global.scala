/**
 * Created by lolski on 7/20/14.
 */
import play.api._
import test.TestDriver

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    //TestDriver.runAll
  }

  override def onStop(app: Application) {
  }

}