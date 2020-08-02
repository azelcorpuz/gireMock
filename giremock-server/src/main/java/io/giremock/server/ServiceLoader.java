package io.giremock.server;

import io.grpc.BindableService;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.plexus.util.FileUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceLoader {

    private static final String MOCK_SUFFIX = "Mock";
    private static final String JAVA_CLASSES_PATTERN = "**/*.java";

    private final String classesDir;
    private final File fileDir;

    private final List<BindableService> services = new ArrayList<>();
    private final List<Pair<String, String>> serviceAndMethodNames = new ArrayList<>();

    public ServiceLoader(String classesDir){
        this.classesDir = classesDir;
        this.fileDir = new File(classesDir);
    }

    public List<BindableService> getServices(){
        return services;
    }

    public List<Pair<String, String>> getServiceAndMethodNames(){
        return serviceAndMethodNames;
    }

    public void load(){
        List<String> result;
        try {
            result = FileUtils.getFileNames(fileDir, JAVA_CLASSES_PATTERN, null, true);
        } catch (IOException e) {
            throw new ServiceLoaderException("Unable to list the java classes", e);
        }
        URLClassLoader classLoader = compile(result);
        List<String> mockClasses = result.stream()
                .map(path -> path.substring(classesDir.length() + 1, path.length() - 5))
                .filter(path -> path.endsWith(MOCK_SUFFIX))
                .map(path -> path.replace(File.separatorChar, '.'))
                .collect(Collectors.toList());
        for (String mockClass : mockClasses) {
            try{
                Class<BindableService> cls = (Class<BindableService>) Class.forName(mockClass, true, classLoader);
                services.add(cls.newInstance());
                String serviceName = cls.getSimpleName().replace(MOCK_SUFFIX, "");
                for (Method method : cls.getDeclaredMethods()) {
                    serviceAndMethodNames.add(Pair.of(serviceName, method.getName()));
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
                throw new ServiceLoaderException("Unable to instantiate the class", e);
            }
        }
    }

    private URLClassLoader compile(List<String> classesToCompile){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, classesToCompile.toArray(new String[]{}));
        try {
            return URLClassLoader.newInstance(new URL[] { fileDir.toURI().toURL() });
        } catch (MalformedURLException e) {
            throw new ServiceLoaderException("Unable to load the classes", e);
        }
    }

}
