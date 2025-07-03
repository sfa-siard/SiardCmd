package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.Table;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;



/**
 * Opens a record set to a database for up- or download.
 *
 * @author Hartwig Thomas
 */
@Slf4j
public class PrimaryDataTransfer {
    protected Connection connection = null;
    protected Archive archive = null;
    protected ArchiveMapping archiveMapping = null;
    protected int queryTimeout = 30;
    protected boolean supportsUdts = false;
    protected boolean supportsArrays = false;
    protected boolean supportsDistincts = false;

    /**
     *
     * @param connection               database connection.
     * @param archive            SIARD archive.
     * @param archiveMapping                 mapping of names in archive.
     * @param supportsArrays    true, if database supports Arrays.
     * @param supportsDistincts true, if database supports DISTINCTs.
     * @param supportsUdts      true, if database supports UDTs.
     */
    protected PrimaryDataTransfer(Connection connection, Archive archive, ArchiveMapping archiveMapping,
                                  boolean supportsArrays, boolean supportsDistincts, boolean supportsUdts) {
        this.connection = connection;
        this.archive = archive;
        this.archiveMapping = archiveMapping;
        this.supportsArrays = supportsArrays;
        this.supportsDistincts = supportsDistincts;
        this.supportsUdts = supportsUdts;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public boolean supportsArrays() {
        return supportsArrays;
    }

    public boolean supportsDistincts() {
        return supportsDistincts;
    }

    public boolean supportsUdts() {
        return supportsUdts;
    }

    /**
     * issue a SELECT query for all fields of the table.
     *
     * @param table table.
     * @param sm    mapping of names in schema (null for read-only).
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    protected ResultSet openTable(Table table, SchemaMapping sm)
            throws IOException, SQLException {
        MetaTable mt = table.getMetaTable();

        TableMapping tm = null;
        if (sm != null)
            tm = sm.getTableMapping(mt.getName());

        StringBuilder sbSql = new StringBuilder("SELECT\r\n");
        List<List<String>> llColumnNames = mt.getColumnNames(supportsArrays(), supportsUdts());
        for (int iColumn = 0; iColumn < llColumnNames.size(); iColumn++) {
            if (iColumn > 0)
                sbSql.append(",\r\n");
            sbSql.append("  ");
            List<String> listColumnName = llColumnNames.get(iColumn);
            StringBuilder sbColumnName = new StringBuilder();
            for (int i = 0; i < listColumnName.size(); i++) {
                if (i > 0)
                    sbColumnName.append(".");
                sbColumnName.append(listColumnName.get(i));
            }
            String sExtendedColumnName = sbColumnName.toString();
            if (tm != null)
                sExtendedColumnName = tm.getMappedExtendedColumnName(sExtendedColumnName);
            String formattedColumnName = SqlLiterals.formatId(sExtendedColumnName);
            if (!formattedColumnName.startsWith("\"") && formattedColumnName.equals(formattedColumnName.toUpperCase())) {
                formattedColumnName = "\"" + formattedColumnName.replace("\"", "\"\"") + "\"";
            }
            sbSql.append(formattedColumnName);
        }
        String sSchemaName = mt.getParentMetaSchema()
                               .getName();
        if (sm != null)
            sSchemaName = sm.getMappedSchemaName();
        String sTableName = mt.getName();
        if (tm != null)
            sTableName = tm.getMappedTableName();
        QualifiedId qiTable = new QualifiedId(null, sSchemaName, sTableName);
        sbSql.append("\r\n FROM " + qiTable.format());

        val sqlStatement = sbSql.toString();
        LOG.trace("SQL statement: '{}'", sqlStatement);

        int iHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
        if (sm == null)
            iHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
        int iConcurrency = ResultSet.CONCUR_UPDATABLE;
        if (sm == null)
            iConcurrency = ResultSet.CONCUR_READ_ONLY;
        int iType = ResultSet.TYPE_FORWARD_ONLY;
        if (sm == null)
            iType = ResultSet.TYPE_FORWARD_ONLY;
        connection.setHoldability(iHoldability);
        Statement stmt = connection.createStatement(iType, iConcurrency, iHoldability);
        stmt.setQueryTimeout(queryTimeout);
        ResultSet rs = stmt.executeQuery(sbSql.toString());

        LOG.debug("Data from table '{}.{}' successfully loaded", qiTable.getSchema(), qiTable.getName());

        return rs;
    }
}
