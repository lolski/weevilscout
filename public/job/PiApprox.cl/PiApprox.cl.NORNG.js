function weevil_main(niter) {
    niter = Number(niter) // 10000000
    var COMPUTE_UNIT_COUNT = 16; // 2, 4, 8, 16, 32, 64, ...
    var CPU_COUNT = 2;
    var result = 0;
    var flopCountReduced = 0;
    if (niter % COMPUTE_UNIT_COUNT == 0) {
        var source_str =
                    " \
            ulong MWC_AddMod64(ulong a, ulong b, ulong M) \
            { \
	            ulong v=a+b; \
	            if( (v>=M) || (v<a) ) \
		            v=v-M; \
	            return v; \
            } \
             \
            ulong MWC_MulMod64(ulong a, ulong b, ulong M) \
            { \
	            ulong r=0; \
	            while(a!=0){ \
		            if(a&1) \
			            r=MWC_AddMod64(r,b,M); \
		            b=MWC_AddMod64(b,b,M); \
		            a=a>>1; \
	            } \
	            return r; \
            } \
             \
            ulong MWC_PowMod64(ulong a, ulong e, ulong M) \
            { \
	            ulong sqr=a, acc=1; \
	            while(e!=0){ \
		            if(e&1) \
			            acc=MWC_MulMod64(acc,sqr,M); \
		            sqr=MWC_MulMod64(sqr,sqr,M); \
		            e=e>>1; \
	            } \
	            return acc; \
            } \
             \
            uint2 MWC_SkipImpl_Mod64(uint2 curr, ulong A, ulong M, ulong distance) \
            { \
	            ulong m=MWC_PowMod64(A, distance, M); \
	            ulong x=curr.x*(ulong)A+curr.y; \
	            x=MWC_MulMod64(x, m, M); \
	            return (uint2)((uint)(x/A), (uint)(x%A)); \
            } \
             \
            uint2 MWC_SeedImpl_Mod64(ulong A, ulong M, uint vecSize, uint vecOffset, ulong streamBase, ulong streamGap) \
            { \
	            enum{ MWC_BASEID = 4077358422479273989UL }; \
	            \
	            ulong dist=streamBase + (get_global_id(0)*vecSize+vecOffset)*streamGap; \
	            ulong m=MWC_PowMod64(A, dist, M); \
	             \
	            ulong x=MWC_MulMod64(MWC_BASEID, m, M); \
	            return (uint2)((uint)(x/A), (uint)(x%A)); \
            } \
             \
            typedef struct{ uint x; uint c; } mwc64x_state_t; \
             \
            enum{ MWC64X_A = 4294883355U }; \
            enum{ MWC64X_M = 18446383549859758079UL }; \
             \
            void MWC64X_Step(mwc64x_state_t *s) \
            { \
	            uint X=s->x, C=s->c; \
	            \
	            uint Xn=MWC64X_A*X+C; \
	            uint carry=(uint)(Xn<C); \
	            uint Cn=mad_hi(MWC64X_A,X,carry); \
            \
	            s->x=Xn; \
	            s->c=Cn; \
            } \
             \
            void MWC64X_Skip(mwc64x_state_t *s, ulong distance) \
            { \
	            uint2 tmp=MWC_SkipImpl_Mod64((uint2)(s->x,s->c), MWC64X_A, MWC64X_M, distance); \
	            s->x=tmp.x; \
	            s->c=tmp.y; \
            } \
             \
            void MWC64X_SeedStreams(mwc64x_state_t *s, ulong baseOffset, ulong perStreamOffset) \
            { \
	            uint2 tmp=MWC_SeedImpl_Mod64(MWC64X_A, MWC64X_M, 1, 0, baseOffset, perStreamOffset); \
	            s->x=tmp.x; \
	            s->c=tmp.y; \
            } \
             \
            uint MWC64X_NextUint(mwc64x_state_t *s) \
            { \
	            uint res=s->x ^ s->c; \
	            MWC64X_Step(s); \
	            return res; \
            } \
            __kernel void pi_approx(__global const int *niter, __global int *count, __global int *flopCount) { \
	            int id = get_global_id(0); \
	            	mwc64x_state_t rng; \
	            	ulong samplesPerStream=id/get_global_size(0); \
	            	MWC64X_SeedStreams(&rng, id, 2*samplesPerStream); \
	            count[id] = 0; \
	            flopCount[id] = 0; \
                for (int i = 0; i < niter[id]; ++i) { \
                    float x = 1; \
                    float y = 1; \
	                float z = x*x+y*y; \
	                if (z <= 1.0) count[id]++; \
	                flopCount[id] += 4; \
	            } \
             \
            }";
        var niter_local = new Uint32Array(COMPUTE_UNIT_COUNT);
        var mod = 0;
        for (var i = 0; i < COMPUTE_UNIT_COUNT; ++i) {
            niter_local[i] = niter / COMPUTE_UNIT_COUNT;
        }
        var count = new Uint32Array(COMPUTE_UNIT_COUNT);
        var flopCount = new Uint32Array(COMPUTE_UNIT_COUNT);
        // Setup WebCL context using the default device of the first platform
        var platform_id = WebCL.getPlatformIDs();
        var context = WebCL.createContextFromType ([WebCL.CL_CONTEXT_PLATFORM, platform_id[0]], WebCL.CL_DEVICE_TYPE_CPU);
        var device_id = context.getContextInfo(WebCL.CL_CONTEXT_DEVICES);
        //var command_queue = context.createCommandQueue(device_id[0], WebCL.CL_QUEUE_PROFILING_ENABLE);
        var command_queue = context.createCommandQueue(device_id[0], 0);
        var program = context.createProgramWithSource(source_str);
        program.buildProgram([device_id[0]], "");

        // args
        var niter_local_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, COMPUTE_UNIT_COUNT * 4);
        command_queue.enqueueWriteBuffer(niter_local_mem_obj, true, 0, COMPUTE_UNIT_COUNT * 4, niter_local, []);
        var count_mem_obj = context.createBuffer(WebCL.CL_MEM_WRITE_ONLY, COMPUTE_UNIT_COUNT * 4);
        var flopCount_mem_obj = context.createBuffer(WebCL.CL_MEM_WRITE_ONLY, COMPUTE_UNIT_COUNT * 4);
        var kernel = program.createKernel("pi_approx");
        kernel.setKernelArg(0, niter_local_mem_obj); // 10000000 / 2
        kernel.setKernelArg(1, count_mem_obj);
        kernel.setKernelArg(2, flopCount_mem_obj);

        //var perf_evt;
        var global_item_size = COMPUTE_UNIT_COUNT; // compute unit
        var local_item_size = COMPUTE_UNIT_COUNT / CPU_COUNT;

        // Execute (enqueue) kernel
        //perf_evt = command_queue.enqueueNDRangeKernel(kernel, 1, [],
            //[global_item_size], [local_item_size], []); // PERFORMANCE DONE?
        //WebCL.waitForEvents([perf_evt]); // PERFORMANCE
        
        command_queue.enqueueNDRangeKernel(kernel, 1, [],
            [global_item_size], [local_item_size], []);
        
        // Read the result buffer from OpenCL device
        command_queue.enqueueReadBuffer(count_mem_obj, true, 0, COMPUTE_UNIT_COUNT * 4, count, []);
        command_queue.enqueueReadBuffer(flopCount_mem_obj, true, 0, COMPUTE_UNIT_COUNT * 4, flopCount, []);
        
        // process result from opencl
        var countReduced = 0;
        for (var i = 0; i < COMPUTE_UNIT_COUNT; ++i) {
            countReduced += count[i];
            flopCountReduced += flopCount[i];
        }
        result = countReduced;
        command_queue.flush();
        command_queue.finish(); //Finish all the operations
        kernel.releaseCLResources();
        program.releaseCLResources();
        niter_local_mem_obj.releaseCLResources();
        count_mem_obj.releaseCLResources();
        flopCount_mem_obj.releaseCLResources();
        command_queue.releaseCLResources();
        context.releaseCLResources();
    }
    else {
        result = -1;
    }
    return [[result, niter, flopCount], flopCountReduced, 0, 0];
}
