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

        //创建 获取日期接口
        getDate getdate = new getDate();
        //将接口添加至 APIManager
        api.add(getdate.getName(), getdate);

        int port = 8064;//端口
        /*
         *设置网站根目录
         * 访问的网页将基于根目录+网页路径 如
         * 127.0.0.1:端口号/index.html
         */
        String dir = "D:\\Projects\\HBuilder\\Homework\\";
        /*
         *设置首页/导航页  输入ip:port/将自动索引至 ip:port/index.html
         */
        String index = "index.html";

        HttpServer server = new HttpServer();
        server.setDir(dir)//"D:\\Projects\\HBuilder\\Homework")
                .setIndexHtml(index)//"index.html")
                .setPort(port)
                .init();


        /*
        File list1 = new File("D:\\Projects\\HBuilder\\Homework");
        for (File f : list1.listFiles(pathname -> pathname.getName().endsWith(".html"))) {
            server.addHtml("/" + f.getName(), f.getPath());
        }

         */

        /*
         *设置监听事件
         */
        server.setRequestListener(new RequestListener() {
            @Override
            public void onRequest(HttpRequest request, HttpRespond respond) {
                //打印请求行
                System.out.println("httpRequest=\n" + request.getRawHttpURL().toString());
                RawHttpURL raw = request.getRawHttpURL();
                //查询接口是否存在，存在就调用
                if (api.hasAPI(raw.getPath())) {
                    try {
                        //调用
                        api.invoke(raw.getPath(), request, respond);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //特殊网页-智能表单
                } else if ("/autoform.html".equals(raw.getPath()) && raw.hasKey()) {
                    //   save(raw.getKeys());
                } else {
                    //处理请求
                    RequestHandler h = new RequestHandler(server);
                    h.processRequest(request, respond);
                }
            }
        });
        //启动网站
        server.startHttpServer();
        /*
        localhost：
        http://127.0.0.1:8064/+网页/接口
         */
    }

    /*
     * 存储留言
     */
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


        for (int i = 0; i < jsonArray.length(); i++) {

        }

    }

}