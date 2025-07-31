package ch.admin.bar.siard2.cmd.oracle.issues.siardsuite129;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class BinaryTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public OracleContainer emptyDb = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql");

    @Rule
    public OracleContainer customDb = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDSUITE_129_BINARY).toPath()),
                    "/container-entrypoint-initdb.d/01_binary_types.sql");



    //Assert that siard archive created by siardcmd does not fail with the same exception when uploaded to db
    @Test
    public void uploadCreatedArchive_expectNoExceptions() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + customDb.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());

        val siardArchive = siardArchivesHandler.prepareResource("oracle/issues/siardsuite129/oracle-created-binary-types.siard");
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + emptyDb.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        // TODO: Assert that metadata was extracted once typeOriginal is added to SiardArchivesHandler
    }
}
