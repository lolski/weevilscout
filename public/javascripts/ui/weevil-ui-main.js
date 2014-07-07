var Dom = YAHOO.util.Dom;
var Event = YAHOO.util.Event;

Event.onDOMReady(function() {
if(BrowserDetect.browser == "Explorer"){
    swapDivText("center1.inner", "This Browser Does not support HTML5!! Known supported browsers are latest releases of Chrome, Firefox and Opera.");
}else{
    var layout = new Layout();
    layout.renderLayout();
}//else

}); //onDOMReady
