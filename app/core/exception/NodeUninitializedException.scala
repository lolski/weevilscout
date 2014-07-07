package core.exception

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 10/13/12
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
class NodeUninitializedException(msg: String = "the current node has not yet been traversed") extends Exception(msg) {

}
