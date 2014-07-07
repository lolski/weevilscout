self.addEventListener('message', function(e){

function doCall(){
	var id = e.data.id;
	var lat = e.data.lat;
	var log = e.data.log;
	var flops = e.data.flops;
    var isWebCLCapable = e.data.isWebCLCapable;
	var oXmlHttp = new XMLHttpRequest();
    oXmlHttp.open('GET',"/cmd/heartbeat?cmd=heartbeat&id="+id+"&lat="+lat+"&log="+log+"&flops="+flops+"&webcl="+isWebCLCapable,true);
	oXmlHttp.onreadystatechange = function(){
		if(oXmlHttp.readyState != 4) { return; }
		var txtDoc = oXmlHttp.responseText;
		if(txtDoc != null){
			self.postMessage(txtDoc);
		}
	};
        oXmlHttp.send(null);
}
doCall();

}, false);
