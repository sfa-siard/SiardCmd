package ch.admin.bar.siard2.cmd.utils;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class ResourcesLoader {
    private ResourcesLoader() {
    }

    public static File loadResource(final String resource) {
        final Optional<URL> urlToResource = Optional.ofNullable(ResourcesLoader.class
                .getClassLoader()
                .getResource(resource));

        return urlToResource.map(url -> new File(url.getFile()))
                .filter(File::exists)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource \"%s\" not found", resource)));
    }
}
