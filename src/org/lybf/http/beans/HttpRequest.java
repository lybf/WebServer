package org.lybf.http.beans;

import java.io.*;
import java.net.Socket;

public class HttpRequest {

    public static final RawHttpURL OK = RawHttpURL.create("", "", "HTTP/1.1", "200", "ok");

    public static final RawHttpURL NOTFOUND = RawHttpURL.create("", "", "HTTP/1.1", "404", "404 not found resource");

    private static final String CRLF = "\r\n";

    private final Socket socket;

    private InputStream inputStream;
    private RawHttpURL firstLine = OK;

    private HttpHeader header;

    public HttpRequest(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        init();
    }

    private void init() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream()));
        firstLine = RawHttpURL.create().parse(br.readLine());
        header = new HttpHeader();
        String s = null;
        while ((s = br.readLine()) != null) {
            if (s.equals("")) break;
            String[] k = s.split(": ");
            header.addHeader(k[0], k[1]);
        }
    }
    public Socket getSocket(){
        return socket;
    }

    public HttpHeader getHeader(){
        return header;
    }

    public RawHttpURL getRawHttpURL() {
        return firstLine;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public HttpRequest setFirstLine(RawHttpURL firstLine) {
        this.firstLine = firstLine;
        return this;
    }


    public String toString() {
        return null;
    }
}


