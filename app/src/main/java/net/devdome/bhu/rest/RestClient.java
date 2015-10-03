package net.devdome.bhu.rest;

import com.squareup.okhttp.OkHttpClient;

import net.devdome.bhu.Config;
import net.devdome.bhu.rest.service.ApiService;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RestClient {
    private ApiService apiService;

    public RestClient() {
        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(60, TimeUnit.SECONDS);
        client.setConnectTimeout(30, TimeUnit.SECONDS);

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
