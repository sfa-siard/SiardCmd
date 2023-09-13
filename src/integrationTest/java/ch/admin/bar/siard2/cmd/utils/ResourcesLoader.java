package ch.admin.bar.siard2.cmd.utils;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class ResourcesLoader {

    public final static String SAMPLE_DATALINK_2_2_SIARD = "siard-projects/2_2/sample-datalink-2.2.siard";


    private ResourcesLoader() {
    }

    public static File loadResource(final String resource) {
        final Optional<URL> urlToResource = Optional.ofNullable(ResourcesLoader.class
                .getClassLoader()
                .getResource(resource));

        return urlToResource.map(url -> new File(url.getFile()))
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource \"%s\" not found")));
    }
}
