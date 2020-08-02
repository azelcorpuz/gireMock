package io.giremock.helper;

import java.util.HashMap;
import java.util.Map;

public class ServiceMap {

    private ServiceMap(){

    }

    private static Map<GrpcApi, RestApi> grpcRestServicesMap = new HashMap<>();
    private static Map<RestApi, GrpcApi> restGrpcServicesMap = new HashMap<>();

    public static void add(GrpcApi grpcApi, RestApi restApi){
        grpcRestServicesMap.put(grpcApi, restApi);
        restGrpcServicesMap.put(restApi, grpcApi);
    }

    public static RestApi get(GrpcApi grpcApi){
        return grpcRestServicesMap.get(grpcApi);
    }

    public static GrpcApi get(RestApi restApi){
        return restGrpcServicesMap.get(restApi);
    }

}
