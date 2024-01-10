package ch.admin.bar.siard2.cmd.utils.siard.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class QualifiedTableId {
    @NonNull Id<SchemaMetaData> schemaId;
    @NonNull Id<TableMetaData> tableId;
}
