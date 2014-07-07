function weevil_main(niter) {
    niter = Number(niter)
    var flopCount = 0;

    var count=0;
    var x = 0.0, y = 0.0, z = 0.0;
    
    for (var i = 0; i<niter; i++) {
        x = Math.random();
        y = Math.random();
        z = x*x+y*y;
        if (z<=1) count++;
        flopCount += 4;
    }
    
    return [[count, niter], flopCount, 0, 0];
}
