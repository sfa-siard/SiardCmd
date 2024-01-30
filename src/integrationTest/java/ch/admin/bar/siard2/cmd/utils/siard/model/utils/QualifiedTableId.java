package ch.admin.bar.siard2.cmd.utils.siard.model.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class QualifiedTableId {
    @NonNull Id<Metadata.Schema> schemaId;
    @NonNull Id<Metadata.Table> tableId;
}
