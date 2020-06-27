package io.giremock.generator;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class GrpcProtoc extends Protoc {

    private static final String PLUGIN_ID = "grpc-java";
    private static final String PLUGIN_EXE = "protoc-gen-grpc-java-1.30.1-osx-x86_64.exe";

    public GrpcProtoc(String dir, List<String> files, String output) {
        super(dir, files, output);
        addPlugin(build());
    }

    private ProtocPlugin build(){
        try {
            File pluginDir = Files.createTempDirectory(PLUGIN_ID).toFile();
            // TODO download
            URL pluginJar = this.getClass().getClassLoader().getResource(PLUGIN_EXE);
            FileUtils.copyFileToDirectory(new File(pluginJar.toURI()), pluginDir);
            pluginDir.listFiles()[0].setExecutable(true);
            return new ProtocPlugin(PLUGIN_ID, pluginDir.listFiles()[0].getAbsolutePath());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to build the GRPC plugin!", e);
        }
    }




}
