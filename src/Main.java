import apis.Download;
import apis.getFiles;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;
import org.lybf.http.beans.RawHttpURL;
import org.lybf.http.net.APIManager;
import org.lybf.http.net.HttpServer;
import org.lybf.http.net.RequestHandler;
import org.lybf.http.net.RequestListener;

import java.io.*;

import apis.getDate;

public class Main {
    public static void main(String[] args) throws Exception {
        APIManager api = APIManager.getInstance();

        //apis/getDate
        getDate getdate = new getDate();
        api.add(getdate.getName(), getdate);

        //apis/getFiles
        getFiles getFiles = new getFiles();
        api.add(getFiles.getName(), getFiles);

        Download download = new Download();
        api.add(download.getName(), download);

        int port = 8064;

        //root dir
        String dir = System.getProperty("user.dir") + File.separator + "web";
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
                System.out.println("httpRequestPath="+raw.getPath());
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