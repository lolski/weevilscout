/**
 * Created with JetBrains WebStorm.
 * User: lolski
 * Date: 4/29/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */


function weevil_main(a, b) {
    /*
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = ready;
    xhr.open("GET", "http://weevil/weevils/WeevilFactory-new/redundant.js", true);
    xhr.send();
    function ready() {

        if (xhr.readyState !== 4) {
            return false;
        }
        if (xhr.status !== 200) {
            // error
        }

        var str = "<weevil>";
        str += "<name>" + trim(document.getElementById("weevil_name").value) + "</name>";
        str += "<parameters>";
        str += "<a><a.0>1</a.0></a>";
        str += "<b><b.0>2</b.0></b>";
        str += "</parameters>";
        str += "<source>function weevil_main(a, b) { return a-b; }</source>";
        str += "</weevil>";
        enqueueWeevil(str);
        //submit the job 10 times
        redundant(1, 3);
        //wait for all to finish
    }
    */
    var b = 0;
    //console.log(["parent job name" + document.getElementById("weevil_name").value]);
    // can't access console and document object from here. oh because it's executed from the webworker!
    // right now the whole thing is ran using webworker. this must not be!!
    createSubjob("http://weevil/weevils/WeevilFactory-new/add.js", "");
    b = b + 10;
    return b;
}