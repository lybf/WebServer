package org.lybf.http.beans;

import java.net.URLDecoder;
import java.util.HashMap;

public class RawHttpURL {
    public static final RawHttpURL OK = RawHttpURL.create(null, null, "HTTP/1.1", "200", "ok");

    public static final RawHttpURL NOT_FOUND = RawHttpURL.create(null, null, "HTTP/1.1", "404", "404 not found resource");

    private String raw;

    private String METHOD;
    private String PATH;
    private String VERSION;
    private String STATE;
    private String DESC;

    public RawHttpURL(String raw) {
        this.raw = raw;
        parse(raw);
    }

    private RawHttpURL(String method, String path, String version, String state, String desc) {
        this.METHOD = method;
        this.PATH = path;
        this.VERSION = version;
        this.STATE = state;
        this.DESC = desc;
    }

    private RawHttpURL() {

    }

    public String getRaw() {
        return raw;
    }

    public String getMethod() {
        return METHOD;
    }

    public String getFullPath() {
        return PATH;
    }

    private RawHttpURL setURL(String method, String path, String version, String state, String desc) {
        this.METHOD = method;
        this.PATH = path;
        this.VERSION = version;
        this.STATE = state;
        this.DESC = desc;
        return this;
    }

    public String getPath() {
        if (PATH != null) {
            if (hasKey()) {
                String[] s = PATH.split("\\?");
                return s[0];
            }
        }
        return PATH;
    }

    public String getDecPath() {
        return URLDecoder.decode(PATH);
    }

    public boolean hasKey() {
        return (PATH.contains("?"));
    }

    public HashMap<String, String> getKeys() {
        //System.out.println("has key = "+hasKey());
        if (hasKey()) return processKey(getDecPath().split("\\?")[1]);
        return null;
    }


    private HashMap<String, String> processKey(String keys) {
        HashMap<String, String> keys2 = new HashMap<String, String>();
        if (keys.contains("&")) {
            String[] k = keys.split("&");
            for (String k2 : k) {
                String[] s = k2.split("=");
                keys2.put(s[0], s[1]);
            }
            return keys2;
        } else {
            String[] s = keys.split("=");
            if (s.length >= 2) {
                keys2.put(s[0], s[1]);
            } else {
                keys2.put(s[0], "");

            }
        }
        return keys2;
    }

    public String getVersion() {
        return VERSION;
    }

    public String getState() {
        return STATE;
    }

    public RawHttpURL parse(String rawHttpURL) {
        if (rawHttpURL != null && hasMethod(rawHttpURL)) {
            String[] s = rawHttpURL.split(" ");
            int i = s.length;
            switch (i) {
                case 5:
                    DESC = s[4];
                case 4:
                    STATE = s[3];
                case 3:
                    VERSION = s[2];
                case 2:
                    PATH = s[1];
                case 1:
                    METHOD = s[0];
                default:
            }
        }
        return this;
    }

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String HEAD = "HEAD";
    public static final String PUT = "PUT";
    public static final String OPTIONS = "OPTIONS";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";
    public static final String CONNECT = "CONNECT";

    public static final String[] HTTP1 = {
            GET, POST, HEAD
    };
    public static final String[] HTTP11 = {
            GET, POST, HEAD, PUT, OPTIONS, DELETE, TRACE, CONNECT
    };

    private boolean hasMethod(String str) {
        for (String s : HTTP11) {
            if (str.contains(str)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String s = "";
        if (METHOD != null) s += METHOD + " ";
        if (PATH != null) s += PATH+ " ";
        if (VERSION != null) s += VERSION + " ";
        if (STATE != null) s += STATE + " ";
        if (DESC != null) s += DESC;
        return s;
    }

    public static RawHttpURL create() {
        return new RawHttpURL();
    }

    public static RawHttpURL create(String method, String path, String version, String state, String desc) {
        return new RawHttpURL(method, path, version, state, desc);
    }

}
