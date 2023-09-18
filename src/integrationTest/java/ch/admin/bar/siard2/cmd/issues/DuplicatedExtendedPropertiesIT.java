package ch.admin.bar.siard2.cmd.issues;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.ResourcesLoader;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import lombok.val;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.MSSQLServerContainer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DuplicatedExtendedPropertiesIT {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public MSSQLServerContainer mssqlserver = (MSSQLServerContainer) new MSSQLServerContainer()
            .acceptLicense()
            .withInitScript(SqlScripts.MsSql.DUPLICATE_EXTENDED_PROPERTIES);

    @Test
    public void shouldCreateSiardArchiveFromDb() throws SQLException, IOException, ClassNotFoundException {

        final String archiveLocation = tmpFolder.getRoot() + "/archive.siard";

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + mssqlserver.getJdbcUrl(),
                "-u:" + mssqlserver.getUsername(),
                "-p:" + mssqlserver.getPassword(),
                "-s:" + archiveLocation
        });

        assertEquals(0, siardFromDb.getReturn());

        Archive archive = ArchiveImpl.newInstance();
        archive.open(new File(archiveLocation));
        MetaSchema metaSchema = archive.getMetaData().getMetaSchema("dbo");
        MetaTable testtable = metaSchema.getMetaTable("testtable");
        assertEquals("Caption.| Description.", testtable.getDescription());
    }
}
