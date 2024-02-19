package ch.admin.bar.siard2.cmd.sql;

import ch.admin.bar.siard2.cmd.model.QualifiedTableId;

public class IdEncoder {

    public String encodeKeySensitive(final QualifiedTableId tableId) {
        return encodeKeySensitive(tableId.getSchema()) +
                "." +
                encodeKeySensitive(tableId.getTable());
    }

    public String encodeKeySensitive(final String value) {
        return "\"" +
                value +
                "\"";
    }
}
