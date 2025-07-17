package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.MetaData;
import ch.enterag.sqlparser.identifier.QualifiedId;
import lombok.Setter;

import java.sql.*;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

public abstract class MetaDataBase {
    public static final String TYPE_SCHEM = "TYPE_SCHEM";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String DATA_TYPE = "DATA_TYPE";
    public static final String INTEGER = "INTEGER";
    protected DatabaseMetaData databaseMetaData;
    protected MetaData metaData;
    @Setter
    protected int queryTimeout = 30;

    private boolean supportsArrays = false;
    private boolean supportsDistincts = false;
    private boolean supportsUdts = false;
    private Set<QualifiedId> usedTypes = null;

    /**
     * @param databaseMetaData database meta data.
     * @param metaData         SIARD meta data.
     */
    protected MetaDataBase(DatabaseMetaData databaseMetaData, MetaData metaData) throws SQLException {
        this.databaseMetaData = databaseMetaData;
        this.metaData = metaData;
        /* determine, whether UDTs are supported */
        checkSupportForUdts();
    }

    private void checkSupportForUdts() throws SQLException {
        ResultSet rs = this.databaseMetaData.getUDTs(null, "%", "%", null);
        while (rs.next() && ((!supportsUdts) || (!supportsDistincts))) {
            String typeSchema = rs.getString(TYPE_SCHEM);
            String typeName = rs.getString(TYPE_NAME);
            QualifiedId qualifiedId = new QualifiedId(null, typeSchema, typeName);
            if (isUsedInColumn(qualifiedId)) {
                int dataType = rs.getInt(DATA_TYPE);
                if (dataType == Types.STRUCT) supportsUdts = true;
                else if (dataType == Types.DISTINCT) supportsDistincts = true;
            }
        }
        rs.close();
        try {
            Array array = this.databaseMetaData.getConnection()
                                               .createArrayOf(INTEGER, new Integer[]{1, 2});
            array.free();
            supportsArrays = true;
        } catch (SQLFeatureNotSupportedException sfnse) {
            supportsArrays = false;
        }
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
     * if a type is only used for procedure parameters but not for table
     * columns, then we do not treat the database as "supporting" the type.
     *
     * @param qiType type to test.
     * @return true if the type is used for a column of a table.
     */
    private boolean isUsedInColumn(QualifiedId qiType) throws SQLException {
        if (usedTypes == null) {
            usedTypes = new HashSet<>();
            ResultSet rs = databaseMetaData.getColumns(null, "%", "%", "%");
            while (rs.next()) {
                int dataType = rs.getInt(DATA_TYPE);
                if ((dataType == Types.DISTINCT) || (dataType == Types.STRUCT)) {
                    String sTypeName = rs.getString(TYPE_NAME);
                    QualifiedId qi;
                    try {
                        qi = new QualifiedId(sTypeName);
                    } catch (ParseException pe) {
                        throw new SQLException(sTypeName + " could not be parsed!", pe);
                    }
                    usedTypes.add(qi);
                }
            }
            rs.close();
        }
        return usedTypes.contains(qiType);
    }


}
