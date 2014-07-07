package config

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/6/12
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
import scala.concurrent.duration._
import akka.util.Timeout

object Config {
  val actorCreationTimeout = Timeout(7 seconds)
  val retryInCaseResultNotReadyFrequency = 5 seconds

}
