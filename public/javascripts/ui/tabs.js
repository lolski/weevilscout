var weevilNetworkTab = new YAHOO.widget.Tab({
    label: 'Weevil Network',
    content: '<div id="wnetwork"><div id="wnetwork.center"/><div id="wnetwork.left"/><div id="wnetwork.bottom"/></div>',
    active: true
});
var workflowTab = new YAHOO.widget.Tab({
    label: 'Submit a Workflow',
    id: 'workflowTab',
    content: '<div id="workflow"><div id="workflow.center"/><div id="workflow.left"/><div id="workflow.bottom"/></div>'
});
var workflowResultsTab = new YAHOO.widget.Tab({
    label: 'Workflow Results',
    content: '<div id="workflowresults"><div id="workflowresults.center" /><div id="workflowresults.left"/><div id="workflowresults.bottom"/></div>'
});
var jobTab = new YAHOO.widget.Tab({
    label: 'Submit a Job',
    id: 'weditTab',
    content: '<div id="wedit"><div id="wedit.center"/><div id="wedit.left"/><div id="wedit.bottom"/></div>'
    //active: true
});
var jobResultsTab = new YAHOO.widget.Tab({
    label: 'Job Results',
    content: '<div id="myweevils"><div id="myweevils.center" style="overflow:auto;height:100%;width:100%"/><div id="myweevils.left"/><div id="myweevils.bottom"/></div>'
});
var monitorTab = new YAHOO.widget.Tab({
    label: 'Monitor',
    content: '<div id="monitor"><div id="monitor.center" style="overflow:auto;height:100%;width:100%">' /*+ monitor*/ +  '</div><div id="monitor.left"/><div id="monitor.bottom"/></div>'
});
var howtoTab = new YAHOO.widget.Tab({
    label: 'Quick How To',
    content: '<iframe id="howto.center" src="http://elab.lab.uvalight.net/~weevil/help/weevilhelp.html"></iframe>'
});

function onAdminJoinBtnClick() {
    function onAdminCoreLoaded() {
        var joinButtonParentElem = document.getElementById('joinBtn').parentNode;
        if (joinButtonParentElem.hasChildNodes()) {
            while (joinButtonParentElem.childNodes.length >= 1) {
                joinButtonParentElem.removeChild(joinButtonParentElem.firstChild);
            }
        }
        var centerChildElem = document.createElement('div');
        centerChildElem.id = "centerChild";
        centerChildElem.style.height = "100%";
        centerChildElem.style.width = "100%";
        joinButtonParentElem.appendChild(centerChildElem);

        var tabs = new YAHOO.widget.TabView("centerChild");

        tabs.addTab(weevilNetworkTab);
        tabs.addTab(workflowTab);
        tabs.addTab(workflowResultsTab);
        tabs.addTab(jobTab);
        tabs.addTab(jobResultsTab);
        tabs.addTab(monitorTab);

        var cw = Dom.get('centerChild').offsetWidth - 20;
        var ch = Dom.get('centerChild').offsetHeight - Dom.get('centerChild').firstChild.offsetHeight - 10;
        Dom.setStyle('tab3.center', 'width', cw + 'px');
        Dom.setStyle('tab3.center', 'height', ch + 'px');

        tabs.addTab(howtoTab);
        Dom.setStyle('tab4.center', 'width', cw + 'px');
        Dom.setStyle('tab4.center', 'height', ch + 'px');
    }
    // add admin-core.js on the fly
    var headID = document.getElementsByTagName("head")[0];
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = '/assets/javascripts/admin-core.js';
    newScript.onload = onAdminCoreLoaded
    headID.appendChild(newScript)
}

function onWorkerJoinBtnClick() {
    function onWorkerCoreLoaded() {
        var joinButtonParentElem = document.getElementById('joinBtn').parentNode;
        if (joinButtonParentElem.hasChildNodes()) {
            while (joinButtonParentElem.childNodes.length >= 1) {
                joinButtonParentElem.removeChild(joinButtonParentElem.firstChild);
            }
        }
        var centerChildElem = document.createElement('div');
        centerChildElem.id = "centerChild";
        centerChildElem.style.height = "100%";
        centerChildElem.style.width = "100%";
        joinButtonParentElem.appendChild(centerChildElem);

        var tabs = new YAHOO.widget.TabView("centerChild");

        tabs.addTab(weevilNetworkTab);
        tabs.addTab(workflowResultsTab);
        tabs.addTab(jobResultsTab);

        var cw = Dom.get('centerChild').offsetWidth - 20;
        var ch = Dom.get('centerChild').offsetHeight - Dom.get('centerChild').firstChild.offsetHeight - 10;
        Dom.setStyle('tab3.center', 'width', cw + 'px');
        Dom.setStyle('tab3.center', 'height', ch + 'px');

        tabs.addTab(howtoTab);
        Dom.setStyle('tab4.center', 'width', cw + 'px');
        Dom.setStyle('tab4.center', 'height', ch + 'px');
    }
    // add worker-core.js on the fly
    var headID = document.getElementsByTagName("head")[0];
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = '/assets/javascripts/worker-core.js';
    newScript.onload = onWorkerCoreLoaded
    headID.appendChild(newScript)
}