package org.lybf.http.net;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.net.URLDecoder;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lybf.http.beans.*;
import org.lybf.http.utils.Progress;


public class RequestHandler {

    private final HttpServer httpServer;
    private Socket socket;

    private final String CRLF = "\r\n";
    private HttpRequest request;
    private HttpRespond respond;

    public RequestHandler(HttpServer httpServer) {
        this.httpServer = httpServer;
    }


    public RequestHandler processRequest(HttpRequest request, HttpRespond respond) {
        this.request = request;
        this.respond = respond;
        this.socket = request.getSocket();
        String method = request.getRawHttpURL().getMethod();
        if (method != null) {
            if (method.equals(RawHttpURL.GET)) {
                processGET();
            }
            if (method.equals(RawHttpURL.POST)) {

            }
        }
        return this;
    }

    /*
     *
     */
    private void processGET() {
        if (socket.isConnected()) {
            String path = null;
            try {
                path = URLDecoder.decode(request.getRawHttpURL().getPath(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            if (path.matches("^http")) {
                processHttp(path, socket);


            } else if (path.equals("/") || path.equals("")) {
                processText(httpServer.getIndexHtml(), "text/html");
            } else if (path.contains("html")) {
                processText(path, "text/html");
            } else if (path.contains("js")) {
                processText(path, "text/JavaScript");
            } else if (path.contains("css")) {
                processText(path, "text/css");
            } else {
                processFile(path);
            }
        }
    }

    private void processHttp(String path, Socket socket) {
        HttpURLConnection httpURLConnection;
        try {
            httpURLConnection = (HttpURLConnection) new URL(path).openConnection();
            for (String key : request.getHeader().getHeaders().keySet()) {
                ArrayList<String> values = request.getHeader().getHeaders().get(key);
                StringJoiner result = new StringJoiner(CRLF);
                StringJoiner paramters = new StringJoiner(",");
                for (String value : values) {
                    paramters.add(value);
                }
                httpURLConnection.addRequestProperty(key, paramters.toString());
            }
            OutputStream out = socket.getOutputStream();
            InputStream input = httpURLConnection.getInputStream();
            int j = 0;
            byte[] bytes = new byte[1024];
            while ((j = input.read(bytes)) > 0) {
                out.write(bytes);
            }
            input.close();
            httpURLConnection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void processFile(String path) {
        String p;
        String s2 = httpServer.getRes(path);
        if (s2 != null) {
            p = s2;
        } else {
            p = (httpServer.getDir() + path).replaceAll("/", "\\\\");
        }
        File file = new File(p);
        respond.setFirstLine(RawHttpURL.OK);
        respond.setRespondHeader(HttpHeader.ContentLength, "" + file.length());
        if (FileType.getContentType(file) != null) {
            respond.addRespondHeader("Content-type", FileType.getContentType(file));
            try {
                send(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            cantNotFound();
        }

    }

    /*
     *   HTML,JS,Css....
     */
    private void processText(String path, String type) {
        String p;
        String s1 = httpServer.getHtml(path);
        if (s1 != null) {
            p = s1;
        } else {
            p = (httpServer.getDir() + path).replaceAll("/", "\\\\");
        }
        System.out.println("GET path = " + p);
        File file = new File(p);

        StringBuffer sb = new StringBuffer();
        if (file.exists()) {
            respond.setFirstLine(RawHttpURL.OK);
            respond.addRespondHeader("Content-type", type + ";charset=utf-8");
            respond.setRespondHeader(HttpHeader.ContentLength, file.length());
            InputStream input = null;
            try {
                input = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String s = null;
            while (true) {
                try {
                    if (((s = br.readLine()) == null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb.append(s + System.lineSeparator());
            }
            try {
                // respond.addRespondHeader(HttpHeader.ContentLength,sb.toString().getBytes().length);

                respond.write(sb.toString().getBytes());
                respond.flush();
                respond.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cantNotFound();
        }
    }



    private void cantNotFound() {
        respond.setFirstLine(RawHttpURL.create(null, null, "HTTP/1.1", "404", "404NotFound"));
        respond.addRespondHeader("Content-type", "text/html;charset-utf-8");
        String notfound =
                "<!DOCTYPE html>\r\n" +
                        "<html>" +
                        "      <head>" +
                        "            <meta charset=utf-8/>" +
                        "            <title>404</title>" +
                        "      </head>" +
                        "      <body>" +
                        "            <center><h1>404 Could not found resources</h1></center>" +
                        "      </body>" +
                        "</html";
        try {
            respond.setRespondHeader(HttpHeader.ContentLength, notfound.getBytes().length);
            respond.write(notfound.getBytes());
            respond.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(File file) throws Exception {
        System.out.println("-------------send-----------");
        System.out.println("SendFile = " + file.getPath());
        byte[] bytes = new byte[1024];
        int j = -1;
        int le = 0;
        Progress p = new Progress(50,'#');

        FileInputStream input = new FileInputStream(file);
        while ((j = input.read(bytes)) > 0) {
            respond.write(bytes);
            le+=j;
            p.setSuffix("("+le+"/"+file.length()+")");
            p.show((int)(le/file.length())*100);
        }
        respond.flush();
        System.out.println("--------------endsend--------------");
        respond.disconnect();
    }

}

