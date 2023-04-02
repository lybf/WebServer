package org.lybf.http.net;

import org.lybf.http.api.BaseApi;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;

import java.util.HashMap;
import java.util.Iterator;

public class APIManager {
    private static APIManager instance;

    private HashMap<String, Object> apis = new HashMap<>();

    public <T extends BaseApi> APIManager add(String path, T api) {
        apis.put(api.getName(), api);
        return instance;
    }

    public static APIManager getInstance() {
        if (instance == null) instance = new APIManager();
        return instance;
    }

    public APIManager remove(String path) {
        if(hasAPI(path)){
            apis.remove(path);
        }
        return this;
    }

    public boolean hasAPI(String path) {
        if (apis.containsKey(path))
            return true;
        Iterator<Object> api = apis.values().iterator();
        while (api.hasNext()) {
            BaseApi a = (BaseApi) api.next();
            if (path.contains(a.getName())) {
                return true;
            }
        }
        return false;
    }

    public void invoke(String path, HttpRequest request, HttpRespond respond) throws Exception {
        if (path == null || request == null || respond == null) return;
        BaseApi api = getAPi(path);
        api.setRespond(respond);
        api.setRawHttpURL(request.getRawHttpURL());
        api.handler(request);
    }

    public BaseApi getAPi(String path) {
        if (apis.containsKey(path)) {
            return (BaseApi) apis.get(path);
        } else {
            Iterator<Object> api = apis.values().iterator();
            while (api.hasNext()) {
                BaseApi a = (BaseApi) api.next();
                 if (path.contains(a.getName())) {
                    return a;
                }
            }
            return null;
        }
    }

}
