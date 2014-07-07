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
      function weevil_main(){var a = e.data.a;
    var sample_size = Number(a);
    var A = new Float32Array(sample_size);
    var B = new Float32Array(sample_size);
    for (var i = 0; i < sample_size; ++i) {
        A[i] = i;
        B[i] = sample_size - i;
    }
    var C = new Float32Array(sample_size);
    for (var i = 0; i < sample_size; ++i) {
        C[i] = A[i] + B[i];
    }
    self.postMessage( [0, sample_size, 0, 0]);
}
}, false);
