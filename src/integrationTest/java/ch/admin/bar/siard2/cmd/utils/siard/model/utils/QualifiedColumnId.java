package ch.admin.bar.siard2.cmd.utils.siard.model.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class QualifiedColumnId {
    @NonNull Id<Metadata.Schema> schemaId;
    @NonNull Id<Metadata.Table> tableId;
    @NonNull Id<Metadata.Column> columnId;

    public QualifiedTableId getQualifiedTableId() {
        return QualifiedTableId.builder()
                .schemaId(schemaId)
                .tableId(tableId)
                .build();
    }
}
