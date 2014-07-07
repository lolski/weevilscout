function weevil_main(a) {
    var flopCount = 0
    var count = 0;
    try {
        a = eval(a);
        var sampleSize = a[0][0][1] * a.length;
        for (var i = 0; i < a.length; ++i) {
            count += a[i][0][0]/sampleSize*4; // 4 + 1
            flopCount += 5;
        }

    }
    catch (e) {

    }
    return [count, flopCount, 0, 0];
}
