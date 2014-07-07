function enqueueWorkflow(workflowStr) {
    var oSendXml = HttpRequest();
    var params = "cmd=enqueueWorkflow&id=" + id + "&workflow=" + encodeURIComponent(workflowStr);
    oSendXml.open('POST', "/cmd/workflow/enqueue", false);
    oSendXml.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    oSendXml.send(params);
    debug("workflow.bottom", "[Info] Submitted workflow: " + document.getElementById("workflow_name").value);
}

function getWorkflowList() {
    var xmlReq = HttpRequest();
    var xmlStr;
    xmlReq.open("GET", "/cmd/workflow/getstore/" + id, false);
    xmlReq.send(null);
    if (xmlReq.status == 200) {
        xmlStr = stringtoXML(xmlReq.responseText);
    }
    return xmlStr;
}