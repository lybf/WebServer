package org.lybf.http.beans;

import java.io.File;

public class FileType {
    public static final String HTML = "text/html";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String XML = "text/xml";
    public static final String GIF = "image/gif";
    public static final String JPEG = "image/jpeg";
    public static final String PNG = "image/png";
    /*
     *
     */
    public static final String[][] contentType = {
            {HTML, ".html", ".htm"},
            {TEXT_PLAIN, ".txt"},
            {XML, ".xml"},
            {GIF, ".gif"},
            {JPEG, ".jpg", ".jpeg"},
            {PNG, ".png"}
    };

    /*
     *
     */
    public static String getContentType(File file) {
        for (String[] str : contentType) {
            String type = str[0];
            for (String f : str) {
                if (file.getName().endsWith(f)) {
                    return type;
                }
            }
        }
        return null;
    }
}
