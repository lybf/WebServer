package org.lybf.http.beans;

public class Respond {
    private String REQUEST_METHOD;
    private FirstLine FIRST_LINE;

    private StringBuffer headers = new StringBuffer();

    private StringBuffer body;

    public void setBody(StringBuffer body) {
        this.body = body;
    }

    public StringBuffer getBody() {
        return body;
    }

    public void setHeaders(StringBuffer headers) {
        this.headers = headers;
    }

    public void addHeader(String header) {
        this.headers.append(header);
    }

    public FirstLine getFirstLine() {
        return FIRST_LINE;
    }

    public Respond setFirstLine(FirstLine firstLine) {
        this.FIRST_LINE = firstLine;
        return this;
    }


    public StringBuffer getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return this.FIRST_LINE.toString() + "\r\n" +
                getHeaders().toString() + "\r\n\r\n" +
                getBody() + "\r\n";
    }
}
