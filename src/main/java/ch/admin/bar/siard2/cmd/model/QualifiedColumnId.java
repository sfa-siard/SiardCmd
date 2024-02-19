package ch.admin.bar.siard2.cmd.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class QualifiedColumnId {
    @NonNull String schema;
    @NonNull String table;
    @NonNull String column;

    public QualifiedTableId getQualifiedTableId() {
        return QualifiedTableId.builder()
                .schema(schema)
                .table(table)
                .build();
    }

    public static class QualifiedColumnIdBuilder {
        public QualifiedColumnIdBuilder qualifiedTable(final QualifiedTableId qualifiedTableId) {
            schema(qualifiedTableId.getSchema());
            table(qualifiedTableId.getTable());
            return this;
        }
    }
}
