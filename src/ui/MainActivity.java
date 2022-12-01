package ui;

import org.lybf.http.net.HttpServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity {

    private HttpServer httpServer;

    public void show() {
        // 显示应用 GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showGUI();
            }
        });
    }

    JFrame frame;
    JLabel label;
    JLabel label_msg;
    Container pane;

    public void showGUI() {
        //JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        frame = new JFrame("HttpServer");

        frame.setSize(1000, 500);
        pane = frame.getContentPane();
        label = new JLabel("Hello World");
        label.setBounds(0,0,200,50);
        pane.add(label);

        label_msg = new JLabel("服务器消息:");
        label_msg.setBounds(0,60,200,50);

        pane.add(label_msg);
        frame.pack();
        frame.setVisible(true);
        update();
    }

    private void update() {
        new Thread(new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        label.setText("state=" + (httpServer.isRunning() ? "Running" : "Stop"));
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public void setHttpServer(HttpServer server) {
        this.httpServer = server;
    }
}
