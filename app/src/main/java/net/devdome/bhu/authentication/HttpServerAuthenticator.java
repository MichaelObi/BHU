package net.devdome.bhu.authentication;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.support.annotation.Nullable;
import android.util.Log;

import net.devdome.bhu.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpServerAuthenticator implements ServerAuthenticator {
    public static String getAuthCode(String response) {
        String authCode;

        try {
            JSONObject loginData = new JSONObject(response);
            authCode = loginData.getJSONObject("data").getString("key");
            return authCode;
        } catch (JSONException e) {
            Log.e(Config.TAG, e.getMessage());
        }
        return null;
    }

    public static String getPostData(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    @Override
    public JsonObject userSignIn(String email, String password, @Nullable String gcmToken) {
        HashMap<String, String> loginParams = new HashMap<>(2);
        loginParams.put("email", email);
        loginParams.put("password", password);
        if (gcmToken != null) {
            loginParams.put("device_token", gcmToken);
        }
        try {
            // Make Login Post Request
            URL loginUrl = new URL(Config.BASE_URL + "/auth");
            HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();

            connection.setReadTimeout(15000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(getPostData(loginParams));
            writer.flush();
            os.close();
            Log.w(Config.TAG, connection.getResponseCode() + " " + connection.getResponseMessage());
            JsonObject json;
            String line;
            String response = "";
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                Log.e(Config.TAG, "response: " + response);
                json = new JsonParser().parse(response).getAsJsonObject();
                json.addProperty("response_code", connection.getResponseCode());
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                json = new JsonParser().parse(response).getAsJsonObject();
                json.addProperty("response_code", connection.getResponseCode());
                Log.e(Config.TAG, "Error response: " + response);
            }
            return json;
        } catch (IOException e) {
            Log.e(Config.TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonObject userRegistration(String email, String firstName, String lastName, String matricNo, String password, String confirmPassword, String departmentCode, String level) {
        HashMap<String, String> loginParams = new HashMap<>(2);
        loginParams.put("email", email);
        loginParams.put("first_name", firstName);
        loginParams.put("last_name", lastName);
        loginParams.put("matric_no", matricNo);
        loginParams.put("password", password);
        loginParams.put("password_confirmation", confirmPassword);
        loginParams.put("level", level);
        loginParams.put("department", departmentCode);
        try {
            // Make Register Post Request
            URL loginUrl = new URL(Config.BASE_URL + "/register");
            HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(8000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(getPostData(loginParams));
            writer.flush();
            os.close();
            Log.w(Config.TAG, connection.getResponseCode() + " " + connection.getResponseMessage());
            JsonObject json;
            String line;
            String response = "";
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                Log.e(Config.TAG, "response: " + response);
                json = new JsonParser().parse(response).getAsJsonObject();
                json.addProperty("response_code", connection.getResponseCode());
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                json = new JsonParser().parse(response).getAsJsonObject();
                json.addProperty("response_code", connection.getResponseCode());
                Log.e(Config.TAG, "Error response: " + response);
            }
            return json;
        } catch (IOException e) {
            Log.e(Config.TAG, e.getMessage());

            return null;
        }
    }


}
