/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/18/12
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
function WebCLWorker(script, params) {
    this.onmessage = null;
    this.onerror = null;
    this.parse = function(source, params) {
        var IDStr = "(?:_|[a-zA-Z])+[a-zA-Z0-9]*";
        var weevilParamsStr = "\\(\\s*(?:(?:\\s*" + IDStr + "\\s*,\\s*)*\\s*" + IDStr + "\\s*)*\\s*\\)";
        var weevilFuncNameStr = "function\\s+weevil_main\\s*";
        var weevilSignatureRegex = new RegExp(weevilFuncNameStr + "(" + weevilParamsStr + ")");
        var weevilSignature = source.match(weevilSignatureRegex);
        var weevilParams = weevilSignature[1];

        function replacer(match, p1, p2, off, str) {
            if (p1 != undefined && p1 != "") {
                return params[p1] + ", ";
            }
            if (p2 != undefined && p2 != "") {
                return params[p2] + ")";
            }
        }
        for (var key in params) {
            if (params.hasOwnProperty(key)) {
                var lookUp = new RegExp("(" + key + "),|(" + key + ")\\)", "g");
                weevilParams = weevilParams.replace(lookUp, replacer);
            }
        }
        var weevilMainInvoke = "weevil_main" + weevilParams + ";";
        var evalString = source + "\n" + weevilMainInvoke;
        return evalString;
    }

    var req = new HttpRequest();
    req.open("GET", script, true);
    var outer = this;
    req.onreadystatechange = function() {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var evalString = outer.parse(req.responseText, params);
                var result = eval(evalString);
                //console.log(result);
                var message = {
                    data: result,
                    type: "webcl_message"

                }
                outer.onmessage(message);
            }
            else {
                outer.onerror(message);
            }
        }
    }
    req.send();
}
