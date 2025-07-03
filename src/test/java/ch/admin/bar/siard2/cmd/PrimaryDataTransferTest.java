package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.*;
import ch.enterag.utils.DU;
import ch.enterag.utils.background.Progress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PrimaryDataTransferTest {

    Connection connection = mock(Connection.class);
    Archive archive = mock(Archive.class);
    ArchiveMapping archiveMapping = mock(ArchiveMapping.class);

    PrimaryDataTransfer primaryDataTransfer;

    @BeforeEach
    void setUp() {
        primaryDataTransfer = new PrimaryDataTransfer(connection, archive, archiveMapping, true, true, true);
    }

    @Test
    void construct_new_instance() {
        // given

        // when

        // then
        assertNotNull(primaryDataTransfer);
        assertTrue(primaryDataTransfer.supportsArrays());
        assertTrue(primaryDataTransfer.supportsDistincts());
        assertTrue(primaryDataTransfer.supportsUdts());
    }

    @Test
    void openTable() throws SQLException, IOException {
        // given
        Table table = mock(Table.class);

        // when
        ResultSet rs = primaryDataTransfer.openTable(null, null);

        // then
        assertNotNull(rs);
    }

}