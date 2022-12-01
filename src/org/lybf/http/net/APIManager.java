package org.lybf.http.net;

import org.lybf.http.api.BaseApi;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;
import org.lybf.http.utils.APIReflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class APIManager {
    private static APIManager instance;

    private HashMap<String, Object> apis = new HashMap<>();

    public <T extends BaseApi> APIManager add(String path, T api) {
        System.out.println("Add "+path);
        apis.put(api.getName(), api);
        return instance;
    }

    public static APIManager getInstance() {
        if (instance == null) instance = new APIManager();
        return instance;
    }

    public APIManager remove(String path) {
        return this;
    }

    public boolean hasAPI(String path) {
        return apis.containsKey(path);
    }

    public void invoke(String path, HttpRequest request, HttpRespond respond) throws Exception {
        if(path == null || request == null || respond == null)return;
        BaseApi api = getAPi(path);
        api.setRespond(respond);
        api.setRawHttpURL(request.getRawHttpURL());
        api.handler(request);
    }

    public BaseApi getAPi(String path) {
        if (apis.containsKey(path))
            return (BaseApi) apis.get(path);
        else
            return null;
    }

}
