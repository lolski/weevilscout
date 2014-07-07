function weevil_main (a) {
    var sample_size = Number(a);

    var A = new Float32Array(sample_size);
    var B = new Float32Array(sample_size);

    for (var i = 0; i < sample_size; ++i) {
        A[i] = i;
        B[i] = sample_size - i;
    }

    var source_str =
        "__kernel void vector_add(__global const float *A, __global const float *B, __global float *C) {" +
            "int i = get_global_id(0);" +
            "C[i] = A[i] + B[i];" +
            "}";
    var source_size = source_str.length;

    // Setup WebCL context using the default device of the first platform
    var platform_id = WebCL.getPlatformIDs();
    var context = WebCL.createContextFromType ([WebCL.CL_CONTEXT_PLATFORM, platform_id[0]], WebCL.CL_DEVICE_TYPE_CPU);
    var device_id = context.getContextInfo(WebCL.CL_CONTEXT_DEVICES);
    var command_queue = context.createCommandQueue(device_id[0], WebCL.CL_QUEUE_PROFILING_ENABLE);
    // Reserve buffers
    var a_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, sample_size * 4);
    var b_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, sample_size * 4);
    var c_mem_obj = context.createBuffer (WebCL.CL_MEM_WRITE_ONLY, sample_size * 4);

    // Write the buffer to OpenCL device memory
    command_queue.enqueueWriteBuffer(a_mem_obj, true, 0, sample_size * 4, A, []);
    command_queue.enqueueWriteBuffer(b_mem_obj, true, 0, sample_size * 4, B, []);

    // ...
    var program = context.createProgramWithSource(source_str);
    program.buildProgram([device_id[0]], "");

    var kernel = program.createKernel("vector_add");
    kernel.setKernelArg(0, a_mem_obj);
    kernel.setKernelArg(1, b_mem_obj);
    kernel.setKernelArg(2, c_mem_obj);

    var perf_evt;
    var global_item_size = sample_size;
    var local_item_size = 64;

    // Execute (enqueue) kernel
    perf_evt = command_queue.enqueueNDRangeKernel(kernel, 1, [],
        [global_item_size], [local_item_size], []); // PERFORMANCE DONE?
    WebCL.waitForEvents([perf_evt]); // PERFORMANCE

    // Read the result buffer from OpenCL device
    var C = Float32Array(sample_size);
    command_queue.enqueueReadBuffer (c_mem_obj, true, 0, sample_size * 4, C, []);

    command_queue.flush();
    command_queue.finish(); //Finish all the operations
    kernel.releaseCLResources();
    program.releaseCLResources();
    a_mem_obj.releaseCLResources();
    b_mem_obj.releaseCLResources();
    c_mem_obj.releaseCLResources();
    command_queue.releaseCLResources();
    context.releaseCLResources();

    return [0, sample_size, 0, 0];
}
