import apis.getFiles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;
import org.lybf.http.beans.RawHttpURL;
import org.lybf.http.net.APIManager;
import org.lybf.http.net.HttpServer;
import org.lybf.http.net.RequestHandler;
import org.lybf.http.net.RequestListener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import apis.getDate;
import ui.MainActivity;

public class Main {
    public static void main(String[] args) throws Exception {
        APIManager api = APIManager.getInstance();

        //apis/getDate
        getDate getdate = new getDate();
        api.add(getdate.getName(), getdate);

        //apis/getFiles
        getFiles getFiles = new getFiles();
        api.add(getFiles.getName(), getFiles);

        int port = 8064;

        //root dir
        String dir = "D:\\Projects\\HBuilder\\Homework\\";
        String index = "index.html";

        HttpServer server = new HttpServer();
        server.setDir(dir)
                .setIndexHtml(index)
                .setPort(port)
                .init();


        server.setRequestListener(new RequestListener() {
            @Override
            public void onRequest(HttpRequest request, HttpRespond respond) {
                //print request
                System.out.println("httpRequest=\n" + request.getRawHttpURL().toString());
                RawHttpURL raw = request.getRawHttpURL();
                //query api
                if (api.hasAPI(raw.getPath())) {
                    try {
                        api.invoke(raw.getPath(), request, respond);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //handle
                    RequestHandler h = new RequestHandler(server);
                    h.processRequest(request, respond);
                }
            }
        });
        //start web server
        server.startHttpServer();
        /*
        localhost:port/
        http://127.0.0.1:8064/
         */
    }


}