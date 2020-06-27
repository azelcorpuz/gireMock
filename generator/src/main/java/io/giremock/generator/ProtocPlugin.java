package io.giremock.generator;

public class ProtocPlugin {

    private final String id;
    private final String executable;

    public ProtocPlugin(String id, String executable){
        this.id = id;
        this.executable = executable;
    }

    public String getId() {
        return id;
    }

    public String getExecutable() {
        return executable;
    }
}
