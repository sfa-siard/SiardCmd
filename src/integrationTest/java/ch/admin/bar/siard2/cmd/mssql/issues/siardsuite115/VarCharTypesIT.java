package ch.admin.bar.siard2.cmd.mssql.issues.siardsuite115;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class VarCharTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer<?> emptyDb = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense();

    @Rule
    public MSSQLServerContainer<?> customDb = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense()
            .withInitScript(SqlScripts.MsSQL.SIARDSUITE_115);

    //Assert that siard archive created by siardcmd is uploaded back to db
    @Test
    public void uploadCreatedArchive_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val actualArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + customDb.getJdbcUrl(),
                "-u:" + customDb.getUsername(),
                "-p:" + customDb.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        //TODO: explore metadata and check column types and typeOriginal,
        // as in PrecisionTypesPostgresIT.java, after https://github.com/sfa-siard/Zip64File/issues/11 is resolved

        val expectedArchive = siardArchivesHandler.prepareResource("mssql/issues/siardsuite115/mssql-created-varchar-types.siard");

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + emptyDb.getJdbcUrl(),
                "-u:" + emptyDb.getUsername(),
                "-p:" + emptyDb.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
