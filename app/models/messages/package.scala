package core

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/4/12
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */
package object messages {
  def heartbeat = {
    /*
    <root>
      <geop>52.3645867:4.9341981</geop>
      <csize>1</csize>
      <flops>1.006734E8</flops>
      <jobs>
        <job>
          <id>5cbdb57d-133d-4595-895a-daabcba995e3</id>
          <status>2</status>
          <name>ab</name>
          <start>2012-10-26 21:23:46.0</start>
          <duration>1970-01-01 00:00:00.0</duration>
        </job>
      </jobs>
      <workflows>
        <workflow>
          <workflow-id></workflow-id>
          <start-node-id></start-node-id>
          <end-node-id></end-node-id>
          <jobs>
            <job>
              <id>5cbdb57d-133d-4595-895a-daabcba995e3</id>
              <status>2</status>
              <name>ab</name>
              <start>2012-10-26 21:23:46.0</start>
              <duration>1970-01-01 00:00:00.0</duration>
            </job>
            <job>
              <id>5cbdb57d-133d-4595-895a-daabcba995e3</id>
              <status>2</status>
              <name>ab</name>
              <start>2012-10-26 21:23:46.0</start>
              <duration>1970-01-01 00:00:00.0</duration>
            </job>
            <job>
              <id>5cbdb57d-133d-4595-895a-daabcba995e3</id>
              <status>2</status>
              <name>ab</name>
              <start>2012-10-26 21:23:46.0</start>
              <duration>1970-01-01 00:00:00.0</duration>

            </job>
          </jobs>
        </workflow>
      </workflows>
    </root>
    */
  }
  def enqueue = {
    /*
     * <weevil>
     * 	<name>abc</name>
     * 	<parameters>
     *   <a >
     *    <a.0>13</a.0>
     *   </a>
     *   <b >
     *    <b.0>13</b.0>
     *   </b>
     *  </parameters>
     *  <source>function weevil_main(a, b){ return a+b; }</source>
     * </weevil>
     */
  }
  def dequeue = {

  }
  def result = {

  }
}
