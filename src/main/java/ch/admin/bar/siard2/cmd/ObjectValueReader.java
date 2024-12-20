package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.utils.database.SqlTypes;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


// understands how to read the value for a given cell from a result set
@AllArgsConstructor
class ObjectValueReader {
    private final ResultSet resultSet;
    private final int dataType;
    private final int position;


    public Object read() throws SQLException {
        Object oValue = null;
        switch (this.dataType) {
            case Types.CHAR:
            case Types.VARCHAR:
                oValue = resultSet.getString(position);
                break;
            case Types.CLOB:
                oValue = resultSet.getClob(position);
                break;
            case Types.SQLXML:
                oValue = resultSet.getSQLXML(position);
                break;
            case Types.NCHAR:
            case Types.NVARCHAR:
                oValue = resultSet.getNString(position);
                break;
            case Types.NCLOB:
                oValue = resultSet.getNClob(position);
                break;
            case Types.BINARY:
            case Types.VARBINARY:
                oValue = resultSet.getBytes(position);
                break;
            case Types.BLOB:
                oValue = resultSet.getBlob(position);
                break;
            case Types.DATALINK:
                oValue = resultSet.getURL(position);
                break;
            case Types.BOOLEAN:
                oValue = resultSet.getBoolean(position);
                break;
            case Types.SMALLINT:
                oValue = resultSet.getInt(position);
                break;
            case Types.INTEGER:
                oValue = resultSet.getLong(position);
                break;
            case Types.BIGINT:
                BigDecimal bdInt = resultSet.getBigDecimal(position);
                if (bdInt != null) oValue = bdInt.toBigIntegerExact();
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                oValue = resultSet.getBigDecimal(position);
                break;
            case Types.REAL:
                oValue = resultSet.getFloat(position);
                break;
            case Types.FLOAT:
            case Types.DOUBLE:
                oValue = resultSet.getDouble(position);
                break;
            case Types.DATE:
                oValue = resultSet.getDate(position);
                break;
            case Types.TIME:
                oValue = resultSet.getTime(position);
                break;
            case Types.TIMESTAMP:
                oValue = resultSet.getTimestamp(position);
                break;
            case Types.OTHER:
            case Types.STRUCT:
                oValue = resultSet.getObject(position);
                break;
            case Types.ARRAY:
                oValue = resultSet.getArray(position);
                break;
            default:
                throw new SQLException("Invalid data type " + dataType + " (" + SqlTypes.getTypeName(dataType) + ") encountered!");
        }
        return oValue;
    }


}
