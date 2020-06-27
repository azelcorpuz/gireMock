package io.giremock.generator;

import java.util.List;

public class GiremockProtoc extends GrpcProtoc {

    private static final String PLUGIN_ID = "giremock";
    private static final String PLUGIN_JAR_NAME = "giremock-protoc-plugin.jar";
    private static final String PLUGIN_MAINCLASS = "io.giremock.protoc.generator.mock.MockGenerator";

    public GiremockProtoc(String dir, List<String> files, String output) {
        super(dir, files, output);
        addPlugin(new JarPluginBuilder(PLUGIN_ID,
                PLUGIN_JAR_NAME, PLUGIN_MAINCLASS)
                .build());
    }
}
