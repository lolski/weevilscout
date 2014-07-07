function weevil_main(niter) {
    niter = Number(niter) // 10000000
    var COMPUTE_UNIT_COUNT = 64; // 2, 4, 8, 16, 32, 64, ...
    var result = 0;
    var flopCount = 0; //TODO IMPLEMENT FLOP COUNT
    if (niter % COMPUTE_UNIT_COUNT == 0) {
        var source_str =
                    " \
            __kernel void pi_approx(__global const int *niter, __global const int *offset, __global const float *rand, __global int *count) { \
	            int id = get_global_id(0); \
	            	mwc64x_state_t rng; \
	            	ulong samplesPerStream=id/get_global_size(0); \
	            	MWC64X_SeedStreams(&rng, id, 2*samplesPerStream); \
	            count[id] = 0; \
                for (int i = 0 + offset[id]; i < offset[id] + niter[id]; ++i) { \
	                float x = convert_float(MWC64X_NextUint(&rng)) / UINT_MAX; \
	                float y = convert_float(MWC64X_NextUint (&rng)) / UINT_MAX; \
	                float z = x*x+y*y; \
	                float m = 1.0; \
	                if (z <= 1.0) count[id]++; \
	            } \
             \
            }";
        var niter_local = new Uint32Array(COMPUTE_UNIT_COUNT);
        var mod = 0;
        for (var i = 0; i < COMPUTE_UNIT_COUNT; ++i) {
            niter_local[i] = niter / COMPUTE_UNIT_COUNT;
        }
        var offset = new Uint32Array(COMPUTE_UNIT_COUNT);
        offset[0] = 0;
        offset[1] = niter_local[0];
        var rand = new Float32Array(niter * 2);
        for (var i = 0; i < niter; i++) {
            rand[i*2] = Math.random();
            rand[i*2+1] = Math.random();
        }
        var count = new Int32Array(COMPUTE_UNIT_COUNT);
        
        // Setup WebCL context using the default device of the first platform
        var platform_id = WebCL.getPlatformIDs();
        var context = WebCL.createContextFromType ([WebCL.CL_CONTEXT_PLATFORM, platform_id[0]], WebCL.CL_DEVICE_TYPE_CPU);
        var device_id = context.getContextInfo(WebCL.CL_CONTEXT_DEVICES);
        var command_queue = context.createCommandQueue(device_id[0], WebCL.CL_QUEUE_PROFILING_ENABLE);

        var program = context.createProgramWithSource(source_str);
        program.buildProgram([device_id[0]], "");

        // args
        var rand_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, niter * 2 * 4);
        command_queue.enqueueWriteBuffer(rand_mem_obj, true, 0, niter * 2 * 4, rand, []);
        var niter_local_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, COMPUTE_UNIT_COUNT * 4);
        command_queue.enqueueWriteBuffer(niter_local_mem_obj, true, 0, COMPUTE_UNIT_COUNT * 4, niter_local, []);
        var offset_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, COMPUTE_UNIT_COUNT * 4);
        command_queue.enqueueWriteBuffer(offset_mem_obj, true, 0, COMPUTE_UNIT_COUNT * 4, offset, []);
        var count_mem_obj = context.createBuffer(WebCL.CL_MEM_WRITE_ONLY, COMPUTE_UNIT_COUNT * 4);
        var kernel = program.createKernel("pi_approx");
        kernel.setKernelArg(0, niter_local_mem_obj); // 10000000 / 2
        kernel.setKernelArg(1, offset_mem_obj); // 10000000 / 2
        kernel.setKernelArg(2, rand_mem_obj);
        kernel.setKernelArg(3, count_mem_obj);

        var perf_evt;
        var global_item_size = COMPUTE_UNIT_COUNT; // compute unit
        var local_item_size = COMPUTE_UNIT_COUNT;

        // Execute (enqueue) kernel
        perf_evt = command_queue.enqueueNDRangeKernel(kernel, 1, [],
            [global_item_size], [local_item_size], []); // PERFORMANCE DONE?
        WebCL.waitForEvents([perf_evt]); // PERFORMANCE

        // Read the result buffer from OpenCL device
        command_queue.enqueueReadBuffer (count_mem_obj, true, 0, COMPUTE_UNIT_COUNT * 4, count, []);
        
        // process result from opencl
        var countReduced = 0;
        for (var i = 0; i < COMPUTE_UNIT_COUNT; ++i) {
            countReduced += count[i];
        }
        result = countReduced;
        command_queue.flush();
        command_queue.finish(); //Finish all the operations
        kernel.releaseCLResources();
        program.releaseCLResources();
        niter_local_mem_obj.releaseCLResources();
        offset_mem_obj.releaseCLResources();
        rand_mem_obj.releaseCLResources();
        count_mem_obj.releaseCLResources();
        command_queue.releaseCLResources();
        context.releaseCLResources();
    }
    else {
        result = -1;
    }
    return [[result, niter], flopCount, 0, 0];
}
