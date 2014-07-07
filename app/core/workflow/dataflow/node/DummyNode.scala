package core.workflow.dataflow.node

import java.util.UUID

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */

class DummyNode(override val userId: UUID, override val parentWorkflowId: Option[UUID], override val name: String)
  extends SimpleNode(userId, parentWorkflowId, name)
