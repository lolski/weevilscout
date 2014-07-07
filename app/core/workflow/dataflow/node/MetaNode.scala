package core.workflow.dataflow.node

import java.util.UUID
import core.types.JS_Types._

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/21/12
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */
class MetaNode(override val userId: UUID, override val parentWorkflowId: Option[UUID], override val name: String,
               override val returntype: JS = JS_String, var finalNode: Boolean = false)
  extends Node(userId, parentWorkflowId, name, returntype)
