import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;
import org.lybf.http.beans.RawHttpURL;
import org.lybf.http.net.HttpServer;
import org.lybf.http.net.RequestHandler;
import org.lybf.http.net.RequestListener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        String dir = "D:\\Projects\\HBuilder\\Homework\\";// "C:\\Users\\liang\\Desktop\\STUDY\\HTML\\lab9智能表单\\智能表单";
        String index = "index.html";
        HttpServer server = new HttpServer();
        server.setDir(dir)//"D:\\Projects\\HBuilder\\Homework")
                .setIndexHtml(index)//"index.html")
                .setPort(8064)
                .init();

        File list1 = new File("D:\\Projects\\HBuilder\\Homework");
        for (File f : list1.listFiles(pathname -> pathname.getName().endsWith(".html"))) {
            server.addHtml("/" + f.getName(), f.getPath());
        }

        server.setRequestListener(new RequestListener() {
            @Override
            public void onRequest(HttpRequest request, HttpRespond respond) {
                System.out.println("httpRequest=" + request.getRawHttpURL().toString());
                RawHttpURL raw = request.getRawHttpURL();
                if (raw.getPath().equals("/autoform.html") && raw.hasKey()) {
                 //   save(raw.getKeys());
                } else {
                    RequestHandler h = new RequestHandler(server);
                    h.processRequest(request);
                }
            }
        });
        server.startHttpServer();
        /*
        http://127.0.0.1:8066/getFiles?filter=html
         */
    }

    public static void save(HashMap<String, String> hashMap) throws Exception {
        String path = "C:\\Users\\liang\\Desktop\\DEV\\Datas";
        File file = new File(path);
        String str = "[]";
        JSONArray jsonArray;
        if (file.exists()) {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            inputStream.read(bytes);
            str = String.valueOf(bytes);
            jsonArray = new JSONArray();
        } else {
            jsonArray = new JSONArray();
        }
        JSONObject jobj = new JSONObject();
        jobj.put("name", hashMap.get("name"));
        jobj.put("sex", hashMap.get("sex"));
        jobj.put("city", hashMap.get("city"));
        jobj.put("favcolor", hashMap.get("favcolor"));
        jobj.put("birth", hashMap.get("birth"));
        jobj.put("tel", hashMap.get("tel"));
        jobj.put("height", hashMap.get("height"));
        jobj.put("kg", hashMap.get("kg"));


        for(int i = 0 ; i < jsonArray.length() ; i++){

        }

    }

}