package ch.admin.bar.siard2.cmd.utils;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * This class provides version information about the application.
 * <p>
 * Note: It requires a properties file (versions.properties) with the properties "App-Version" and "SIARD-Version"
 * to be present in the classpath (generated through the Gradle task "createVersionsPropertiesFile" currently).
 */
public class VersionsExplorer {
    public static final VersionsExplorer INSTANCE = new VersionsExplorer();

    private final Properties properties;

    private VersionsExplorer() {
        this.properties = loadProperties();

        // test existence of properties
        getAppVersion();
        getSiardVersion();
    }

    public String getAppVersion() {
        return getProperty("App-Version");
    }

    public String getSiardVersion() {
        return getProperty("SIARD-Version");
    }

    private String getProperty(final String key) {
        return Optional.ofNullable(properties.getProperty("App-Version"))
                .orElseThrow(() -> new IllegalStateException(String.format("Property '%s' not found", key)));
    }

    private Properties loadProperties() {
        try {
            final Properties versionsProperties = new Properties();
            versionsProperties.load(ResourcesLoader.loadResourceAsStream("versions.properties"));

            return versionsProperties;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
