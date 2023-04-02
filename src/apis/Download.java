package apis;

import org.lybf.http.api.BaseApi;
import org.lybf.http.beans.HttpHeader;
import org.lybf.http.beans.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Download extends BaseApi {
    @Override
    public void handler(HttpRequest request) {
        HttpHeader header = request.getHeader();
        String path = getValue("path");
        File file;
        if (path != null) {
            file = new File(request.getHttpServer().getDir() + File.separator + path);
        } else {
            file = new File(request.getHttpServer().getDir() + File.separator +
                    request.getRawHttpURL().getPath().replace(getName(), ""));
        }
        if (file.exists()) {
            if (header != null) {
                ArrayList<Part> ranges = new ArrayList<>();
                String range = null;
                ArrayList range2 = header.getHeader("Range");
                if (range2 != null) range = (String) range2.get(0);
                if (range != null) {
                    if (range.contains(",")) {
                        String[] strs = range.replace("bytes=", "").split(",");
                        for (String s : strs) {
                            Part p = new Part(s);
                            ranges.add(p);
                        }
                    }
                } else {
                    ranges.add(new Part((range != null ? (range.replace("bytes=", ""))
                            : "0-" + file.length())));
                }
                for (Part p : ranges) {
                    sendFile(file, p);
                }
            } else {

            }

        }
    }

    private void sendFile(File file, Part p) {
        long start = 0;
        long end = 0;
        if (p.start <= 0) {
            start = 0;
        } else {
            start = p.start;
        }
        if (p.end >= file.length()) {
            end = file.length();
        } else {
            end = p.end;
        }
        String range = "bytes=" + start + "-" + end + "/";
        getRespond().addRespondHeader(HttpHeader.ContentLength, file.length());
        getRespond().addRespondHeader("Content-Range", "bytes=" + start + "-" + end + "/" + file.length());

        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int j = 0;
            while ((j = inputStream.read(bytes)) > 0) {
                getRespond().write(bytes);
            }
            getRespond().flush();
            getRespond().disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "/apis/download";
    }

    class Part {
        public long start;
        public long end;
        public long total;

        public Part() {
        }

        public Part(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.total = total;
        }

        public Part(String string) {
            String[] strs = string.split("-");
            start = Long.valueOf(strs[0]);
            end = Long.valueOf(strs[1]);
        }
    }
}
