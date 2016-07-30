package net.devdome.bhu.app.rest;

import com.squareup.okhttp.OkHttpClient;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.rest.service.ApiService;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RestClient {

    private ApiService apiService;

    public RestClient() {
        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(Config.DEFAULT_HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
        client.setConnectTimeout(Config.DEFAULT_HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Config.BASE_URL)
                .setClient(new OkClient(client))
                .build();
        apiService = restAdapter.create(ApiService.class);
    }

    public RestClient(long readTimeout, long connectTimeout) {
        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(readTimeout, TimeUnit.SECONDS);
        client.setConnectTimeout(connectTimeout, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Config.BASE_URL)
                .setClient(new OkClient(client))
                .build();
        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }
}
