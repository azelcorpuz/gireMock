package io.giremock.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RestApi implements Api{

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String url;
    private final String method;

    public RestApi(String url, String method){
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestApi that = (RestApi) o;
        return url.toLowerCase().equals(that.url.toLowerCase()) &&
                method.toLowerCase().equals(that.method.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(url.toLowerCase(), method.toLowerCase());
    }

    @Override
    public String get(String requestStr) {
        RequestBody body = RequestBody.create(JSON, requestStr);
        Request request = new Request.Builder()
                .url(getUrl())
                .method(getMethod(), body)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new HelperException("Error occurred while calling the WireMock server", e);
        }
    }

    @Override
    public List<String> getAll(String request) {
        String response = get(request);
        try {
            return OBJECT_MAPPER.readValue(response, List.class);
        } catch (IOException e) {
            throw new HelperException("Error occurred while serializing the json!", e);
        }
    }
}
