package core.exception

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/22/12
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
class NodeTypeNotAllowedException(msg: String = "node type not allowed. (allowed types are job | value | pool | reducer") extends Exception(msg) {

}
