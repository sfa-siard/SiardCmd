/*======================================================================
PrimaryDataFromDb transfers primary data from databases to SIARD files. 
Application : Siard2
Description : Transfers primary data from databases to SIARD files.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 01.09.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.Record;
import ch.admin.bar.siard2.api.RecordRetainer;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.Value;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.StopWatch;
import ch.enterag.utils.background.Progress;
import ch.enterag.utils.database.SqlTypes;
import ch.enterag.utils.logging.IndentLogger;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.tika.Tika;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;

/*====================================================================*/

/**
 * Transfers primary data from databases to SIARD files.
 *
 * @author Hartwig Thomas
 */
@Slf4j
public class PrimaryDataFromDb extends PrimaryDataTransfer {
    /**
     * logger
     */
    private static final IndentLogger _il = IndentLogger.getIndentLogger(PrimaryDataFromDb.class.getName());
    private static final long _lREPORT_RECORDS = 1000;
    private Progress _progress = null;
    private long _lRecordsDownloaded = -1;
    private long _lRecordsTotal = -1;
    private long _lRecordsPercent = -1;
    private StopWatch _swGetCell = null;
    private StopWatch _swGetValue = null;
    private StopWatch _swSetValue = null;
    private final Tika tika = new Tika();

    /*------------------------------------------------------------------*/

    /**
     * increment the number of records downloaded, issuing a notification,
     * when a percent is reached.
     */
    private void incDownloaded() {
        _lRecordsDownloaded++;
        if ((_progress != null) && (_lRecordsTotal > 0) && ((_lRecordsDownloaded % _lRecordsPercent) == 0)) {
            int iPercent = (int) ((100 * _lRecordsDownloaded) / _lRecordsTotal);
            _progress.notifyProgress(iPercent);
        }
    } /* incDownloaded */

    /**
     * check if cancel was requested.
     *
     * @return true, if cancel was requested.
     */
    private boolean cancelRequested() {
        if (_progress != null && _progress.cancelRequested()) {
            LOG.info("Cancel downloading of primary data because of request");
            return true;
        }
        return false;
    }

    private void setValue(Value value, Object oValue)
            throws IOException, SQLException {
        if (oValue != null) {
            if (oValue instanceof String)
                value.setString((String) oValue);
            else if (oValue instanceof byte[]) {
                byte[] bytes = (byte[]) oValue;
                String mimeType = tika.detect(bytes);
                value.getMetaValue().setMimeType(mimeType);
                value.setBytes(bytes);
            } else if (oValue instanceof Boolean)
                value.setBoolean((Boolean) oValue);
            else if (oValue instanceof Short)
                value.setShort((Short) oValue);
            else if (oValue instanceof Integer)
                value.setInt((Integer) oValue);
            else if (oValue instanceof Long)
                value.setLong((Long) oValue);
            else if (oValue instanceof BigInteger)
                value.setBigInteger((BigInteger) oValue);
            else if (oValue instanceof BigDecimal)
                value.setBigDecimal((BigDecimal) oValue);
            else if (oValue instanceof Float)
                value.setFloat((Float) oValue);
            else if (oValue instanceof Double)
                value.setDouble((Double) oValue);
            else if (oValue instanceof Timestamp)
                value.setTimestamp((Timestamp) oValue);
            else if (oValue instanceof Time)
                value.setTime((Time) oValue);
            else if (oValue instanceof Date)
                value.setDate((Date) oValue);
            else if (oValue instanceof Duration)
                value.setDuration((Duration) oValue);
            else if (oValue instanceof Clob) {
                Clob clob = (Clob) oValue;
                String mimeType = tika.detect(clob.getAsciiStream());
                value.getMetaValue().setMimeType(mimeType);
                value.setReader(clob.getCharacterStream());
                clob.free();
            } else if (oValue instanceof SQLXML) {
                SQLXML sqlxml = (SQLXML) oValue;
                value.setReader(sqlxml.getCharacterStream());
                sqlxml.free();
            } else if (oValue instanceof Blob) {
                Blob blob = (Blob) oValue;
                String mimeType = tika.detect(blob.getBinaryStream());
                value.getMetaValue().setMimeType(mimeType);
                value.setInputStream(blob.getBinaryStream());
                blob.free();
            } else if (oValue instanceof URL) {
                URL url = (URL) oValue;
                value.setInputStream(url.openStream(), url.getPath());
            } else if (oValue instanceof Array) {
                Array array = (Array) oValue;
                Object[] ao = (Object[]) array.getArray();
                for (int iElement = 0; iElement < ao.length; iElement++) {
                    Value valueElement = value.getElement(iElement);
                    setValue(valueElement, ao[iElement]);
                }
                array.free();
            } else if (oValue instanceof Struct) {
                Struct struct = (Struct) oValue;
                Object[] ao = struct.getAttributes();
                for (int iAttribute = 0; iAttribute < ao.length; iAttribute++) {
                    Value valueAttribute = value.getAttribute(iAttribute);
                    setValue(valueAttribute, ao[iAttribute]);
                }
            } else
                throw new SQLException("Invalid value type " + oValue.getClass().getName() + " encountered!");
        }
    } /* setValue */

