/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/18/12
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */

function WebCL_detect() {
    // First check if the WebCL extension is installed at all
    if (window.WebCL == undefined) {
        return false;
    }

    // Get a list of available CL platforms, and another list of the
    // available devices on each platform. If there are no platforms,
    // or no available devices on any platform, then we can conclude
    // that WebCL is not available.

    try {
        var platforms = WebCL.getPlatformIDs();
        var devices = [];
        for (var i in platforms) {
            var plat = platforms[i];
            devices[i] = plat.getDeviceIDs(WebCL.CL_DEVICE_TYPE_ALL);
        }
        return true;
    } catch (e) {
        //throw "Error: Failure in inquiring platform details"
        return false;
    }
}