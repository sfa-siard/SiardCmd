package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArchiveMapping {
    private final Map<String, SchemaMapping> schemas = new HashMap<String, SchemaMapping>();

    public SchemaMapping getSchemaMapping(String schemaName) {
        return schemas.get(schemaName);
    }

    public String getMappedSchemaName(String schemaName) {
        return getSchemaMapping(schemaName).getMappedSchemaName();
    }

    private ArchiveMapping(boolean supportsArrays, boolean supportsUdts, Map<String, String> schemas, MetaData metadata, int maxTableNameLength, int maxColumnNameLength) throws IOException {
        for (int i = 0; i < metadata.getMetaSchemas(); i++) {
            MetaSchema metaSchema = metadata.getMetaSchema(i);
            String mappedSchemaName = schemas.get(metaSchema.getName());
            if (mappedSchemaName == null) {
                mappedSchemaName = metaSchema.getName();
            }
            this.schemas.put(metaSchema.getName(), SchemaMapping.newInstance(supportsArrays, supportsUdts, mappedSchemaName, metaSchema, maxTableNameLength, maxColumnNameLength));
        }
    }

    public static ArchiveMapping newInstance(boolean supportsArrays, boolean supportsUdts, Map<String, String> schemas, MetaData metaData, int axTableNameLength, int maxColumnNameLength) throws IOException {
        return new ArchiveMapping(supportsArrays, supportsUdts, schemas, metaData, axTableNameLength, maxColumnNameLength);
    }
}
