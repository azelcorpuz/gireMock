package io.giremock.protoc.helper;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestApi.class)
public class RestApiTest {

    private final static MediaType json =  MediaType.get("application/json; charset=utf-8");
    private final static OkHttpClient mockClient = Mockito.mock(OkHttpClient.class);
    private final static Call mockCall = Mockito.mock(Call.class);

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(OkHttpClient.class).withNoArguments().thenReturn(mockClient);
        Mockito.reset(mockClient,mockCall);
    }

    private void setMock(RestApi api,
                         String requestString, String responseString) {
        Request request = new Request.Builder().url(api.getUrl())
                .method(api.getMethod(), RequestBody.create(json, requestString))
                .build();
        Response response = new Response.Builder().body(ResponseBody.create(json, responseString))
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(1)
                .message("some message")
                .build();
        Mockito.when(mockClient.newCall(argThat(r ->{
            Buffer buffer = new Buffer();
            try {
                r.body().writeTo(buffer);
            } catch (IOException e) {
                Assert.fail("Exception receive, test failed forcefully: "+  e.getMessage());
            }
            return r.url().equals(request.url())
                    && r.method().equals(request.method())
                    && buffer.readUtf8().equals(requestString);
        }))).thenReturn(mockCall);
        try{
            Mockito.when(mockCall.execute()).thenReturn(response);
        }catch (IOException e){
            Assert.fail("Exception receive, test failed forcefully: "+  e.getMessage());
        }
    }

    @Test
    public void testGet() {
        String expectedResponse = "{\"response\": \"response here\"}";
        String expectedRequest = "{\"request\": \"request here\"}";
        RestApi api = new RestApi("https://url1", "POST");
        setMock(api, expectedRequest, expectedResponse);
        String responseString = api.get(expectedRequest);
        Assert.assertEquals(expectedResponse, responseString);
    }

    @Test
    public void testGetAll() {
        String expectedResponse = "[{\"response\": \"response here\"}, {\"response\": \"response2 here\"}]";
        String expectedRequest = "{\"request\": \"request here\"}";
        RestApi api = new RestApi("https://url1", "post");
        setMock(api, expectedRequest, expectedResponse);
        List<String> responseStrings = api.getAll(expectedRequest);
        Assert.assertEquals(2, responseStrings.size());
    }
}
