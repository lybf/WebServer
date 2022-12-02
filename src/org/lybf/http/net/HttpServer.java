package org.lybf.http.net;

import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.Thread;

public class HttpServer {
    private int port;

    private String dir;

    private String indexHtml;

    private ServerSocket serverSocket;

    private HashMap<String, String> htmls = new HashMap<>();
    private HashMap<String, String> res = new HashMap<>();

    public HttpServer setIndexHtml(String indexHtml) {
        this.indexHtml = indexHtml;
        return this;
    }

    public String getIndexHtml() {
        return indexHtml;
    }

    public HttpServer addHtml(String name, String path) {
        htmls.put(name, path);
        return this;
    }

    public String getHtml(String name) {
        if (htmls.containsKey(name)) return htmls.get(name);
        return null;
    }

    public HttpServer addRes(String name, String path) {
        res.put(name, path);
        return this;
    }

    public String getRes(String name) {
        if (res.containsKey(name)) return res.get(name);
        return null;
    }

    public HttpServer setDir(String dir) {
        this.dir = dir;
        return this;
    }

    public String getDir() {
        return dir;
    }

    public HttpServer setPort(int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }


    public HttpServer init() throws IOException {
        this.serverSocket = new ServerSocket(port);
        return this;
    }

    public void stop() {
        running = false;
    }


    public HttpServer setMaxThread(int max) {
        this.maxThreads = max;
        return this;
    }

    private RequestListener listener;

    public HttpServer setRequestListener(RequestListener requestListener) {
        listener = requestListener;
        return this;
    }

    public boolean isRunning() {
        return running;
    }

    private int maxThreads = 10;
    private boolean running = true;


    private ExecutorService threads;

    public HttpServer startHttpServer() throws IOException {
        this.running = true;
        this.threads = Executors.newFixedThreadPool(maxThreads);
        this.serverSocket.setSoTimeout(0);

        System.out.println("-------HttpServerStart--------");
        System.out.println("ip:127.0.0.1:" + port);
        System.out.println("------------------------------");
        System.out.println("wating connect");
        while (running) {
            try {
                threads.execute(new HttpThread(serverSocket, serverSocket.accept()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }


    class HttpThread extends Thread {
        private ServerSocket serverSocket;
        private Socket socket;

        public HttpThread(ServerSocket serverSocket, Socket socket) {
            this.serverSocket = serverSocket;
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //  if(!socket.isConnected())return;
                System.out.println("----------Accept a connect---------");
                System.out.println("ip:" + socket.getInetAddress());
                if (listener != null) {
                    HttpRequest request = new HttpRequest(HttpServer.this,socket);
                    HttpRespond respond = new HttpRespond(socket);

                    listener.onRequest(request, respond);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
