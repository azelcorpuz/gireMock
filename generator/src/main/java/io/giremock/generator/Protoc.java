package io.giremock.generator;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.util.ArrayList;
import java.util.List;

public class Protoc {

    private final List<String> files;
    private final String dir;
    private final String output;
    private final List<ProtocPlugin> plugins;

    public Protoc(String protoDir, List<String> protoFiles, String javaOutputDir){
        this.files = protoFiles;
        this.dir = protoDir;
        this.output = javaOutputDir;
        this.plugins = new ArrayList<>();
    }

    public void addPlugin(ProtocPlugin plugin){
        plugins.add(plugin);
    }

    public List<ProtocPlugin> getPlugins(){
        return plugins;
    }

    public void execute() {
        final Commandline cl = new Commandline();
        //todo locate or download protoc
        // download proto if not available
        cl.setExecutable("/usr/local/bin/protoc");
        cl.addArguments(buildCommand().toArray(new String[]{}));

        CommandLineUtils.StringStreamConsumer error = new CommandLineUtils.StringStreamConsumer();
        CommandLineUtils.StringStreamConsumer output = new CommandLineUtils.StringStreamConsumer();

        // There is a race condition in JDK that may sporadically prevent process creation on Linux
        // https://bugs.openjdk.java.net/browse/JDK-8068370
        // In order to mitigate that, retry up to 2 more times before giving up
        int attemptsLeft = 3;
        while (attemptsLeft > 0) {
            try {
                --attemptsLeft;
                CommandLineUtils.executeCommandLine(cl, null, output, error);
            } catch (CommandLineException e) {
                if (attemptsLeft == 0 || e.getCause() == null) {
                    throw new RuntimeException("Unable to run the ProtobufGenerator: " + error.getOutput(), e);
                }
                //Thread.sleep(1000L);
            } finally {
                if(attemptsLeft == 0 && StringUtils.isNotEmpty(error.getOutput())){
                    throw new RuntimeException("Unable to run the ProtobufGenerator: " + error.getOutput());
                }
            }
        }
    }

    private List<String> buildCommand() {
        final List<String> command = new ArrayList<>();
        command.add("--proto_path=" + dir);

        for (ProtocPlugin plugin : plugins) {
            command.add("--plugin=protoc-gen-"+ plugin.getId() +"=" + plugin.getExecutable());
            command.add("--" + plugin.getId() +"_out=" + output);
        }
        command.add("--java_out=" + output);
        for (String file : files){
            command.add(file);
        }
        return command;
    }
}
