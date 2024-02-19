package ch.admin.bar.siard2.cmd.mapping;

import ch.admin.bar.siard2.cmd.model.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;

/**
 * The {@code IdMapper} interface defines methods for mapping identifiers, such as table and column names, based on certain rules or user-defined mappings.
 * Implementations of this interface are responsible for transforming original qualified table and column identifiers into their mapped counterparts.
 * <p>
 * Identifiers may be mapped for various reasons, such as addressing technical constraints (e.g., maximum length of an ID) or accommodating user-defined mappings,
 * where users specify how IDs should be renamed.
 * </p>
 *
 * @see QualifiedTableId
 * @see QualifiedColumnId
 */
public interface IdMapper {

    /**
     * Maps the original qualified table identifier to its mapped counterpart.
     *
     * @param origQualifiedTableId The original qualified table identifier to be mapped.
     * @return The mapped qualified table identifier.
     */
    QualifiedTableId map(QualifiedTableId origQualifiedTableId);

    /**
     * Maps the original qualified column identifier to its mapped counterpart.
     *
     * @param origQualifiedColumnId The original qualified column identifier to be mapped.
     * @return The mapped qualified column identifier.
     */
    QualifiedColumnId map(QualifiedColumnId origQualifiedColumnId);
}

