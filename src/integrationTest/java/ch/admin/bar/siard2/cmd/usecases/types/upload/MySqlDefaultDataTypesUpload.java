package ch.admin.bar.siard2.cmd.usecases.types.upload;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.usecases.types.download.defaulttypes.MySqlDefaultDataTypes;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.Content;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.update.UpdateInstruction;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Assert;

public class MySqlDefaultDataTypesUpload {

    /**
     * Output of {@link ch.admin.bar.siard2.cmd.usecases.types.download.defaulttypes.MySql8DefaultDataTypesIT}
     */
    public final static String SIMPLE_TEAMS_EXAMPLE = "usecases/types/default/all-default-data-types_mysql8.siard";
    public final static String CREATE_IT_USER_SQL_SCRIPT = "usecases/types/default/create-it-user.sql";

    @SneakyThrows
    public static void executeTest(SiardArchivesHandler siardArchivesHandler, String jdbcUrl) {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SIMPLE_TEAMS_EXAMPLE);
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
                .assertionModifier(ignoreColumnCellValue(expectedArchive, MySqlDefaultDataTypes.COLUMN_DATE)) // FIXME Seems to be an upload issue
                .assertionModifier(ignoreColumnCellValue(expectedArchive, MySqlDefaultDataTypes.COLUMN_BIT)) // FIXME Seems to be an upload issue
                .assertEqual();
    }

    private static SiardArchiveAssertions.UpdateInstructionAssertionModifier ignoreColumnCellValue(
            final SiardArchivesHandler.SiardArchiveExplorer expectedArchive,
            final QualifiedColumnId qualifiedColumnId
    ) {
        val cells = expectedArchive.exploreContent().findCells(qualifiedColumnId);
        val columnNumber = cells.get(0).getColumnNumber();

        return SiardArchiveAssertions.UpdateInstructionAssertionModifier.builder()
                .description("Ignore the content of " + qualifiedColumnId)
                .updateInstruction(UpdateInstruction.<Content.TableCell>builder()
                        .clazz(Content.TableCell.class)
                        .updater(cell -> {
                            if (cell.getColumnNumber() == columnNumber) {
                                return cell.toBuilder()
                                        .value(SiardArchiveAssertions.IGNORED_PLACEHOLDER.getValue())
                                        .build();
                            }

                            return cell;
                        })
                        .build())
                .build();
    }
}
