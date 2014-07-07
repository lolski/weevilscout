package core.exception

/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 11/6/12
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
class ParamListUninitializedException(msg: String = "some element in the parameter list has not yet been initialized") extends Exception(msg) {

}
