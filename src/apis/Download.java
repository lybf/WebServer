package apis;

import org.lybf.http.api.BaseApi;
import org.lybf.http.beans.HttpRequest;

public class Download extends BaseApi {
    @Override
    public void handler(HttpRequest request) {

    }

    @Override
    public String getName() {
        return "/apis/download";
    }
}
