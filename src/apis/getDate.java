package apis;

import org.lybf.http.api.BaseApi;
import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;
import org.lybf.http.beans.RawHttpURL;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *获取时间API
 */
public class getDate extends BaseApi {

    private String format;

    @Override
    public void handler(HttpRequest request) {
        System.out.println("handler:" + request.getRawHttpURL().toString());
        //获取回应方法
        HttpRespond respond = getRespond();

        SimpleDateFormat formator = new SimpleDateFormat("yyyy-MM-dd mm:ss");
        if ((format = getValue("format")) != null) {
            formator = new SimpleDateFormat(format);
        }
        Date date = new Date(System.currentTimeMillis());

        String date2 = formator.format(date);
        //回应格式
        respond.addRespondHeader("Content-type", "text/html");
        //首行信息
        respond.setFirstLine(RawHttpURL.OK);

        //回应内容
        String d = "<!DOCTYPE html>" +
                "<html>" +
                "    <head>" +
                "          <title>date</title>" +
                "    </head>" +
                "    <body>" +
                "          <center><H1>" + date2 + "</H1></center>" +
                "    </body>" +
                "</html>";
        try {
            //发送信息给客户端
            respond.write(d.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接口名，用于识别接口
    public String getName() {
        return "/getDate";
    }
}
