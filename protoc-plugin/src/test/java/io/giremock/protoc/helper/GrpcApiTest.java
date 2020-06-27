package io.giremock.protoc.helper;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class GrpcApiTest {

    @Test
    public void testGet() {
        String expectedResponse = "{\"response\": \"response here\"}";
        String expectedRequest = "{\"request\": \"request here\"}";
        RestApi restApi = Mockito.mock(RestApi.class);
        Mockito.when(restApi.get(expectedRequest)).thenReturn(expectedResponse);
        GrpcApi api = new GrpcApi("serviceName", "serviceMethod");
        ServiceMap.add(new GrpcApi("ServiceName", "serviceMethod"),
                restApi);
        String responseString = api.get(expectedRequest);
        Assert.assertEquals(expectedResponse, responseString);
    }

    @Test
    public void testGetAll() {
        List<String> expectedResponse = new ArrayList<>();
        expectedResponse.add("{\"response\": \"response here\"}");
        expectedResponse.add("{\"response2\": \"response here\"}");
        String expectedRequest = "{\"request\": \"request here\"}";
        RestApi restApi = Mockito.mock(RestApi.class);
        Mockito.when(restApi.getAll(expectedRequest)).thenReturn(expectedResponse);
        GrpcApi api = new GrpcApi("serviceName", "serviceMethod");
        ServiceMap.add(new GrpcApi("ServiceName", "serviceMethod"),
                restApi);
        List<String> responseString = api.getAll(expectedRequest);
        Assert.assertEquals(expectedResponse, responseString);
    }
}
