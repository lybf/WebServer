package org.lybf.http.net;

import org.lybf.http.beans.HttpRequest;
import org.lybf.http.beans.HttpRespond;

public interface RequestListener {
    void onRequest(HttpRequest request, HttpRespond respond);
}
