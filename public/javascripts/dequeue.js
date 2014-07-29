self.addEventListener('message', function(e){

function dequeue(){
	var id = e.data.id;
	var flops = e.data.flops;
    var isWebCLCapable = e.data.isWebCLCapable;
    var oXmlHttp = new XMLHttpRequest();
	oXmlHttp.open('GET', "/cmd/dequeue/" + id + "?webcl=" + isWebCLCapable, true);
	oXmlHttp.onreadystatechange = function(){
		if(oXmlHttp.readyState != 4) { return; }
		var txtDoc = oXmlHttp.responseText;
		var pat = /sleep/g;
		
		if(txtDoc != null){
			if(oXmlHttp.responseText.match(pat)){
				txtDoc = null;
				oXmlHttp = null;
				pat = null;
				id = null;
				var scale = 5000000;
				var timeout = scale / flops;
				setTimeout(dequeue, /*timeout **/ 10000); //
			}else{
				self.postMessage(txtDoc);
			}
		}
	};
        oXmlHttp.send(null);
}
dequeue();

}, false);
