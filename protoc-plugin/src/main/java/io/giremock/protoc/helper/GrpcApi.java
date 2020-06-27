package io.giremock.protoc.helper;

import java.util.List;
import java.util.Objects;

public class GrpcApi implements Api {

    private final String serviceName;
    private final String serviceMethod;

    public GrpcApi(String serviceName, String serviceMethod){
        this.serviceName = serviceName;
        this.serviceMethod = serviceMethod;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrpcApi that = (GrpcApi) o;
        return serviceName.toLowerCase().equals(that.serviceName.toLowerCase()) &&
                serviceMethod.toLowerCase().equals(that.serviceMethod.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName.toLowerCase(), serviceMethod.toLowerCase());
    }

    @Override
    public String get(String request) {
        RestApi restApi = ServiceMap.get(this);
        return restApi.get(request);
    }

    @Override
    public List<String> getAll(String requests) {
        RestApi restApi = ServiceMap.get(this);
        return restApi.getAll(requests);
    }
}
