package ch.admin.bar.siard2.cmd.issues.mssql;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.cmd.BaseFromDbTester;
import ch.admin.bar.siard2.cmd.SiardFromDb;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.MSSQLServerContainer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DoubleQuotesInUserDefinedTypesTest extends BaseFromDbTester {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public MSSQLServerContainer mssqlserver = (MSSQLServerContainer) new MSSQLServerContainer()
            .acceptLicense()
            .withInitScript("scripts/mssql/issue-25.sql");

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

        assertNotNull(metaSchema.getMetaTable("testtable"));

        MetaColumn dayColumn = metaSchema.getMetaTable("testtable").getMetaColumn("DAY");
        assertEquals("dtDay2", dayColumn.getMetaType().getName());

        assertEquals("dtDay2", metaSchema.getMetaType(0).getName());
    }
}