function enqueueJob(weevilStr) {
    var oSendXml = HttpRequest();
    var params = "cmd=enqueue&id=" + id + "&weevil=" + encodeURIComponent(weevilStr);
    oSendXml.open('POST', "/cmd/enqueue", false);
    oSendXml.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    oSendXml.send(params);
    debug("wedit.bottom", "[Info] Submitted job: " + document.getElementById("weevil_name").value);
}

function getJobList() {
    var xmlReq = HttpRequest();
    var xmlStr;
    xmlReq.open("GET", "/cmd/getstore/" + id, false);
    xmlReq.send(null);
    if (xmlReq.status == 200) {
        xmlStr = stringtoXML(xmlReq.responseText);
    }
    return xmlStr;
}