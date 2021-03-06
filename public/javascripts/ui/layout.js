function Layout() {
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    var layoutDefinition = {
        height: Dom.getClientHeight(),
        width: Dom.get('wrap').offsetWidth,
        units: [{
            position: 'top',
            height: 100,
            body: 'top1',
            gutter: '1px',
            collapse: false,
            resize: false
        },
        {
            position: 'bottom',
            height: 50,
            resize: false,
            body: 'bottom1',
            gutter: '1px',
            collapse: false
        },
        {
            position: 'center',
            body: 'center1',
            gutter: '1px',
            scroll: true
        }]
    };
    function onLayoutBeforeResize() {
        Dom.setStyle('wrap', 'height', Dom.getClientHeight() + 'px');
        Dom.setStyle('wrap', 'width', Dom.getClientWidth() + 'px');
        if (Dom.get('centerChild')) {
            var cw = Dom.get('centerChild').offsetWidth - 10;
            var ch = Dom.get('centerChild').offsetHeight - Dom.get('centerChild').firstChild.offsetHeight - 10;
            Dom.setStyle('wnetwork', 'height', ch + 'px');
            Dom.setStyle('wnetwork', 'width', cw + 'px');
            Dom.setStyle('wedit', 'height', ch + 'px');
            Dom.setStyle('wedit', 'width', cw + 'px');
            Dom.setStyle('myweevils.center', 'width', cw + 'px');
            Dom.setStyle('myweevils.center', 'height', ch + 'px');
        }
    }
    function onLayoutRender() {
        Event.onAvailable('center1', function() {
            var oCenter = document.getElementById("center1");
            var oButton = document.createElement('div');
            var oNote1 = document.createElement('div');
            var oNote2 = document.createElement('div');
            var ch = document.body.clientHeight / 2 - 200;
            var chn1 = document.body.clientHeight / 2 - 300;
            var cw = document.body.clientWidth / 2 - 100;
            var cwn2 = document.body.clientWidth / 2 + 120;
            var cwn1 = document.body.clientWidth / 2 - 650;
            oButton.id = 'joinBtn';
            oButton.style.left = cw + 'px';
            oButton.style.top = ch + 'px';
            oButton.style.width = '200px';
            oButton.style.height = '200px';
            oButton.style.position = 'absolute';
            oNote1.style.top = chn1 + 'px';
            oNote1.style.left = cwn1 + 'px';
            oNote1.style.width = '500px';
            oNote1.style.height = '370px';
            oNote1.style.position = 'absolute';
            oNote1.innerHTML = '<p class="header">What is This?</p><p class="answer">WeevilScout is an initiative to solve complex problems using a global network of browsers.</p><br/>\
					                <p class="header">How Does it Work?</p><p class="answer">When you click the start button your browser will be connected to a cluster of browsers. Once connected your browser can start computing tasks which are sent to it by WeevilScout server.</p><br/>\
                                    <p class="header">News</p><p class="answer">None</p>';
            oCenter.appendChild(oNote1);
            oNote2.style.top = chn1 + 'px';
            oNote2.style.left = cwn2 + 'px';
            oNote2.style.width = '500px';
            oNote2.style.height = '370px';
            oNote2.style.position = 'absolute';
            oNote2.innerHTML = '<p class="header">Privacy Concern?</p><p class="answer">For sake of illustrating the cluster on Google maps you will be asked to share your location this is only used to show the distribution of the cluster on a global scale. A random user id is generated when you connect to the network so no lenghty registration procedures are required.</p><br/>\
					                <p class="header">Agreement</p><p class="answer">By clicking on the START button you are joining the WeevilScout network at you OWN risk and agree that your browser and therfore your computer will be used to execute JavaScript programs sent to it. </p>';
            oCenter.appendChild(oNote2);
            oCenter.appendChild(oButton);
            Dom.setAttribute('joinBtn', 'onclick', 'onWorkerJoinBtnClick()');
            var jb = document.getElementById('joinBtn');
            jb.onmouseover = function() {
                this.style.cursor = 'pointer';
            };
            jb.innerHTML = '<img src="/assets/images/on.png"/>';
        });

        YAHOO.util.Event.addListener("workflowTab", "click", onWorkflowTabClick);
        function onWorkflowTabClick() {
            YAHOO.util.Event.removeListener("workflowTab", "click", onWorkflowTabClick);
            Event.onAvailable('workflow.editor.panel.editArea', function() {
                editAreaLoader.init({
                    id: "workflow.editor.panel.editArea",
                    start_highlight: true,
                    allow_resize: "both",
                    allow_toggle: true,
                    word_wrap: true,
                    language: "en",
                    syntax: "js"
                });
            });
        }

        YAHOO.util.Event.addListener("weditTab", "click", onWEditTabClick);
        function onWEditTabClick() {
            YAHOO.util.Event.removeListener("weditTab", "click", onWEditTabClick);
            Event.onAvailable('wedit.editor.panel.editArea', function() {
                editAreaLoader.init({
                    id: "wedit.editor.panel.editArea",
                    start_highlight: true,
                    allow_resize: "both",
                    allow_toggle: true,
                    word_wrap: true,
                    language: "en",
                    syntax: "js"
                });
            });
        }

        Event.onAvailable('wnetwork', function() {

            var layout_wnetwork = new YAHOO.widget.Layout('wnetwork', {
                width: Dom.get('centerChild').offsetWidth - 10,
                height: Dom.get('centerChild').offsetHeight - Dom.get('centerChild').firstChild.offsetHeight - 10,
                units: [{
                    position: 'bottom',
                    header: 'Console',
                    gutter: '0px',
                    height: '100px',
                    resize: true,
                    proxy: false,
                    body: 'wnetwork.bottom',
                    scroll: true,
                    gutter: '5 0 0 0',
                    collapse: true,
                    maxHeight: 500,
                    animate: true
                },
                    //{ position: 'left', gutter: '0px', width:'150px', resize: true, proxy: false, body: 'wnetwork.left', gutter: '0 5 0 0', collapse: true, animate: true },
                    {
                        position: 'center',
                        body: 'wnetwork.center',
                        width: '100%',
                        gutter: '0 0 0 0',
                        scroll: false
                    }]
            });
            layout_wnetwork.render();
            //layout_wnetwork.getUnitByPosition('left').toggle();
            layout_wnetwork.getUnitByPosition('bottom').toggle();
        }); //Event.onAvailable('wnetwork', function()
        Event.onAvailable('wedit', function() {
            var layoutWEdit = new YAHOO.widget.Layout('wedit', {
                width: Dom.get('centerChild').offsetWidth - 10,
                height: Dom.get('centerChild').offsetHeight - Dom.get('centerChild').firstChild.offsetHeight - 10,
                units: [{
                    position: 'bottom',
                    header: 'Console',
                    gutter: '0px',
                    height: '100px',
                    resize: true,
                    proxy: false,
                    body: 'wedit.bottom',
                    scroll: true,
                    gutter: '5 0 0 0',
                    collapse: true,
                    maxHeight: 500,
                    animate: true
                },
                    {
                        position: 'left',
                        header: 'Library',
                        gutter: '0px',
                        width: '200px',
                        resize: true,
                        proxy: false,
                        body: 'wedit.left',
                        gutter: '0 5 0 0',
                        collapse: true,
                        animate: true
                    },
                    {
                        position: 'center',
                        body: 'wedit.center',
                        width: '100%',
                        gutter: '0 0 0 0',
                        scroll: false
                    }]
            });
            layoutWEdit.render();
            loadWEditor();
            loadWEditLibrary();
        }); //Event.onAvailable('wedit', function()
        Event.onAvailable('workflow', function() {
            var layoutWorkflow = new YAHOO.widget.Layout('workflow', {
                width: Dom.get('centerChild').offsetWidth - 10,
                height: Dom.get('centerChild').offsetHeight - Dom.get('centerChild').firstChild.offsetHeight - 10,
                units: [{
                    position: 'bottom',
                    header: 'Console',
                    gutter: '0px',
                    height: '100px',
                    resize: true,
                    proxy: false,
                    body: 'workflow.bottom',
                    scroll: true,
                    gutter: '5 0 0 0',
                    collapse: true,
                    maxHeight: 500,
                    animate: true
                },
                {
                    position: 'left',
                    header: 'Library',
                    gutter: '0px',
                    width: '200px',
                    resize: true,
                    proxy: false,
                    body: 'workflow.left',
                    gutter: '0 5 0 0',
                    collapse: true,
                    animate: true
                },
                {
                    position: 'center',
                    body: 'workflow.center',
                    width: '100%',
                    gutter: '0 0 0 0',
                    scroll: false
                }]
            });
            layoutWorkflow.render();
            loadWorkflowEditor();
            loadWorkflowLibrary();
        });
    }
    function onAvailableWorkflowLibrary() {
        tree = new YAHOO.widget.TreeView("workflow.library");
        var xmlRes = getWorkflowList();
        var weevilList = xmlRes.getElementsByTagName("weevil");
        for (i = 0; i < weevilList.length; i++) {
            var tmpNode = new YAHOO.widget.MenuNode(weevilList[i].childNodes[0].nodeValue, tree.getRoot(), false);
        }
        tree.subscribe("labelClick", function(node) {
            var oGetScript = HttpRequest();
            var url = "/assets/workflow/" + node.label + "/" + node.label + ".xml";
            oGetScript.open('GET', url, true);
            oGetScript.onreadystatechange = function() {
                if (oGetScript.readyState != 4) {
                    return;
                }
                editAreaLoader.setValue('workflow.editor.panel.editArea', oGetScript.responseText);
            };
            oGetScript.send(null);
        });

        tree.draw();
    }
    function onAvailableWEditLibrary() {
        tree = new YAHOO.widget.TreeView("wedit.library");
        var xmlRes = getJobList();
        var weevilList = xmlRes.getElementsByTagName("weevil");
        for (i = 0; i < weevilList.length; i++) {
             var tmpNode = new YAHOO.widget.MenuNode(weevilList[i].childNodes[0].nodeValue, tree.getRoot(), false);
        }
        tree.subscribe("labelClick", function(node) {
            var oGetScript = HttpRequest();
            var url = "/assets/job/" + node.label + "/" + node.label + ".js";
            oGetScript.open('GET', url, true);
            oGetScript.onreadystatechange = function() {
                if (oGetScript.readyState != 4) {
                    return;
                }
                editAreaLoader.setValue('wedit.editor.panel.editArea', oGetScript.responseText);
            };
            oGetScript.send(null);
        });

        tree.draw();
    }
    function onAvailableWNetworkCenter() {
        var oCenter = document.getElementById('wnetwork.center');
        var gMapEl = document.createElement('div');
        gMapEl.setAttribute('id', 'content');
        gMapEl.setAttribute('class', 'googlemap');
        oCenter.appendChild(gMapEl);

        joinWeevilNetwork();

    }
    function onAvailableWNetworkLeft() {
    }
    function loadWorkflowEditor() {
        var workflowEditor = new YAHOO.util.Element('workflow.center', {
            id: 'workflow.editor'
        });

        var workflowEditorToolbar = document.createElement('div');
        workflowEditorToolbar.setAttribute('id', 'workflow.editor.toolbar');
        workflowEditorToolbar.style.height = '5%';
        var workflowEditorPanel = document.createElement('div');
        workflowEditorPanel.setAttribute('id', 'workflow.editor.panel');
        workflowEditorPanel.style.width = '98%';
        workflowEditorPanel.style.height = '87%';

        document.getElementById('workflow.editor').appendChild(workflowEditorToolbar);
        document.getElementById('workflow.editor').appendChild(workflowEditorPanel);

        var workflowInputPanel = new YAHOO.widget.Panel("workflow.inputPanel", {
            fixedcenter: true,
            width: "500px",
            visible: false,
            draggable: true,
            close: true
        });
        workflowInputPanel.setHeader("Panel #2 from Script &mdash; This Panel Is Draggable");
        workflowInputPanel.setBody('<div id="workflow.editPanel2" style="max-height: 400px; overflow:auto"></div>');
        workflowInputPanel.setFooter("End of Panel #2");
        workflowInputPanel.render(document.body);

        var workflowSubmitPanel = new YAHOO.widget.Panel("workflow.submitPanel", {
            fixedcenter: true,
            width: "500px",
            visible: false,
            draggable: true,
            close: true
        });
        workflowSubmitPanel.setHeader("Launch WorkflowCollection.scala");
        workflowSubmitPanel.setBody('<div id="workflow.submitPanel2" style="max-height: 400px; overflow:auto"></div>');
        workflowSubmitPanel.setFooter("");
        workflowSubmitPanel.render(document.body);

        var oButton2 = new YAHOO.widget.Button({
            id: "workflow.btn_2",
            type: "button",
            label: "Submit",
            container: "workflow.editor.toolbar",
            onclick: {
                fn: onSubmit
            }
        });
        Dom.setStyle('workflow.editor', 'width', '100%');
        Dom.setStyle('workflow.editor', 'height', '100%');
        Dom.setStyle('workflow.editor.panel', 'margin', '1em');
        var txtBox = document.createElement('textarea');
        txtBox.id = 'workflow.editor.panel.editArea';
        txtBox.style.width = '100%';
        txtBox.style.height = '100%';
        txtBox.innerHTML = "<!--Write your workflow here-->";

        document.getElementById('workflow.editor.panel').appendChild(txtBox);

        function onSubmit() {
            var name = "" // TODO: create name field
            var source = editAreaLoader.getValue('workflow.editor.panel.editArea');
            //var workflow = "<workflow><name>" + name + "</name><source>" + source + "</source></workflow>"
            var workflow = source
            enqueueWorkflow(workflow);
        }
        function onInputs() {
            workflowInputPanel.show();
            var cell = document.getElementById("editPanel2");
            if (!cell.hasChildNodes()) {
            }
        }

        function getXmlParameters() {
            var inputs = getFunctionParamList(editAreaLoader.getValue("wedit.editor.panel.editArea"));
            var paramStr = "<parameters>\n";
            //var regx = /[\D+]/;
            for (i = 0; i < inputs.length; i++) {
                var obk = document.getElementById("input" + inputs[i]);
                var mult = document.getElementById("mult_input" + inputs[i]);
                obk.value = trim(obk.value);
                if (obk.value) {
                    var params = obk.value.split(/\r\n|\r|\n/);
                    var paramList = "";
                    for (j = 0; j < params.length; j++) {
                        paramList = paramList + "<" + inputs[i] + "." + j + ">" + params[j] + "</" + inputs[i] + "." + j + ">\n";
                    }
                    paramStr = paramStr + "<" + inputs[i] + " >\n" + paramList + "</" + inputs[i] + ">\n";
                } else {
                    paramStr = paramStr + "<" + inputs[i] + "><" + inputs[i] + ".0>null</" + inputs[i] + ".0></" + inputs[i] + ">\n";
                }
            }
            paramStr = paramStr + "</parameters>\n";
            return paramStr;

        }

        function onRefreshInputs() {
            var cell = document.getElementById("editPanel2");
            if (cell.hasChildNodes()) {
                while (cell.childNodes.length >= 1) {
                    cell.removeChild(cell.firstChild);
                }
            }
            var inputs = getFunctionParamList(editAreaLoader.getValue("wedit.editor.panel.editArea"));

            for (i = 0; i < inputs.length; i++) {
                var oInputDivT = document.createElement('div');
                var oInputDivI = document.createElement('div');
                //oInputDivI.setAttribute('id','input'+inputs[i]);
                oInputDivT.innerHTML = "<p>Value for parameter: " + inputs[i] + "</p>";
                oInputDivI.innerHTML = '<textarea id="input' + inputs[i] + '"></textarea>';
                document.getElementById('editPanel2').appendChild(oInputDivT);
                document.getElementById('editPanel2').appendChild(oInputDivI);
            }
            var oButton3 = new YAHOO.widget.Button({
                id: "btn_4",
                type: "button",
                label: "Refresh",
                container: "editPanel2",
                onclick: {
                    fn: onRefreshInputs
                }
            });
        } //onrefreshinputs
        function onValidate() {

            removeElementById("tmp_script");
            var oHead = document.getElementsByTagName('HEAD').item(0);
            var oScript = document.createElement('script');
            oScript.setAttribute('id', 'tmp_script');
            oScript.type = "text/javascript";
            oScript.text = editAreaLoader.getValue("wedit.editor.panel.editArea");
            oHead.appendChild(oScript);
            var inputs = getFunctionParamList(editAreaLoader.getValue("wedit.editor.panel.editArea"));

            var params = [];
            var paramStr = "(";
            var regx = /[\D+]/;
            for (i = 0; i < inputs.length; i++) {
                var obk = document.getElementById("input" + inputs[i]);
                if (obk.value) {
                    if (obk.value.match(regx)) {
                        params[i] = obk.value;
                    } else {
                        params[i] = parseInt(obk.value);
                    }
                    paramStr = paramStr + obk.value + ",";
                } else {
                    params[i] = null;
                    paramStr = paramStr + "null,";
                }
            }
            paramStr = paramStr.substr(0, paramStr.length - 1);
            paramStr = paramStr + ")";

            debug("wedit.bottom", "Validating weevil_main" + paramStr);
            var result;
            try {
                result = window["weevil_main"].apply(this, params);
            } catch(err) {
                debug("wedit.bottom", "Validation Error: " + err.message);
                result = null;
            }
            if (result != null) {
                debug("wedit.bottom", "Result:" + result);
                debug("wedit.bottom", "Validation OK");
            }

        }

    }
    function loadWorkflowLibrary() {
        var workflowLibrary = new YAHOO.util.Element('workflow.left', {
            id: 'workflow.library'
        });
        Dom.setStyle('workflow.library', 'width', '100%');
        Dom.setStyle('workflow.library', 'height', '100%');
        Dom.setStyle('workflow.library', 'padding', '2px');
    }
    function loadWEditor() {
        var weevilEditor = new YAHOO.util.Element('wedit.center', {
            id: 'wedit.editor'
        });

        var weditEditorToolbar = document.createElement('div');
        weditEditorToolbar.setAttribute('id', 'wedit.editor.toolbar');
        weditEditorToolbar.style.height = '5%';
        var weditEditorPanel = document.createElement('div');
        weditEditorPanel.setAttribute('id', 'wedit.editor.panel');
        weditEditorPanel.style.width = '98%';
        weditEditorPanel.style.height = '87%';

        document.getElementById('wedit.editor').appendChild(weditEditorToolbar);
        document.getElementById('wedit.editor').appendChild(weditEditorPanel);

        var inputPanel = new YAHOO.widget.Panel("inputPanel", {
            fixedcenter: true,
            width: "500px",
            visible: false,
            draggable: true,
            close: true
        });
        inputPanel.setHeader("Panel #2 from Script &mdash; This Panel Is Draggable");
        inputPanel.setBody('<div id="editPanel2" style="max-height: 400px; overflow:auto"></div>');
        inputPanel.setFooter("End of Panel #2");
        inputPanel.render(document.body);

        var submitPanel = new YAHOO.widget.Panel("submitPanel", {
            fixedcenter: true,
            width: "500px",
            visible: false,
            draggable: true,
            close: true
        });
        submitPanel.setHeader("Submit a Weevil");
        submitPanel.setBody('<div id="submitPanel2" style="max-height: 400px; overflow:auto"></div>');
        submitPanel.setFooter("");
        submitPanel.render(document.body);

        var oButton2 = new YAHOO.widget.Button({
            id: "btn_2",
            type: "button",
            label: "Submit",
            container: "wedit.editor.toolbar",
            onclick: {
                fn: onSubmit
            }
        });
        Dom.setStyle('wedit.editor', 'width', '100%');
        Dom.setStyle('wedit.editor', 'height', '100%');
        Dom.setStyle('wedit.editor.panel', 'margin', '1em');
        var txtBox = document.createElement('textarea');
        txtBox.id = 'wedit.editor.panel.editArea';
        txtBox.style.width = '100%';
        txtBox.style.height = '100%';
        txtBox.innerHTML = "/*Write your Weevil here*/ \n\n\nfunction weevil_main(a, b) {\n	return a+b; \n}";

        document.getElementById('wedit.editor.panel').appendChild(txtBox);

        function onSubmit() {
            var cell = document.getElementById("submitPanel2");
            if (cell.hasChildNodes()) {
                while (cell.childNodes.length >= 1) {
                    cell.removeChild(cell.firstChild);
                }
            }
            var oP = document.createElement('p');
            oP.innerHTML = "Weevil Name: ";
            var oText = document.createElement('textarea');
            oText.id = "weevil_name";
            document.getElementById('submitPanel2').appendChild(oP);
            document.getElementById('submitPanel2').appendChild(oText);

            var inputs = getFunctionParamList(editAreaLoader.getValue("wedit.editor.panel.editArea"));
            for (i = 0; i < inputs.length; i++) {
                var oInputDivT = document.createElement('div');
                var oInputDivI = document.createElement('div');
                var oCheckBox = document.createElement("input");
                oCheckBox.id = 'mult_input' + inputs[i];
                oCheckBox.type = "checkbox";
                oCheckBox.value = "test";
                oCheckBox.checked = false;
                //oCheckBox.style.float = "right";
                var oCheckBoxText = document.createElement('p');
                //oCheckBoxText.style.float = "right";
                oCheckBoxText.innerHTML = "Multiple Inputs ";
                oInputDivT.innerHTML = "<p>Value for parameter: " + inputs[i] + "</p>";
                oInputDivI.innerHTML = '<textarea id="input' + inputs[i] + '"></textarea>';
                document.getElementById('submitPanel2').appendChild(oInputDivT);
                document.getElementById('submitPanel2').appendChild(oInputDivI);
            }
            document.getElementById('submitPanel2').appendChild(document.createElement('div'));
            var oButton3 = new YAHOO.widget.Button({
                id: "btn_5",
                type: "button",
                label: "Submit",
                container: "submitPanel2",
                onclick: {
                    fn: sendWeevil
                }
            });
            submitPanel.show();

            //TODO: function sendWeevil(name, params, src) {
            function sendWeevil() {
                var weevilStr = "<weevil>\n";
                weevilStr += "<workflow type=\"independent\"></workflow>";
                weevilStr = weevilStr + "<name>" + trim(document.getElementById("weevil_name").value) + "</name>\n";
                weevilStr = weevilStr + getXmlParameters();
                weevilStr = weevilStr + "<source>";
                var srcStr = editAreaLoader.getValue("wedit.editor.panel.editArea");
                srcStr = srcStr.replace(/&/g, "&amp;");
                srcStr = srcStr.replace(/</g, "&lt;");
                srcStr = srcStr.replace(/>/g, "&gt;");
                srcStr = srcStr.replace(/"/g, "&quot;");
                srcStr = srcStr.replace(/'/g, "&apos;");
                weevilStr = weevilStr + srcStr;
                weevilStr = weevilStr + "</source>\n</weevil>";
                enqueueJob(weevilStr);
                submitPanel.hide();
            }

        }
        function onInputs() {
            inputPanel.show();
            var cell = document.getElementById("editPanel2");
            if (!cell.hasChildNodes()) {
                //onRefreshInputs();
            }
        }

        //TODO:
        function getXmlParameters() {
            var inputs = getFunctionParamList(editAreaLoader.getValue("wedit.editor.panel.editArea"));
            var paramStr = "<parameters>\n";
            //var regx = /[\D+]/;
            for (i = 0; i < inputs.length; i++) {
                var obk = document.getElementById("input" + inputs[i]);
                var mult = document.getElementById("mult_input" + inputs[i]);
                obk.value = trim(obk.value);
                if (obk.value) {
                    //if(mult.checked == true){
                    var params = obk.value.split(/\r\n|\r|\n/);
                    var paramList = "";
                    for (j = 0; j < params.length; j++) {
                        paramList = paramList + "<" + inputs[i] + "." + j + ">" + params[j] + "</" + inputs[i] + "." + j + ">\n";
                    }
                    paramStr = paramStr + "<" + inputs[i] + " >\n" + paramList + "</" + inputs[i] + ">\n";
                } else {
                     paramStr = paramStr + "<" + inputs[i] + "><" + inputs[i] + ".0>null</" + inputs[i] + ".0></" + inputs[i] + ">\n";
                }
            }
            paramStr = paramStr + "</parameters>\n";
            return paramStr;

        }

        function onRefreshInputs() {
            var cell = document.getElementById("editPanel2");
            if (cell.hasChildNodes()) {
                while (cell.childNodes.length >= 1) {
                    cell.removeChild(cell.firstChild);
                }
            }
            var inputs = getFunctionParamList(editAreaLoader.getValue("wedit.editor.panel.editArea"));

            for (i = 0; i < inputs.length; i++) {
                var oInputDivT = document.createElement('div');
                var oInputDivI = document.createElement('div');
                 oInputDivT.innerHTML = "<p>Value for parameter: " + inputs[i] + "</p>";
                oInputDivI.innerHTML = '<textarea id="input' + inputs[i] + '"></textarea>';
                document.getElementById('editPanel2').appendChild(oInputDivT);
                document.getElementById('editPanel2').appendChild(oInputDivI);
            }
            var oButton3 = new YAHOO.widget.Button({
                id: "btn_4",
                type: "button",
                label: "Refresh",
                container: "editPanel2",
                onclick: {
                    fn: onRefreshInputs
                }
            });
        } //onrefreshinputs
        function onValidate() {

            removeElementById("tmp_script");
            var oHead = document.getElementsByTagName('HEAD').item(0);
            var oScript = document.createElement('script');
            oScript.setAttribute('id', 'tmp_script');
            oScript.type = "text/javascript";
            //oScript.text = txtBox.value;
            oScript.text = editAreaLoader.getValue("wedit.editor.panel.editArea");
            oHead.appendChild(oScript);
            var inputs = getFunctionParamList(editAreaLoader.getValue("wedit.editor.panel.editArea"));

            var params = [];
            var paramStr = "(";
            var regx = /[\D+]/;
            for (i = 0; i < inputs.length; i++) {
                var obk = document.getElementById("input" + inputs[i]);
                if (obk.value) {
                    if (obk.value.match(regx)) {
                        params[i] = obk.value;
                    } else {
                        params[i] = parseInt(obk.value);
                    }
                    paramStr = paramStr + obk.value + ",";
                } else {
                    params[i] = null;
                    paramStr = paramStr + "null,";
                }
            }
            paramStr = paramStr.substr(0, paramStr.length - 1);
            paramStr = paramStr + ")";

            debug("wedit.bottom", "Validating weevil_main" + paramStr);
            var result;
            try {
                result = window["weevil_main"].apply(this, params);
            } catch(err) {
                debug("wedit.bottom", "Validation Error: " + err.message);
                result = null;
            }
            if (result != null) {
                debug("wedit.bottom", "Result:" + result);
                debug("wedit.bottom", "Validation OK");
            }

        }
    }
    function loadWEditLibrary() {
        var weditLibrary = new YAHOO.util.Element('wedit.left', {
            id: 'wedit.library'
        });
        Dom.setStyle('wedit.library', 'width', '100%');
        Dom.setStyle('wedit.library', 'height', '100%');
        Dom.setStyle('wedit.library', 'padding', '2px');
    }

    this.layout = new YAHOO.widget.Layout('wrap', layoutDefinition);
    this.renderLayout = function() {
        this.layout.render();
    }

    this.layout.on('beforeResize', onLayoutBeforeResize);
    this.layout.on('render', onLayoutRender);

    Event.onAvailable('workflow.library', onAvailableWorkflowLibrary);
    Event.onAvailable("wedit.library", onAvailableWEditLibrary);
    Event.onAvailable('wnetwork.center', onAvailableWNetworkCenter);
    Event.onAvailable('wnetwork.left', onAvailableWNetworkLeft);

    Event.on(window, 'resize', this.layout.resize, this.layout, true);
}