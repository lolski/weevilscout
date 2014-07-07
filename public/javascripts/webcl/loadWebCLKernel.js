/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/27/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
function loadWebCLKernel(src) {
    var source =
        "__kernel void Add(__global const int *A, __global const int *B, __global int *C) {\n" +
            "	// Get the index of the current element to be processed\n" +
            "	int i = get_global_id(0);\n" +
            "	// Do the operation\n" +
            "	C[i] = A[i] + B[i];\n" +
            "}\n";
    return source;
}