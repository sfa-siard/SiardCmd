package ch.admin.bar.siard2.cmd.usecases.keys.upload;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Assert;

public class MySqlKeysUpload {

    /**
     * Output of {@link ch.admin.bar.siard2.cmd.usecases.types.download.defaulttypes.MySql8DefaultDataTypesIT}
     */
    public final static String SIARD_ARCHIVE = "usecases/keys/simple-teams-example_mysql5.siard";
    public final static String CREATE_IT_USER_SQL_SCRIPT = "usecases/types/default/create-it-user.sql";

    @SneakyThrows
    public static void executeTest(SiardArchivesHandler siardArchivesHandler, String jdbcUrl) {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SIARD_ARCHIVE);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + jdbcUrl,
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + expectedArchive.getPathToArchiveFile()
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + jdbcUrl,
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        SiardArchiveAssertions.builder()
                .expectedArchive(expectedArchive)
                .actualArchive(actualArchive.preserveArchive())
                .assertEqual();
    }
}
