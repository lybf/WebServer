package org.lybf.http.beans;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HttpRespond {
    private final Socket socket;

    private OutputStream outputStream;
    private RawHttpURL firstLine = OK;

    public static final RawHttpURL OK = RawHttpURL.create("","","HTTP/1.1","200","ok");

    public static final RawHttpURL NOTFOUND =  RawHttpURL.create("","","HTTP/1.1","404","404 not found resource");

    private String CRLF = "\r\n";

    public HttpRespond(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = socket.getOutputStream();
    }

    public HttpRespond setFirstLine(RawHttpURL firstLine){
        this.firstLine = firstLine;
        return this;
    }

    public HttpRespond write(byte[] bytes,int length){
        return this;
    }

    public String toString(){

        return null;
    }
}
