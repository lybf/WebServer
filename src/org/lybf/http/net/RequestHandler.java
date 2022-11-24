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
import org.lybf.http.beans.FileType;
import org.lybf.http.beans.FirstLine;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.Respond;


public class RequestHandler {

    private final HttpServer httpServer;
    private Socket socket;

    private final String CRLF = "\r\n";
    private HttpRequest request;

    public RequestHandler(HttpServer httpServer) {
        this.httpServer = httpServer;
    }


    public RequestHandler processRequest(HttpRequest request) {
        this.request = request;
        this.socket = request.getSocket();
        String method = request.getRawHttpURL().getMethod();
        if (method != null) {
            if (method.equals(FirstLine.GET)) {
                processGET();
            }
            if (method.equals(FirstLine.POST)) {

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
        Respond respond = new Respond();
        respond.setFirstLine(FirstLine.Factory.obtain(null, null, "HTTP/1.1", "200", "OK"));
        respond.addHeader("Content-length: " + file.length() + "\r\n");
        if (FileType.getContentType(file) != null) {
            respond.addHeader("Content-type: " + FileType.getContentType(file) + "\r\n");
            try {
                send(respond, file);
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
        System.out.println("GETpath = " + p);
        File file = new File(p);
        Respond respond = new Respond();

        StringBuffer sb = new StringBuffer();
        System.out.println("file exists=" + file.exists());
        if (file.exists()) {
            respond.setFirstLine(FirstLine.Factory.obtain(null, null, "HTTP/1.1", "200", "OK"));
            respond.addHeader("Content-type: " + type + ";charset=utf-8");
            respond.addHeader("\r\n" + "Content-length: " + file.length());
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
            respond.setBody(sb);
            sendText(respond);
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

        Respond respond = new Respond();
        FirstLine first = FirstLine.Factory.obtain(null, null, "HTTP/1.1", "200", "OK");
        respond.setFirstLine(first);
        System.out.println("has returnType=" + map.containsKey("returnType"));
        if (map.containsKey("returnType")) {
            if (map.get("returnType").equals("jsonp")) {
                respond.addHeader("Content-type: application/javascrpit");
                if (map.containsKey("callback")) {
                    String method = map.get("callback");
                    System.out.println("callbackMethod=" + method);
                    StringBuffer back = new StringBuffer(method + "(" + jsonArray.toString() + ")");
                    System.out.println("callback = " + back);
                    respond.setBody(back);
                }
            }
        } else {
            respond.addHeader("Content-type: application/json");
            respond.setBody(new StringBuffer().append(jsonArray.toString()));
        }
        try {
            System.out.println("send");
            sendText(respond);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * 处理GET请求参数
     */
    private HashMap<String, String> processKey(String keys) {
        HashMap<String, String> keys2 = new HashMap<String, String>();
        if (keys.contains("&")) {
            String[] k = keys.split("&");
            for (String k2 : k) {
                String[] s = k2.split("=");
                keys2.put(s[0], s[1]);
            }
        }
        return keys2;
    }

    private void cantNotFound() {
        Respond respond = new Respond();
        respond.setFirstLine(FirstLine.Factory.obtain(null, null, "HTTP/1.1", "404", "404NotFound"));
        respond.addHeader("Content-type: text/html;charset-utf-8");
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
        respond.setBody(new StringBuffer().append(notfound));
        sendText(respond);
    }

    //给客户端发送消息
    private void sendText(Respond respond) {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String info = respond.getFirstLine().toString() +
                "\r\n" +
                respond.getHeaders().toString()
                + "\r\n\r\n"
                + respond.getBody().toString();
        //  System.out.println("send content: \r\n" + info);
        try {
            out.write(info.getBytes("utf-8"));
            out.flush();
            socket.shutdownOutput();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

        System.out.println("\r\n\r\n");
    }

    private void send(Respond respond, File file) throws Exception {
        System.out.println("-------------send-----------");
        OutputStream out = socket.getOutputStream();
        out.write(respond.getFirstLine().toString().getBytes());
        out.write("\r\n".getBytes());
        //
        //write headers
        out.write((respond.getHeaders().toString()).getBytes());
        //System.out.println(respond.getHeaders().toString());
        //write body
        out.write("\r\n".getBytes());
        System.out.println("SendFile = " + file.getPath());
        byte[] bytes = new byte[1024];
        int j = -1;
        FileInputStream input = new FileInputStream(file);
        while ((j = input.read(bytes)) > 0) {
            out.write(bytes);
        }
        out.write("\r\n".getBytes());
        out.flush();
        System.out.println("--------------endsend--------------");

    }

}

