package ch.admin.bar.siard2.cmd.mapping;

import ch.admin.bar.siard2.cmd.model.QualifiedTableId;

public interface TableIdMapper {
    QualifiedTableId map(QualifiedTableId origQualifiedTableId);
}
