package core.workflow.dataflow.node

import java.util.UUID
import core.types.JS_Types._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 3:07 AM
 * To change this template use File | Settings | File Templates.
 */
class ValueDescription(override val userId: UUID, override val parentWorkflowId: Option[UUID],
                       override val name: String, val value: String, override val returntype: JS = JS_String)
  extends SimpleNode(userId, parentWorkflowId, name, returntype) with ValueContainer {
  override def getValue() = {
    value
  }
}
