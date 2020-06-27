import io.giremock.generator.Protoc;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ProtocTest {

    @Test
    public void test() throws IOException, URISyntaxException {
        File testDir = Files.createTempDirectory("protoc-test").toFile();
        URL testProto = this.getClass().getClassLoader().getResource("test-simple.proto");
        FileUtils.copyFileToDirectory(new File(testProto.toURI()), testDir);
        String testDirPath = testDir.getAbsolutePath();
        Protoc protoc = new Protoc(
                testDirPath,
                Arrays.asList("test-simple.proto"),
                testDirPath);
        protoc.execute();
        File generated = new File(testDirPath + "/io/giremock/test/simple");
        assertEquals(1, generated.listFiles().length);
        //todo assert files - TestSimpleProto
        FileUtils.deleteDirectory(testDir);
    }
}
