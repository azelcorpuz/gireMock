import io.giremock.generator.GiremockProtoc;
import io.giremock.generator.GrpcProtoc;
import io.giremock.generator.Protoc;
import io.giremock.generator.ProtocPlugin;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GiremockProtocTest {

    @Test
    public void test() throws IOException, URISyntaxException {
        File testDir = Files.createTempDirectory("protoc-test").toFile();
        URL testProto = this.getClass().getClassLoader().getResource("test.proto");
        FileUtils.copyFileToDirectory(new File(testProto.toURI()), testDir);
        String testDirPath = testDir.getAbsolutePath();
        Protoc protoc = new GiremockProtoc(testDirPath,
                Arrays.asList("test.proto"),
                testDirPath);
        protoc.execute();
        File generated = new File(testDirPath + "/io/grpc/testing/compiler");
        assertEquals(5, generated.listFiles().length);
        //todo assert files - TestProto
        FileUtils.deleteDirectory(testDir);

        List<ProtocPlugin> plugins = protoc.getPlugins();
        for (ProtocPlugin plugin : plugins) {
            File pluginDir = new File(plugin.getExecutable()).getParentFile();
            FileUtils.deleteDirectory(pluginDir);
        }
    }
}
