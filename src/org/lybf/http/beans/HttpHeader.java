package org.lybf.http.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

public class HttpHeader {
    public static final String ContentType = "Content-type";

    public static final String Host = "Host";
    public static final String ContentLength = "Content-Length";

    public static final String Accept = "Accept";
    private static final String CRLF = "\r\n";

    public HashMap<String, ArrayList<String>> headers;

    public HttpHeader() {
        headers = new HashMap<>();
    }

    public HttpHeader addHeader(String key, int value) {
        addHeader(key, "" + value);
        return this;
    }

    public HttpHeader addHeader(String key, long value) {
        addHeader(key, "" + value);
        return this;
    }

    public HttpHeader addHeader(String key, float value) {
        addHeader(key, "" + value);
        return this;
    }

    public HttpHeader addHeader(String key, short value) {
        addHeader(key, "" + value);
        return this;
    }

    public HttpHeader addHeader(String key, String value) {
        ArrayList<String> values = new ArrayList<>();
        if (headers.containsKey(key)) {
            values = headers.get(key);
            for (String s : values) {
                if (!s.equals(value)) {
                    values.add(value);
                    headers.put(key, values);
                }
            }
        } else {
            values.add(value);
            headers.put(key, values);
        }
        return this;
    }

    public HttpHeader setHeader(String key, String value) {
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        headers.put(key, values);
        return this;
    }

    public HttpHeader setHeader(String key, int value) {
        return setHeader(key, "" + value);
    }

    public HttpHeader setHeader(String key, long value) {
        return setHeader(key, "" + value);
    }

    public ArrayList<String> getHeader(String key) {
        if (headers.containsKey(key)) {
            return headers.get(key);
        } else {
            return null;
        }
    }

    public HashMap<String, ArrayList<String>> getHeaders() {
        return headers;
    }

    public String toString() {
        StringJoiner result = new StringJoiner(CRLF);
        for (String key : headers.keySet()) {
            ArrayList<String> values = headers.get(key);
            StringJoiner paramters = new StringJoiner(",");
            for (String value : values) {
                paramters.add(value);
            }
            result.add(key + ": " + paramters);
        }
        return result.toString();
    }
}
