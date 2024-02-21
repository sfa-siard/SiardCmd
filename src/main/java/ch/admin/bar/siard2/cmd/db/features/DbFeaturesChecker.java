package ch.admin.bar.siard2.cmd.db.features;

import ch.enterag.sqlparser.identifier.QualifiedId;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class DbFeaturesChecker {

    @NonNull private final Connection connection;
    @NonNull private final DatabaseMetaData dbMetaData;

    @SneakyThrows
    public DbFeatures checkFeatures() {
        val result = checkUdtAndDistinctSupport();

        return DbFeatures.builder()
                .maxColumnNameLength(dbMetaData.getMaxColumnNameLength())
                .maxTableNameLength(dbMetaData.getMaxTableNameLength())
                .arraysSupported(checkArraysSupport())
                .distinctsSupported(result.isDistinctsSupported())
                .udtsSupported(result.isUdtsSupported())
                .build();

    }

    private boolean checkArraysSupport() {
        try {
            Array array = connection.createArrayOf("INTEGER", new Integer[]{1, 2});
            array.free();

            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    private Result checkUdtAndDistinctSupport() throws SQLException {
        boolean supportsUdts = false;
        boolean supportsDistincts = false;

        val usedUDTs = findUsedUDTs();

        val resultSet = dbMetaData.getUDTs(
                null,
                "%",
                "%",
                null
        );

        while (resultSet.next()) {
            val sTypeSchema = resultSet.getString("TYPE_SCHEM");
            val sTypeName = resultSet.getString("TYPE_NAME");

            if (usedUDTs.contains(new QualifiedId(null, sTypeSchema, sTypeName))) {
                val dataType = resultSet.getInt("DATA_TYPE");

                if (dataType == Types.STRUCT) {
                    supportsUdts = true;
                } else if (dataType == Types.DISTINCT) {
                    supportsDistincts = true;
                }
            }

            if (supportsUdts && supportsDistincts) {
                break;
            }
        }

        return new Result(supportsDistincts, supportsUdts);
    }

    private Set<QualifiedId> findUsedUDTs() throws SQLException {
        final Set<QualifiedId> usedUdts = new HashSet<>();

        val resultSet = dbMetaData.getColumns(null, "%", "%", "%");

        while (resultSet.next()) {
            val dataType = resultSet.getInt("DATA_TYPE");
            if ((dataType == Types.DISTINCT) || (dataType == Types.STRUCT)) {
                val fullyQualifiedTypeName = resultSet.getString("TYPE_NAME");

                try {
                    usedUdts.add(new QualifiedId(fullyQualifiedTypeName));
                } catch (ParseException pe) {
                    throw new SQLException(fullyQualifiedTypeName + " could not be parsed!", pe);
                }
            }
        }

        return usedUdts;
    }

    @Value
    private static class Result {
        boolean distinctsSupported;
        boolean udtsSupported;
    }
}
