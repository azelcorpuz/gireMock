package io.giremock.server;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.giremock.generator.GiremockProtoc;
import io.giremock.generator.Protoc;
import io.giremock.helper.GrpcApi;
import io.giremock.helper.RestApi;
import io.giremock.helper.ServiceMap;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class Runner {

    private static final String TEMP_DIR_PREFIX = "giremock";
    private static final int GRPC_PORT_DEFAULT = 6565;
    private static final int REST_PORT_DEFAULT = 8080;

    private final List<String> protoFiles;

    public Runner(List<String> protoFiles){
        if (protoFiles.size() == 0){
            throw new IllegalArgumentException("No proto files was given!");
        }
        this.protoFiles = protoFiles;
    }

    public void start(){
        String grpcPortConfig = System.getProperty("grpc.port");
        String restPortConfig = System.getProperty("rest.port");
        int restPort = StringUtils.isBlank(restPortConfig) ? REST_PORT_DEFAULT : Integer.parseInt(restPortConfig);
        int grpcPort = StringUtils.isBlank(grpcPortConfig) ? GRPC_PORT_DEFAULT : Integer.parseInt(grpcPortConfig);

        File testDir;
        try {
            testDir = Files.createTempDirectory(TEMP_DIR_PREFIX).toFile();
        } catch (IOException e) {
            throw new RunnerException("Unable to create a temporary directory!", e);
        }
        String testDirPath = testDir.getAbsolutePath();
        generateCodes(testDir);

        ServiceLoader serviceLoader = new ServiceLoader(testDirPath);
        serviceLoader.load();


        for (Pair<String, String> serviceAndMethodName : serviceLoader.getServiceAndMethodNames()) {
            GrpcApi grpcApi = new GrpcApi(serviceAndMethodName.getLeft(),
                    serviceAndMethodName.getRight());
            RestApi restApi = new RestApi("http://localhost:" + restPort + "/" +
                    grpcApi.getServiceName() + "/" + grpcApi.getServiceMethod(),
                    "POST");
            ServiceMap.add(grpcApi, restApi);
        }
        runServers(serviceLoader.getServices(), grpcPort, restPort);
    }

    private void runServers(List<BindableService> services, int grpcPort, int restPort){
        WireMockServer wireMockServer = new WireMockServer(options()
                .port(restPort));
        wireMockServer.start();

        try {
            ServerBuilder<?> serverBuilder = ServerBuilder.forPort(grpcPort);
            services.forEach(serverBuilder::addService);
            Server server = serverBuilder.build();
            server.start();
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            throw new RunnerException("GRPC Server failed to run!", e);
        }
    }

    private void generateCodes(File destination){
        List<String> protoFileNames = new ArrayList<>();
        for (String protoFile : protoFiles) {
            File file = new File(protoFile);
            try {
                FileUtils.copyFileToDirectory(new File(protoFile), destination);
            } catch (IOException e) {
                throw new RunnerException("Unable to copy files to temporary directory!", e);
            }
            protoFileNames.add(file.getName());
        }

        String testDirPath = destination.getAbsolutePath();
        Protoc protoc = new GiremockProtoc(testDirPath, protoFileNames, testDirPath);
        protoc.execute();
    }


}
