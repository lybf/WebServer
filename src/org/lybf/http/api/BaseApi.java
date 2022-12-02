package org.lybf.http.api;

import org.lybf.http.annation.API;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;
import org.lybf.http.beans.RawHttpURL;

import java.util.HashMap;

/*
 *  /base?type=&returnType=
 */
@API
public abstract class BaseApi {


    private HttpRespond respond;
    private RawHttpURL rawHttpURL;

    /*
     *  It will be invoke by Handler
     */
    public BaseApi setRespond(HttpRespond respond) {
        this.respond = respond;
        return this;
    }

    /*
     *It will be invoke by Handler
     * Hashmap:
     * type= & returnType=
     */
    public BaseApi setRawHttpURL(RawHttpURL rawHttpURL) {
        this.rawHttpURL = rawHttpURL;
        return this;
    }


    /*
     *消息处理
     */
    public abstract void handler(HttpRequest request);

    /*
     *获取参数（Hashmap）
     */
    protected HashMap<String, String> getParamters() {
        if (rawHttpURL != null) return rawHttpURL.getKeys();
        return null;
    }

    protected boolean containsKey(String key) {
        if (getParamters() != null) return getParamters().containsKey(key);
        return false;
    }

    /*
     *获取参数
     */
    protected String getValue(String key) {
        if (getParamters() != null)
            return getParamters().get(key);
        return null;
    }


    //接口名，用于识别接口
    public abstract String getName() ;

    /*
     *example:
     *public String getName() {
     *   return "/base";
     *}
     */

    public HttpRespond getRespond() {
        return respond;
    }


}
