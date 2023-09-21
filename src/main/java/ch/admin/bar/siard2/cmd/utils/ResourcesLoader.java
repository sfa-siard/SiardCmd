package ch.admin.bar.siard2.cmd.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

/**
 * Helper class for loading class-path-resources.
 */
public class ResourcesLoader {
    private ResourcesLoader() {
    }

    /**
     * Checks if a resource exists and returns the path to it.
     *
     * @param resource The path from the source root to the resource.
     * @return A File object representing the existing resource.
     * @throws IllegalArgumentException if the specified resource does not exist.
     */
    public static File loadResource(final String resource) {
        final Optional<URL> urlToResource = Optional.ofNullable(ResourcesLoader.class
                .getClassLoader()
                .getResource(resource));

        return urlToResource.map(url -> new File(url.getFile()))
                .filter(File::exists)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource \"%s\" not found", resource)));
    }

    public static InputStream loadResourceAsStream(final String resource) {
        final Optional<InputStream> urlToResource = Optional.ofNullable(ResourcesLoader.class
                .getClassLoader()
                .getResourceAsStream(resource));

        return urlToResource
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource \"%s\" not found", resource)));
    }
}
