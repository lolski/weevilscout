function weevil_main(a){
    var sum = [0, 0, 0, 0];
    try {
        a = eval(a);
        var sum = [0, a.length, 0, 0];
        for (var i = 0; i < a.length; ++i) {
            //sum[1] += a[i][1];
            //sum[2] += a[i][2];
            //sum[3] += a[i][3];
        }
    }
    catch (e) {

    }
	return sum;
}
