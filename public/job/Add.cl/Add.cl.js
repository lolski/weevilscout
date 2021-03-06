function weevil_main(a, b) {
	var platform_id = WebCL.getPlatformIDs();
        var context = WebCL.createContextFromType ([WebCL.CL_CONTEXT_PLATFORM, platform_id[0]], WebCL.CL_DEVICE_TYPE_CPU);
        var device_id = context.getContextInfo(WebCL.CL_CONTEXT_DEVICES);
        var command_queue = context.createCommandQueue(device_id[0], WebCL.CL_QUEUE_PROFILING_ENABLE);
               
  	// convert a, b to Int?
  	var intSize = 4;
  	var elem = 1
  	var a_tarray = Uint32Array(elem);
  	var b_tarray = Uint32Array(elem);
  	var ret_tarray = Uint32Array(elem);
  	a_tarray[0] = a;
  	b_tarray[0] = b;
  	//a_tarray[1] = a+1;
  	//b_tarray[1] = b+1;
  	
  	var a_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, intSize * elem);
  	var b_mem_obj = context.createBuffer(WebCL.CL_MEM_READ_ONLY, intSize * elem);
  	var ret_mem_obj = context.createBuffer(WebCL.CL_MEM_WRITE_ONLY, intSize * elem);  	
  	
  	command_queue.enqueueWriteBuffer(a_mem_obj, true, 0, intSize * elem, a_tarray, []);
  	command_queue.enqueueWriteBuffer(b_mem_obj, true, 0, intSize * elem, b_tarray, []);
	command_queue.enqueueReadBuffer(ret_mem_obj, true, 0, intSize * elem, ret_tarray, []);
  	  	
	clKernelStr = loadWebCLKernel("Add");
	var program = context.createProgramWithSource(clKernelStr);
	program.buildProgram([device_id[0]], "");
	var kernel = program.createKernel("Add");
	
	kernel.setKernelArg(0, a_mem_obj);
	kernel.setKernelArg(1, b_mem_obj);
	kernel.setKernelArg(2, ret_mem_obj);
	
	command_queue.enqueueNDRangeKernel(kernel, 1, [], [elem], [elem], []);
	command_queue.enqueueReadBuffer(ret_mem_obj, true, 0, intSize * elem, ret_tarray, []);
	
    command_queue.flush();
    command_queue.finish(); //Finish all the operations
    kernel.releaseCLResources();
    program.releaseCLResources();
    a_mem_obj.releaseCLResources();
    b_mem_obj.releaseCLResources();
    ret_mem_obj.releaseCLResources();
    command_queue.releaseCLResources();
    context.releaseCLResources();
    return ret_tarray[0];
}
