package core.worker

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 10/8/12
 * Time: 9:31 PM
 * To change this template use File | Settings | File Templates.
 */
class Worker(
  val browser: Option[String] = None,
  val availablePlugins: Map[String, Tuple2[String, Boolean]] = Map.empty[String, Tuple2[String, Boolean]],
  val location: Option[String] = None,
  var FLOPS: Double = 0.0
)
