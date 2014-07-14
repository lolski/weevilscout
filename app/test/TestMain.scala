package test

import java.util.UUID

import core.workflow.WorkflowCollection

/**
 * Created by lolski on 7/12/14.
 */
class TestMain {
  def main(args: Array[String]) {
    // workflow definition XML
    val add4 =
      <workflow>
        <nodes>
          <value id="val1" value="44" returntype="Number" />
          <value id="val2" value="13" returntype="Number" />
          <job id="val3" src="Add" returntype="Number">
            <parameters>
              <parameter paramtype="Number" id="a" />
              <parameter paramtype="Number" id="b" />
            </parameters>
          </job>
          <job id="val4" src="Add" returntype="Number">
            <parameters>
              <parameter paramtype="Number" id="a" />
              <parameter paramtype="Number" id="b" />
            </parameters>
          </job>
          <value id="val5" value="100" returntype="Number" />
        </nodes>
        <edges>
          <edge id="first" source="val1" dest="val3" paramdest="a" /> <!-- 44 -->
          <edge id="second" source="val2" dest="val3" paramdest="b" /> <!-- 13 -->
          <edge id="third" source="val5" dest="val4" paramdest="a" /> <!-- 100 -->
          <edge id="fourth" source="val3" dest="val4" paramdest="b" /> <!-- 57 -->
        </edges>
        <finalnode id="val4" />
      </workflow>

    // turn into graph and start workflow
    val testUUID = UUID.randomUUID()
    WorkflowCollection.launch(testUUID, add4)
  }
}
