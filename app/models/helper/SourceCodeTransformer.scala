package models.helper

object SourceCodeTransformer {
  val ID = "($|_|[a-zA-Z])+[a-zA-Z0-9]*" // TODO: is $ really necessary?

  def transformJSCode(source: String, parameters: String) = {
    var newSource = source
    val main_replace = """weevil_main\s*\(\s*((\s*""" + ID + """\s*,\s*)*\s*""" + ID + """\s*)*\s*\)\s*"""
    val param_replace = """weevil_main\(\)\s*(\{)"""
    val return_replace = "return(.*);"
    newSource = main_replace.r.replaceAllIn(newSource, "weevil_main()")
    newSource = param_replace.r.replaceAllIn(newSource, m => m + parameters)
    newSource = return_replace.r.replaceAllIn(newSource, m => "self.postMessage(" + m.group(1) + ");") //PROBLEM: handle cases like when statement doenst have semicolon: ends in \n, or \s*}
    //PROBLEM: what happened on complex branching such as return inside a for loop, if elses, etc?
    val templateJS =
      """self.addEventListener('message', function(e) {
      	var data = e.data;
        switch (data.cmd) {
      	  case 'start':
      		weevil_main();
      		break;
      	case 'stop':
      		self.close();
      		break;
    	}
      """ +
        newSource +
        "}, false);"
    templateJS
  }

  def transformWebCLCode(source: String, parameters: Map[String, String]) = {
    source
  }
}
