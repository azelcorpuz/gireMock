package io.giremock.generator;
import org.codehaus.plexus.util.FileUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class JarPluginBuilder {

    private final String PROTO_PLUGIN_PREFIX = "protoc-gen-";
    private final String jar;
    private final String pluginId;
    private final String mainClass;

    /**
     *
     * @param jar name of the fat jar that implements the plugin, should be present in the class path
     * @param pluginId pluginId to be used
     * @param mainClass full name of the main class (includes package) inside jar
     */
    public JarPluginBuilder(String pluginId, String jar, String mainClass){
        this.jar = jar;
        this.pluginId = pluginId;
        this.mainClass = mainClass;

    }

    public ProtocPlugin build() {
        try {
            File pluginExec = buildUnixPlugin(Files.createTempDirectory(pluginId).toFile());
            return new ProtocPlugin(pluginId, pluginExec.getAbsolutePath());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to create temporary directory for the plugin!", e);
        }
        //TODO windows support, check xolstice protobuf plugin
    }

    private File buildUnixPlugin(File pluginDir) throws IOException, URISyntaxException {
        //TODO: find java home automatically
        final File javaLocation = new File("/usr", "bin/java");
        URL pluginJar = this.getClass().getClassLoader().getResource(jar);
        FileUtils.copyFileToDirectory(new File(pluginJar.toURI()), pluginDir);
        final File pluginExec = new File(pluginDir.getAbsolutePath(), PROTO_PLUGIN_PREFIX + pluginId);
        try (final PrintWriter out = new PrintWriter(new FileWriter(pluginExec))) {
            out.println("#!/bin/sh");
            out.println();
            out.print("CP="+ pluginDir.getAbsolutePath() + "/giremock-protoc-plugin.jar");
            out.println();
            out.println("\"" + javaLocation.getAbsolutePath() + "\" -cp $CP " + mainClass);
            out.println();
        }
        pluginExec.setExecutable(true);
        return pluginExec;
    }
}
