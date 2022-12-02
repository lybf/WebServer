package apis;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lybf.http.api.BaseApi;
import org.lybf.http.beans.HttpHeader;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;
import org.lybf.http.beans.RawHttpURL;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class getFiles extends BaseApi {
    @Override
    public void handler(HttpRequest request) {
        RawHttpURL rawHttpURL = request.getRawHttpURL();
        String path = rawHttpURL.getPath();
        processsGetFiles(request);
    }

    private void processsGetFiles(HttpRequest request) {
        HttpRespond respond = getRespond();
        String path = File.separator;
        if (containsKey("path")) {
                path = getValue("path");
            }

        JSONArray jsonArray = new JSONArray();
        File file = new File(request.getHttpServer().getDir() + File.separator + path);
        File[] files = new File[0];
        if (containsKey("filter")) {
            System.out.println("filter = " + getValue("filter"));
            final String filter = getValue("filter");
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
            jobj.put("path", f.getPath().replace(request.getHttpServer().getDir(), ""));
            jobj.put("lastModified", f.lastModified());
            jobj.put("isDir", f.isDirectory());
            jobj.put("size", f.length());
            jobj.put("canRead", f.canRead());
            jobj.put("date", new SimpleDateFormat("yyyy-dd-mm MM:HH:ss").format(
                    new Date(f.lastModified())));
            jsonArray.put(jobj);
        }
        respond.setFirstLine(RawHttpURL.OK);
        System.out.println("has returnType=" + containsKey("returnType"));
        if (containsKey("returnType")) {
            if (getValue("returnType").equals("jsonp")) {
                respond.addRespondHeader("Content-type", "application/javascrpit");
                if (containsKey("callback")) {
                    String method = getValue("callback");
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

    @Override
    public String getName() {
        return "/apis/getFiles";
    }
}
