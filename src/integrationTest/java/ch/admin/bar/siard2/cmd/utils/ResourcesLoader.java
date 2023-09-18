package ch.admin.bar.siard2.cmd.utils;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class ResourcesLoader {

    public final static String SAMPLE_DATALINK_2_2_SIARD = "siard-projects/2_2/sample-datalink-2-2.siard";
    public final static String SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2 = "siard-projects/2_2/simple-teams-example_oracle18_2-2.siard";
    public final static String NORTHWIND_2_1_SIARD = "siard-projects/2_1/northwind-2-1.siard";

    /**
     * Simple scheme but a lot of records
     */
    public final static String DVD_RENTAL_2_1_SIARD = "siard-projects/2_1/dvd-rental-2-1.siard";


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
}
