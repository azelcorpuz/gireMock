package io.giremock.server;

import java.util.Arrays;

public class Application {

    public static void main(String[] args) {
        Runner runner = new Runner(Arrays.asList(args));
        runner.start();
    }
}
