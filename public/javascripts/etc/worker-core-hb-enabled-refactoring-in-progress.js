var id = "";
var dtime = 0;
var cjobs = 0;
id = randomUUID();
var isWebCLCapable = WebCL_detect();
var flops = 500000; // just some initial value
var lat = ""
var log = ""

window.onbeforeunload = function() {
    if (id != "") {
        var AJs = new XMLHttpRequest();
        AJs.open("GET", "/cmd/end/" + id)
        AJs.send(null);
    }
}

function getFunctionParamList(functionText) {
    var myregx = /weevil_main[\s]*\([\w\,\s]+\)/;
    var myregx2 = /\([\w\,\s]+\)/;
    if (functionText.match(myregx)) {
        var str1 = new String(functionText.match(myregx)[0]);
        var str2 = new String(str1.match(myregx2));
        var str3 = str2.substr(1, str2.length - 2);
        var inputs = str3.split(",");
        for (i = 0; i < inputs.length; i++) {
            inputs[i] = inputs[i].replace(/^\s+|\s+$/g, '');
        }
        return inputs;
    }
    return[];
}

function joinWeevilNetwork() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(success);
    }
    var heartbeatWorker = setupHeartbeat();
    setupFlopsApprox(heartbeatWorker);

    var dequeueWorker = new Worker("/assets/javascripts/dequeue.js");
    var paramHash;
    var regxfloat = /^[-+]?\d+(\.\d+){1}$/;
    var regxint = /^[-+]?\d+$/;
    dequeueWorker.addEventListener('message', function(e) {
            var xmlDoc = stringtoXML(e.data);
            paramHash = new Object();
            var startTime;
            var stopTime;

            var script = xmlDoc.getElementsByTagName("source")[0].childNodes[0].nodeValue;
            var workflowIdNode = xmlDoc.getElementsByTagName("workflow-id")[0];
            var isJobAssociatedWithAWorkflow;
            var workflow_id = "";
            if (workflowIdNode != null) {
                isJobAssociatedWithAWorkflow = true;
                workflow_id = workflowIdNode.childNodes[0].nodeValue;
            }
            else {
                isJobAssociatedWithAWorkflow = false;
            }
            var job_id = xmlDoc.getElementsByTagName("job-id")[0].childNodes[0].nodeValue;
            var job_name = xmlDoc.getElementsByTagName("name")[0].childNodes[0].nodeValue;
            var job_type = xmlDoc.getElementsByTagName("jobtype")[0].childNodes[0].nodeValue;
            debug("wnetwork.bottom", "[Info] Starting job: " + job_name);
            var params = xmlDoc.getElementsByTagName("parameters")[0].childNodes;

            for (k = 0; k < params.length; k++) {
                for (l = 0; l < params[k].childNodes.length; ++l) {
                    if (!params[k].childNodes[l].nodeName.match("#text")) {
                        if (params[k].childNodes[l].firstChild.nodeValue.match(regxint)) {
                            paramHash[params[k].nodeName] = parseInt(params[k].childNodes[l].firstChild.nodeValue);
                        }
                        else if (params[k].childNodes[l].firstChild.nodeValue.match(regxfloat)) {
                            paramHash[params[k].nodeName] = parseFloat(params[k].childNodes[l].firstChild.nodeValue);
                        } else {
                            paramHash[params[k].nodeName] = (params[k].childNodes[l].firstChild.nodeValue != "null") ? params[k].childNodes[l].firstChild.nodeValue : null;
                        }
                    }
                }
            }

            if (job_type == "webcl") {
                // TODO: start timer
                var start = 0;
                if (window.performance.now) {
                    start = window.performance.now();
                }
                var fgworker = new WebCLWorker(script, paramHash);
                function fgworker_onerror(e) {
                    var job_name = e.filename.substring(e.filename.lastIndexOf("/") + 1, e.filename.length - 3);
                    debug("wnetwork.bottom", "[Error] In " + job_name);

                    var sdata;
                    var AJ = new HttpRequest();
                    if (isJobAssociatedWithAWorkflow) {
                        sdata = "cmd=submit&workflow_id=" + encodeURIComponent(workflow_id) + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=3&result=" + encodeURIComponent("[Error] " + e.message);
                        AJ.open("POST", "/cmd/submit", false);
                    }
                    else {
                        sdata = "cmd=submit&wnoorkflow=1" + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=3&result=" + encodeURIComponent("[Error] " + e.message);
                        AJ.open("POST", "/cmd/submit_noworkflow", false);
                    }
                    AJ.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                    AJ.send(sdata);
                    // TODO: stop timer
                    dequeueWorker.postMessage({
                        'id': id,
                        'isWebCLCapable': isWebCLCapable
                    });
                    thread.postMessage({
                        'cmd': 'stop'
                    });
                    stopTime = new Date();
                    var tdiff = Math.abs(stopTime - startTime);
                    dtime = dtime + tdiff;
                    swapDivText("tstate","Idling");
                    swapDivText("dtime",(dtime / 1000)+"s");
                }
                function fgworker_onmessage(e) {
                    var sdata;
                    var AJ = new HttpRequest();
                    debug("wnetwork.bottom", "[Info] Completed job: " + job_name);
                    // MONITOR CODE
                    var end = 0;
                    if (window.performance.now) {
                        end = window.performance.now();
                    }
                    var execTime = end - start;
                    var flopCount = e.data[1];
                    var flopPerSec = flopCount * (1e3 / execTime) / 1e6;
                    e.data[2] = execTime;
                    e.data[3] = flopPerSec;
                    // MONITOR CODE END
                    if (isJobAssociatedWithAWorkflow) {
                        sdata = "cmd=submit&workflow_id=" + encodeURIComponent(workflow_id) + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=2&result=" + encodeURIComponent(JSON.stringify(e.data));
                        AJ.open("POST", "/cmd/submit", false)
                    }
                    else {
                        sdata = "cmd=submit&noworkflow=1" + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=2&result=" + encodeURIComponent(JSON.stringify(e.data));
                        AJ.open("POST", "/cmd/submit_noworkflow", false)
                    }
                    AJ.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                    debug("wnetwork.bottom", "[Info] Submitted results for workflow_id | job_id | job_name: " + workflow_id + " | " + job_id + " | " + job_name);
                    AJ.send(sdata);
                    // TODO: stop timer
                    dequeueWorker.postMessage({
                        'id': id,
                        'isWebCLCapable': isWebCLCapable
                    });
                    stopTime = new Date();
                    var tdiff = Math.abs(stopTime - startTime);
                    dtime = dtime + tdiff;
                    cjobs = cjobs + 1;
                    swapDivText("tstate","Idling");
                    swapDivText("cjobs",cjobs);
                    swapDivText("dtime",(dtime / 1000)+"s");
                }
                fgworker.onmessage = fgworker_onmessage;
                fgworker.onerror = fgworker_onerror;
            }
            else if (job_type == "javascript") {
                // TODO: start timer
                var start = 0;
                if (window.performance.now) {
                    start = window.performance.now();
                }
                var thread = new Worker(script);
                function thread_onerror(e) {
                    var job_name = e.filename.substring(e.filename.lastIndexOf("/") + 1, e.filename.length - 3);
                    debug("wnetwork.bottom", "[Error] In " + job_name);

                    var sdata;
                    var AJ = new HttpRequest();
                    if (isJobAssociatedWithAWorkflow) {
                        sdata = "cmd=submit&workflow_id=" + encodeURIComponent(workflow_id) + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=3&result=" + encodeURIComponent("[Error] " + e.message);
                        AJ.open("POST", "/cmd/submit", false);
                    }
                    else {
                        sdata = "cmd=submit&wnoorkflow=1" + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=3&result=" + encodeURIComponent("[Error] " + e.message);
                        AJ.open("POST", "/cmd/submit_noworkflow", false);
                    }
                    AJ.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                    AJ.send(sdata);
                    // TODO: stop timer
                    dequeueWorker.postMessage({
                        'id': id,
                        'isWebCLCapable': isWebCLCapable
                    });
                    thread.postMessage({
                        'cmd': 'stop'
                    });
                    stopTime = new Date();
                    var tdiff = Math.abs(stopTime - startTime);
                    dtime = dtime + tdiff;
                    swapDivText("tstate","Idling");
                    swapDivText("dtime",(dtime / 1000)+"s");
                }
                function thread_onmessage(e) {
                    var sdata;
                    var AJ = new HttpRequest();
                    debug("wnetwork.bottom", "[Info] Completed job: " + job_name);
                    // MONITOR CODE
                    var end = 0;
                    if (window.performance.now) {
                        end = window.performance.now();
                    }
                    var execTime = end - start;
                    var flopCount = e.data[1];
                    var flopPerSec = flopCount * (1e3 / execTime) / 1e6;
                    e.data[2] = execTime;
                    e.data[3] = flopPerSec;
                    // MONITOR CODE END
                    if (isJobAssociatedWithAWorkflow) {
                        sdata = "cmd=submit&workflow_id=" + encodeURIComponent(workflow_id) + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=2&result=" + encodeURIComponent(JSON.stringify(e.data));
                        AJ.open("POST", "/cmd/submit", false)
                    }
                    else {
                        sdata = "cmd=submit&noworkflow=1" + "&job_id=" + encodeURIComponent(job_id) + "&job_name=" + job_name + "&id=" + encodeURIComponent(id) + "&status=2&result=" + encodeURIComponent(JSON.stringify(e.data));
                        AJ.open("POST", "/cmd/submit_noworkflow", false)
                    }
                    AJ.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                    debug("wnetwork.bottom", "[Info] Submitted results for workflow_id | job_id | job_name: " + workflow_id + " | " + job_id + " | " + job_name);
                    AJ.send(sdata);

                    // TODO: stop timer
                    dequeueWorker.postMessage({
                        'id': id,
                        'isWebCLCapable': isWebCLCapable
                    });
                    thread.postMessage({
                        'cmd': 'stop'
                    });
                    stopTime = new Date();
                    var tdiff = Math.abs(stopTime - startTime);
                    dtime = dtime + tdiff;
                    cjobs = cjobs + 1;
                    swapDivText("tstate","Idling");
                    swapDivText("cjobs",cjobs);
                    swapDivText("dtime",(dtime / 1000)+"s");
                }
                thread.onerror = thread_onerror;
                thread.onmessage = thread_onmessage;
                paramHash["cmd"] = "start";
                startTime = new Date();
                thread.postMessage(paramHash);
                swapDivText("tstate","Working");
            }
        },
        false);
    dequeueWorker.postMessage({
        'id': id,
        'flops': flops,
        'isWebCLCapable': isWebCLCapable
    });
}

