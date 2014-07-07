var id = "";
var dtime = 0;
var cjobs = 0;
id = randomUUID();
var flops = 500000; // just some initial value

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
}

function success(position) {

    var lat = position.coords.latitude;
    var log = position.coords.longitude;
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