    /*------------------------------------------------------------------*/

    /**
     * extract primary data of a record from the result set.
     *
     * @param rs     result set.
     * @param record record to be filled.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getRecord(ResultSet rs, Record record)
            throws IOException, SQLException {
        ResultSetMetaData restultSetMetaData = rs.getMetaData();
        if (restultSetMetaData.getColumnCount() != record.getCells())
            throw new IOException("Invalid number of result columns found!");
        for (int iCell = 0; iCell < record.getCells(); iCell++) {
            _swGetCell.start();
            int iPosition = iCell + 1;
            Cell cell = record.getCell(iCell);
            MetaColumn mc = cell.getMetaColumn();
            // String sColumnName = mc.getName();
            int iDataType = mc.getPreType();
            if (mc.getCardinality() >= 0)
                iDataType = Types.ARRAY;
            MetaType mt = mc.getMetaType();
            if (mt != null) {
                CategoryType cat = mt.getCategoryType();
                if (cat == CategoryType.DISTINCT)
                    iDataType = mt.getBasePreType();
                else
                    iDataType = Types.STRUCT;
            }
            _swGetCell.stop();
            _swGetValue.start();
            Object oValue = null;
            switch (iDataType) {
                case Types.CHAR:
                case Types.VARCHAR:
                    oValue = rs.getString(iPosition);
                    break;
                case Types.CLOB:
                    oValue = rs.getClob(iPosition);
                    break;
                case Types.SQLXML:
                    oValue = rs.getSQLXML(iPosition);
                    break;
                case Types.NCHAR:
                case Types.NVARCHAR:
                    oValue = rs.getNString(iPosition);
                    break;
                case Types.NCLOB:
                    oValue = rs.getNClob(iPosition);
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                    oValue = rs.getBytes(iPosition);
                    break;
                case Types.BLOB:
                    oValue = rs.getBlob(iPosition);
                    break;
                case Types.DATALINK:
                    oValue = rs.getURL(iPosition);
                    break;
                case Types.BOOLEAN:
                    oValue = rs.getBoolean(iPosition);
                    break;
                case Types.SMALLINT:
                    oValue = rs.getInt(iPosition);
                    break;
                case Types.INTEGER:
                    oValue = rs.getLong(iPosition);
                    break;
                case Types.BIGINT:
                    BigDecimal bdInt = rs.getBigDecimal(iPosition);
                    if (bdInt != null)
                        oValue = bdInt.toBigIntegerExact();
                    break;
                case Types.DECIMAL:
                case Types.NUMERIC:
                    oValue = rs.getBigDecimal(iPosition);
                    break;
                case Types.REAL:
                    oValue = rs.getFloat(iPosition);
                    break;
                case Types.FLOAT:
                case Types.DOUBLE:
                    oValue = rs.getDouble(iPosition);
                    break;
                case Types.DATE:
                    oValue = rs.getDate(iPosition);
                    break;
                case Types.TIME:
                    oValue = rs.getTime(iPosition);
                    break;
                case Types.TIMESTAMP:
                    oValue = rs.getTimestamp(iPosition);
                    break;
                case Types.OTHER:
                case Types.STRUCT:
                    oValue = rs.getObject(iPosition);
                    break;
                case Types.ARRAY:
                    oValue = rs.getArray(iPosition);
                    break;
                default:
                    throw new SQLException("Invalid data type " +
                                                   iDataType + " (" +
                                                   SqlTypes.getTypeName(iDataType) + ") encountered!");
            } /* switch */
            if (rs.wasNull())
                oValue = null;
            _swGetValue.stop();
            _swSetValue.start();
            setValue(cell, oValue);
            _swSetValue.stop();
        } /* loop over values */
    } /* getRecord */

    /*------------------------------------------------------------------*/

    /**
     * download primary data of a table using a SELECT query for all
     * fields.
     *
     * @param table table.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getTable(Table table)
            throws IOException, SQLException {
        _il.enter(table.getMetaTable().getName());
        _swGetCell = StopWatch.getInstance();
        _swGetValue = StopWatch.getInstance();
        _swSetValue = StopWatch.getInstance();
        QualifiedId qiTable = new QualifiedId(null,
                                              table.getParentSchema().getMetaSchema().getName(),
                                              table.getMetaTable().getName());
        System.out.println("  Table: " + qiTable.format());
        long lRecord = 0;
        RecordRetainer rr = table.createRecords();
        ResultSet rs = openTable(table, null);
        Statement stmt = rs.getStatement();
        StopWatch swCreate = StopWatch.getInstance();
        StopWatch swGet = StopWatch.getInstance();
        StopWatch swPut = StopWatch.getInstance();
        StopWatch sw = StopWatch.getInstance();
        sw.start();
        long lBytesStart = rr.getByteCount();
        while (rs.next() && (!cancelRequested())) {
            swCreate.start();
            Record record = rr.create();
            swCreate.stop();
            swGet.start();

            getRecord(rs, record);
            swGet.stop();
            swPut.start();
            rr.put(record);
            swPut.stop();
            lRecord++;
            if ((lRecord % _lREPORT_RECORDS) == 0) {
                System.out.println("    Record " + lRecord + " (" + sw.formatRate(rr.getByteCount() - lBytesStart,
                                                                                  sw.stop()) + " kB/s)");
                lBytesStart = rr.getByteCount();
                sw.start();
            }
            incDownloaded();
        }
        System.out.println("    Record " + lRecord + " (" + sw.formatRate(rr.getByteCount() - lBytesStart,
                                                                          sw.stop()) + " kB/s)");
        System.out.println("    Total: " + StopWatch.formatLong(lRecord) + " records (" + StopWatch.formatLong(rr.getByteCount()) + " bytes in " + sw.formatMs() + " ms)");
        if (!rs.isClosed())
            rs.close();
        if (!stmt.isClosed())
            stmt.close();
        rr.close();

        LOG.debug("All data of table '{}.{}' successfully downloaded",
                qiTable.getSchema(),
                qiTable.getName());

        _il.exit();
    } /* getTable */

    /*------------------------------------------------------------------*/

    /**
     * download primary data of a schema.
     *
     * @param schema schema
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getSchema(Schema schema)
            throws IOException, SQLException {
        val schemaName = schema.getMetaSchema().getName();

        _il.enter(schemaName);
        for (int iTable = 0; (iTable < schema.getTables()) && (!cancelRequested()); iTable++) {
            Table table = schema.getTable(iTable);
            getTable(table);
        }

        LOG.debug("All data of schema '{}' successfully downloaded", schemaName);
        _il.exit();
    } /* getSchema */

    /*------------------------------------------------------------------*/

    /**
     * download primary data.
     *
     * @param progress receives progress notifications and sends cancel
     *                 requests.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    public void download(Progress progress)
            throws IOException, SQLException {

        LOG.info("Start primary data download to archive {}",
                this._archive.getFile().getAbsoluteFile());

        _il.enter();
        System.out.println("\r\nPrimary Data");
        _progress = progress;
        /* determine total number of records in the database */
        _lRecordsTotal = 0;
        for (int iSchema = 0; iSchema < _archive.getSchemas(); iSchema++) {
            Schema schema = _archive.getSchema(iSchema);
            for (int iTable = 0; iTable < schema.getTables(); iTable++) {
                Table table = schema.getTable(iTable);
                _lRecordsTotal = _lRecordsTotal + table.getMetaTable().getRows();
            }
        }
        _lRecordsPercent = (_lRecordsTotal + 99) / 100;
        _lRecordsDownloaded = 0;
        /* now download */
        for (int iSchema = 0; (iSchema < _archive.getSchemas()) && (!cancelRequested()); iSchema++) {
            Schema schema = _archive.getSchema(iSchema);
            getSchema(schema);
        }
        if (cancelRequested())
            throw new IOException("\r\nDownload of primary data cancelled!");
        System.out.println("\r\nDownload terminated successfully.");
        _conn.rollback();
        _il.exit();

        LOG.info("Primary data download finished");
    } /* download */

    /*------------------------------------------------------------------*/

    /**
     * constructor
     *
     * @param conn    database connection.
     * @param archive SIARD archive.
     */
    private PrimaryDataFromDb(Connection conn, Archive archive) {
        super(conn, archive, null, true, true, true);
    } /* constructor PrimaryDataTransfer */

    /*------------------------------------------------------------------*/

    /**
     * factory
     *
     * @param conn    database connection.
     * @param archive SIARD archive.
     * @return new instance of PrimaryDataFromDb.
     */
    public static PrimaryDataFromDb newInstance(Connection conn, Archive archive) {
        return new PrimaryDataFromDb(conn, archive);
    } /* newInstance */

} /* class PrimaryDataFromDb */