function success(position) {
    lat = position.coords.latitude;
    log = position.coords.longitude;
    var markers = [];
    var malta = new google.maps.LatLng(35, 14);
    var myOptions = {
        zoom: 3,
        center: malta,
        mapTypeControl: false,
        navigationControlOptions: {
            style: google.maps.NavigationControlStyle.SMALL
        },
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var mapi = new google.maps.Map(document.getElementById("content"), myOptions);
    var mcOptions = {
        gridSize: 30,
        maxZoom: 13
    };
    var mc = null;

    debug("wnetwork.bottom", "[Info] user id: " + id);

}

function setupHeartbeat() {

    var heartbeat = new Worker("/assets/javascripts/heartbeat.js");
    var gInterval = setInterval(hb, 3000); //TODO: adjust


    heartbeat.addEventListener('message', function(e) {
            var myColumnDefs = [{
                key: "id",
                sortable: true,
                resizeable: true
            },
                {
                    key: "name",
                    sortable: true,
                    resizeable: true
                },
                {
                    key: "status",
                    sortable: true,
                    resizeable: true
                },
                {
                    key: "queued time",
                    formatter: YAHOO.widget.DataTable.formatDate,
                    sortable: true,
                    sortOptions: {
                        defaultDir: YAHOO.widget.DataTable.CLASS_DESC
                    },
                    resizeable: true
                },
                {
                    key: "duration",
                    sortable: true,
                    resizeable: true
                },
                {
                    key: "results",
                    sortable: false,
                    resizeable: true
                }
                //{key:"executed by"	, sortable:true, resizeable:true}
            ];
            var xmlDoc = stringtoXML(e.data);
            // Job result
            var weevils = xmlDoc.getElementsByTagName("weevil");
            if (weevils.length > 0) {
                (function() {
                    var dataSource = new Array();
                    for (k = 0; k < weevils.length; k++) {
                        var data = weevils[k].childNodes[0].nodeValue.split("##");
                        var wState = "undefined";
                        if (data[1] == 0) wState = "queued";
                        if (data[1] == 1) wState = "running";
                        if (data[1] == 2) wState = "complete";
                        if (data[1] == 3) wState = "error";

                        var o = {};
                        o["id"] = "<a href=/assets/job_running/" + data[0] + ".xml target='_blank'>" + data[0] + "</a>";
                        o["name"] = "<a href=/assets/job_running/" + data[2] + ".js target='_blank'>" + data[2] + "</a>";
                        o["status"] = wState;
                        o["queued time"] = data[3];
                        o["duration"] = data[4];
                        o["results"] = "<a href=/assets/results/" + data[2] + "/" + data[0] + "_OUT.html target='_blank'>results</a>";
                        dataSource.push(o);
                    }
                    var myDataSource = new YAHOO.util.DataSource(dataSource);
                    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                    myDataSource.responseSchema = {
                        fields: ["id", "name", "status", "queued time", "duration", "results"]
                    };
                    var myDataTable = new YAHOO.widget.DataTable("myweevils.center", myColumnDefs, myDataSource, {
                        caption: "Weevil Stats"
                    });

                })();
            }
            // workflow result
            var workflow = xmlDoc.getElementsByTagName("workflow");
            var parent = document.getElementById("workflowresults.center");
            parent.innerHTML = "";

            for (var i = 0; i < workflow.length; ++i) {
                var workflowId = workflow[i].getElementsByTagName("workflow-id")[0].textContent;
                var nodeStartId, nodeEndId;
                var jobs = workflow[i].getElementsByTagName("job");
                var dataSource = new Array();
                for (var j = 0; j < jobs.length; ++j) {
                    var job = jobs[j];
                    var entry = {};
                    entry["id"] = job.getElementsByTagName("job-id")[0].textContent;
                    entry["status"] = job.getElementsByTagName("status")[0].textContent;
                    entry["name"] = job.getElementsByTagName("name")[0].textContent;
                    entry["start"] = job.getElementsByTagName("start")[0].textContent;
                    entry["duration"] = job.getElementsByTagName("duration")[0].textContent;
                    entry["results"] = "<a href='/assets/workflow_results/" + workflowId + "/" + entry["id"] + "_OUT.html' target='_blank'a>results</a>";
                    dataSource.push(entry);
                }

                var workflowResultsTableContainer = document.createElement("div");
                parent.appendChild(workflowResultsTableContainer);
                var title = '<a href="/assets/workflow_results/' + workflowId + '/workflow.png" target="_blank">' + workflowId + '</a>'
                var workflowResultsTable = new WorkflowResultsTabularView(title);
                workflowResultsTable.setDataSource(dataSource);
                workflowResultsTable.displayTable(workflowResultsTableContainer);

            }
            var csize = xmlDoc.getElementsByTagName("csize")[0].childNodes[0].nodeValue;
            swapDivText("csize", csize);
            var bogoflops = xmlDoc.getElementsByTagName("flops")[0].childNodes[0].nodeValue;
            swapDivText("flops", bogoflops);

            (function() {
                var geops = xmlDoc.getElementsByTagName("geop");
                var markers = new Array();

                for (i = 0; i < geops.length; i++) {
                    var loc = geops[i].childNodes[0].nodeValue.split(":");
                    var latlng = new google.maps.LatLng(loc[0], loc[1]);
                    var marker = new google.maps.Marker({
                        position: latlng,
                        title: loc[2]
                    });
                    markers.push(marker);
                }
                if (mc != null) {
                    mc.clearMarkers();
                    mc.addMarkers(markers);
                } else {
                    mc = new MarkerClusterer(mapi, markers, mcOptions);
                }
            })();

            //setTimeout(hb,2000);
            xmlDoc = null;

        },
        false);

    function hb() {
        heartbeat.postMessage({
            'id': id,
            'lat': lat,
            'log': log,
            'flops': avgFlops,
            'isWebCLCapable': isWebCLCapable
        });
    }
    return heartbeat;
}

function setupFlopsApprox(heartbeat) {
    var flopsThread = new Worker("assets/javascripts/flops-calculate.js");
    var flopsItr = 0;
    var flopsAry = new Array();
    var avgFlops = 0;
    flopsThread.addEventListener("message", function(e) {
            flopsAry[flopsItr] = e.data.flops;
            flopsItr++;
            if (flopsItr < 3) {
                flopsThread.postMessage({
                    'cmd': 'start'
                });
            } else {
                flopsThread.postMessage({
                    'cmd': 'stop'
                });
                avgFlops = (flopsAry[0] + flopsAry[1] + flopsAry[2]) / 3
                heartbeat.postMessage({
                    'id': id,
                    'lat': lat,
                    'log': log,
                    'flops': avgFlops,
                    'isWebCLCapable': isWebCLCapable
                });
                flops = avgFlops;
                var mflops = Math.round(avgFlops / 1000000);
                debug("wnetwork.bottom", "[Info] bogoflops: " + mflops + " Mflops");
            }

        },
        false);
    flopsThread.postMessage({
        'cmd': 'start'
    });
}