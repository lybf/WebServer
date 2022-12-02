package org.lybf.http.beans;

import org.lybf.http.net.HttpServer;

import java.io.*;
import java.net.Socket;

public class HttpRequest {


    private static final String CRLF = "\r\n";

    private final HttpServer httpServer;
    private final Socket socket;

    private InputStream inputStream;
    private RawHttpURL firstLine = RawHttpURL.OK;

    private HttpHeader header;

    public HttpRequest(HttpServer httpServer,Socket socket) throws IOException {
        this.httpServer = httpServer;
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

    public Socket getSocket() {
        return socket;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public HttpServer getHttpServer(){
        return httpServer;
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

    public void disconnect() throws IOException {
        socket.shutdownInput();
    }

    public String toString() {
        return firstLine.toString() + CRLF +
                header.toString() + CRLF;
    }
}


