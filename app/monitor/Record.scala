package monitor

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 12/20/12
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.UUID
import core.types.Monitor_Action_Types._

class Record(val nodeId: UUID, val time: Long, val action: Monitor_Action, var FLOPS: Double, val data: String)


