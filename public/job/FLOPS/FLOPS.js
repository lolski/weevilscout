function weevil_main (a) {
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
    return [0, sample_size, 0, 0];
}
