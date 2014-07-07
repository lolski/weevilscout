__kernel void Add(__global const int a, __global const int b, global__const int *ret) {
    *ret = a + b
}
