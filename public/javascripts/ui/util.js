/**
 * Created with JetBrains WebStorm.
 * User: lolski
 * Date: 9/12/12
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
function swapDivText(id,text) {
    var element = document.getElementById(id);
    if (element != null) {
        var parentNode = element.parentNode;
        parentNode.removeChild(element);
        var newDiv = document.createElement("div");
        newDiv.setAttribute("id", id);
        var newContent = document.createTextNode(text);
        newDiv.appendChild(newContent);
        parentNode.appendChild(newDiv);
    }
}