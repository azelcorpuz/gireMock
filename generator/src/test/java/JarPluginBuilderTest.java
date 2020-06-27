import io.giremock.generator.JarPluginBuilder;
import io.giremock.generator.ProtocPlugin;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JarPluginBuilderTest {

    private JarPluginBuilder toTest = new JarPluginBuilder(
            "giremock",
            "giremock-protoc-plugin.jar",
            "io.giremock.protoc.generator.mock.MockGenerator");

    @Test
    public void test() throws IOException {
        ProtocPlugin plugin = toTest.build();
        assertEquals("giremock", plugin.getId());

        File pluginFile = new File(plugin.getExecutable());
        File pluginDir = pluginFile.getParentFile();
        File[] files = pluginDir.listFiles();
        assertEquals(2, files.length);
        assertTrue(pluginDir.isDirectory());
        //TODO assert the 2 jar files
        //TODO assert sh script

        FileUtils.deleteDirectory(pluginDir);
    }
}
