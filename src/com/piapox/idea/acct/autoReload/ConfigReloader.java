package com.piapox.idea.acct.autoReload;

import com.google.gson.Gson;
import com.piapox.idea.acct.autoReload.settings.AutoReloadSettings;
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

import static com.google.common.base.Strings.isNullOrEmpty;

class ConfigReloader {

    private final AutoReloadSettings settings;

    public interface ConfigReloadedListener {
        void onSuccess();
        void onFailed();
    }

    ConfigReloader(AutoReloadSettings settings) {
        this.settings = settings;
    }

    void reloadCopConfig(ConfigReloadedListener listener) {
        String urlCOPReload = settings.getUrlCOPReload();
        if (!isNullOrEmpty(urlCOPReload)) {
            reloadConfig(urlCOPReload, listener);
        }
    }

    void reloadAbsConfig(ConfigReloadedListener listener) {
        String urlABSReload = settings.getUrlABSReload();
        if (!isNullOrEmpty(urlABSReload)) {
            reloadConfig(urlABSReload, listener);
        }
    }

    private void reloadConfig(String uri, ConfigReloadedListener listener) {
        String username = settings.getUsername();
        String password = settings.getPassword();
        String urlLogin = settings.getUrlLogin();
        if (isNullOrEmpty(username) || isNullOrEmpty(password) || isNullOrEmpty(urlLogin)) return;

        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        try {
            HttpUriRequest loginRequest = RequestBuilder.post()
                    .setUri(urlLogin)
                    .addParameter("schema", "0")
                    .addParameter("alias", username)
                    .addParameter("password", password)
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
