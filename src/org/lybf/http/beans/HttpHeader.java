package org.lybf.http.beans;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpHeader {
    public static final String ContentType = "Content-type";
    public static final String Host = "Host";
    public static final String ContentLength = "Content-Length";
    public static final String Accept = "Accept";

    public HashMap<String, ArrayList<String>> headers;
    public HttpHeader(){
    }
}
