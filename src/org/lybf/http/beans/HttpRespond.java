package org.lybf.http.beans;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HttpRespond {
    private final Socket socket;

    private OutputStream outputStream;
    private RawHttpURL firstLine = OK;

    private HttpHeader headers = new HttpHeader();


    public static final RawHttpURL OK = RawHttpURL.create("", "", "HTTP/1.1", "200", "ok");

    public static final RawHttpURL NOT_FOUND = RawHttpURL.create("", "", "HTTP/1.1", "404", "404 not found resource");

    private String CRLF = "\r\n";

    public HttpRespond(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = socket.getOutputStream();
    }

    public HttpRespond setFirstLine(RawHttpURL firstLine) {
        this.firstLine = firstLine;
        return this;
    }

    /*
    Use this method to get a outPutStream
    Example:
         OutPutStream out = httpRespond.getOutStream();
         out.write("HTTP/1.1 200 ok\r\n".getBytes());
         out.write("Content-type: text/html\r\n\r\n".getBytes());
         out.write(...........);
         ........
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /*
        set header
     */
    public HttpRespond addRespondHeader(String key, String value) {
        if (infoHadWrite) throw new IllegalStateException("had connect,you can't to add any header info");
        if (key == null) throw new NullPointerException("key is null");
        headers.addHeader(key, value);
        return this;
    }

    private boolean infoHadWrite;

    public HttpRespond write(byte[] bytes) throws IOException {
        /*
            Write headers infomations if not write
         */
        if (!infoHadWrite) {
            String header = headers.toString();
            String info = firstLine +
                    CRLF +
                    ((header.equals("") || header.toString() == null) ? "Content-type: text/plain" : header) +
                    CRLF +
                    CRLF;
            getOutputStream().write(info.getBytes());
            infoHadWrite = true;
        }
        getOutputStream().write(bytes);
        return this;
    }

    public String toString() {

        return null;
    }
}
