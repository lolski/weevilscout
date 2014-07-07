function weevil_main(a){
    a = eval(a)
	var sum = 0
	for (var i = 0; i < a.length; ++i) {
		sum += a[i]
	}
	return sum;
}
