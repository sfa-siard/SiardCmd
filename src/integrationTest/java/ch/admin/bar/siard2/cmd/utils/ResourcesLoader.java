package ch.admin.bar.siard2.cmd.utils;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class ResourcesLoader {




    public final static String ORACLE_INIT = "config/oracle/00_create_user.sql";


    private ResourcesLoader() {
    }

    public static File loadResource(final String resource) {
        final Optional<URL> urlToResource = Optional.ofNullable(ResourcesLoader.class
                .getClassLoader()
                .getResource(resource));

        return urlToResource.map(url -> new File(url.getFile()))
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource \"%s\" not found", resource)));
    }

    public static class SiardProjectExamples {

    }
}
