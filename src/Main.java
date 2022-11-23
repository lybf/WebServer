import org.lybf.http.net.HttpServer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException {
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

        File list2 = new File("C:\\Users\\liang\\Desktop\\STUDY\\HTML\\lab9智能表单\\智能表单");
        for (File f : list2.listFiles(pathname -> pathname.getName().endsWith(".html"))) {
            server.addHtml("/" + f.getName(), f.getPath());
        }
        HashMap<String,String> res = new HashMap<>();
        addRes("/",list2,res );
        for(String k : res.keySet()){
            server.addRes(k,res.get(k));
        }
        server.startHttpServer();
        /*
        http://127.0.0.1:8066/getFiles?filter=html
         */
    }

    public static void addRes(String relativePath, File file, HashMap<String, String> res) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (File f : list) {
                addRes(relativePath + "/" + f.getName(), f, res);
            }
        } else {
            System.out.println("addres "+relativePath.replace("//","/")+"   "+file.getPath());
            res.put(relativePath.replace("//","/"), file.getPath());
        }
    }
}