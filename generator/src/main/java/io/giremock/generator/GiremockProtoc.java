package io.giremock.generator;

import java.util.List;

public class GiremockProtoc extends GrpcProtoc {

    private static final String PLUGIN_ID = "giremock";
    private static final String PLUGIN_JAR_NAME = "giremock-protoc-plugin.jar";
    private static final String PLUGIN_MAINCLASS = "io.giremock.protoc.MockGenerator";

    public GiremockProtoc(String protoDir, List<String> protoFiles, String javaOutputDir) {
        super(protoDir, protoFiles, javaOutputDir);
        addPlugin(new JarPluginBuilder(PLUGIN_ID,
                PLUGIN_JAR_NAME, PLUGIN_MAINCLASS)
                .build());
    }
}
