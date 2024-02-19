package ch.admin.bar.siard2.cmd.mapping;

import ch.admin.bar.siard2.cmd.model.QualifiedColumnId;

public interface ColumnIdMapper {
    QualifiedColumnId map(QualifiedColumnId origQualifiedColumnId);
}
