package com.piapox.idea.acct.action.autoReload;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class ConfigReloader {

    private static final String USERNAME = "system";
    private static final String PASSWORD = "manager";

    public interface ConfigReloadedListener {
        void onSuccess();
        void onFailed();
    }

    public static void reloadCopConfig(ConfigReloadedListener listener) {
        reloadConfig("http://pcy1229003.eur.ad.sag/copernicus/default/service/reconfigure", listener);
    }


    public static void reloadAbsConfig(ConfigReloadedListener listener) {
        reloadConfig("http://pcy1229003.eur.ad.sag/abs/copernicus/default/service/reconfigure", listener);
    }

    private static void reloadConfig(String uri, ConfigReloadedListener listener) {
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        try {
            HttpUriRequest loginRequest = RequestBuilder.post()
                    .setUri("http://pcy1229003.eur.ad.sag/copernicus/default/service/login")
                    .addParameter("schema", "0")
                    .addParameter("alias", USERNAME)
                    .addParameter("password", PASSWORD)
                    .build();

            try (CloseableHttpResponse loginResponse = httpClient.execute(loginRequest)) {

                HttpEntity entity = loginResponse.getEntity();
                int loginResponseStatusCode = loginResponse.getStatusLine().getStatusCode();
                if (loginResponseStatusCode != 200) {
                    listener.onFailed();
                    return;
                }

                String responseJSON = EntityUtils.toString(entity);
                Gson gson = new Gson();
                Map map = gson.fromJson(responseJSON, Map.class);
                String csrfToken = (String) map.get("csrfToken");

                // reload cop config
                HttpUriRequest reloadCopConfigRequest = RequestBuilder.post()
                        .setUri(uri)
                        .addParameter("csrfToken", csrfToken)
                        .build();

                // reuse the cookie from login response
                try (CloseableHttpResponse reloadCopConfigResponse = httpClient.execute(reloadCopConfigRequest)) {
                    int reloadCopConfigResponseStatusCode = reloadCopConfigResponse.getStatusLine().getStatusCode();
                    if (reloadCopConfigResponseStatusCode == 200) {
                        listener.onSuccess();
                    } else {
                        listener.onFailed();
                    }
                }
            }

        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onFailed();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
