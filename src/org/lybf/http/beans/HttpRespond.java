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

    public HttpRespond setContentType(String type){
        addRespondHeader("Content-type",type);
        return this;
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
    public HttpRespond addRespondHeader(String key, int value) {
        if (infoHadWrite) throw new IllegalStateException("had connect,you can't to add any header info");
        headers.addHeader(key, value);
        return this;
    }



    public HttpRespond addRespondHeader(String key, long value) {
        addRespondHeader(key, "" + value);
        return this;
    }

    public HttpRespond addRespondHeader(String key, float value) {
        addRespondHeader(key, "" + value);
        return this;
    }

    public HttpRespond addRespondHeaderr(String key, short value) {
        addRespondHeader(key, "" + value);
        return this;
    }

    public HttpRespond setRespondHeader(String key,String value){
        headers.setHeader(key,value);
        return this;
    }
    public HttpRespond setRespondHeader(String key,int value){
        headers.setHeader(key,value);
        return this;
    }

    public HttpRespond setRespondHeader(String key,long value){
        headers.setHeader(key,value);
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
                    CRLF+"\n";

            System.out.println("info :\n"+info);
            getOutputStream().write(info.getBytes());
            infoHadWrite = true;
        }
        getOutputStream().write(bytes);
        return this;
    }


    public void flush() throws IOException {
        getOutputStream().flush();
    }

    public void disconnect() throws IOException {
        socket.shutdownOutput();
    }
    public String toString() {

        return null;
    }
}
