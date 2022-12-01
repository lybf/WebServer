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
     *处理GET方法
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
            } else if (path.contains("?")) {
                String[] datas = path.split("\\?");
                //process getFiles action
                if (datas[0].contains("getFiles")) processsGetFiles();

            } else if (path.contains("getFiles")) {
                processsGetFiles();
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
        System.out.println("file exists=" + file.exists());
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


    private void processsGetFiles() {
        String path = "\\";
        HashMap<String, String> map = request.getRawHttpURL().getKeys();
        if (map != null) {
            if (map.containsKey("path")) {
                path = map.get("path");
            }
        }
        System.out.println("GetFiles = " + httpServer.getDir() + "\\" + path);
        JSONArray jsonArray = new JSONArray();
        File file = new File(httpServer.getDir() + "\\" + path);
        File[] files = new File[0];
        if (map != null && map.containsKey("filter")) {
            System.out.println("filter = " + map.get("filter"));
            final String filter = map.get("filter");
            files = file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(filter);
                }
            });
        } else {
            files = file.listFiles();
        }
        for (File f : files) {
            JSONObject jobj = new JSONObject();
            jobj.put("name", f.getName());
            jobj.put("file", f.getName());
            jobj.put("path", f.getPath().replace(httpServer.getDir(), ""));
            jobj.put("lastModified", f.lastModified());
            jobj.put("isDir", f.isDirectory());
            jobj.put("size", f.length());
            jobj.put("canRead", f.canRead());
            jobj.put("date", new SimpleDateFormat("yyyy-dd-mm MM:HH:ss").format(
                    new Date(f.lastModified())));
            jsonArray.put(jobj);
        }

        respond.setFirstLine(RawHttpURL.OK);
        System.out.println("has returnType=" + map.containsKey("returnType"));
        if (map.containsKey("returnType")) {
            if (map.get("returnType").equals("jsonp")) {
                respond.addRespondHeader("Content-type", "application/javascrpit");
                if (map.containsKey("callback")) {
                    String method = map.get("callback");
                    System.out.println("callbackMethod=" + method);
                    StringBuffer back = new StringBuffer(method + "(" + jsonArray.toString() + ")");
                    System.out.println("callback = " + back);
                    respond.setRespondHeader(HttpHeader.ContentLength, back.toString().getBytes().length);

                    try {
                        respond.write(back.toString().getBytes());
                        respond.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            respond.addRespondHeader("Content-type", "application/json");

            try {
                respond.addRespondHeader(HttpHeader.ContentLength, jsonArray.toString().getBytes().length);
                respond.write(jsonArray.toString().getBytes());
                System.out.println("send " + jsonArray.toString());
                respond.flush();
                respond.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    //给客户端发送消息


    private void send(File file) throws Exception {
        System.out.println("-------------send-----------");
        System.out.println("SendFile = " + file.getPath());
        byte[] bytes = new byte[1024];
        int j = -1;
        FileInputStream input = new FileInputStream(file);
        while ((j = input.read(bytes)) > 0) {
            respond.write(bytes);
        }
        respond.flush();
        System.out.println("--------------endsend--------------");
        respond.disconnect();
    }

}

