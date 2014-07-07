self.addEventListener('message', function(e) {
      	var data = e.data;
        switch (data.cmd) {
      	  case 'start':
      		weevil_main();
      		break;
      	case 'stop':
      		self.close();
      		break;
    	}
      function weevil_main(){var b = e.data.b;var a = e.data.a;
	self.postMessage( a+b);
}
}, false);
