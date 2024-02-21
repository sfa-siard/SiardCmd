package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.util.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;
import ch.admin.bar.siard2.cmd.model.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;
import lombok.val;

public class ArchiveMapping implements IdMapper {
  private Map<String,SchemaMapping> _mapSchemas = new HashMap<String,SchemaMapping>();
  public SchemaMapping getSchemaMapping(String sSchemaName) { return _mapSchemas.get(sSchemaName); }
  public String getMappedSchemaName(String sSchemaName) { return getSchemaMapping(sSchemaName).getMappedSchemaName(); }
  
  public ArchiveMapping(boolean bSupportsArrays, boolean bSupportsUdts,
    Map<String,String> mapSchemas, MetaData md,
    int iMaxTableNameLength, int iMaxColumnNameLength)
    throws IOException
  {
    for (int iSchema = 0; iSchema < md.getMetaSchemas(); iSchema++)
    {
      MetaSchema ms = md.getMetaSchema(iSchema);
      String sMappedSchemaName = mapSchemas.get(ms.getName());
      if (sMappedSchemaName == null)
        sMappedSchemaName = ms.getName();
      _mapSchemas.put(ms.getName(), 
        SchemaMapping.newInstance(bSupportsArrays, bSupportsUdts, 
          sMappedSchemaName, ms, iMaxTableNameLength, iMaxColumnNameLength));
    }
  } /* constructor */
  
  public static ArchiveMapping newInstance(boolean bSupportsArrays, boolean bSupportsUdts,
    Map<String,String> mapSchemas, 
    MetaData md, int iMaxTableNameLength, int iMaxColumnNameLength)
    throws IOException
  {
    return new ArchiveMapping(bSupportsArrays,bSupportsUdts,
      mapSchemas, md, iMaxTableNameLength, iMaxColumnNameLength);
  } /* newInstance */

  @Override
  public QualifiedColumnId map(QualifiedColumnId origQualifiedColumnId) {
    val sm = getSchemaMapping(origQualifiedColumnId.getSchema());
    if (sm == null) {
      return origQualifiedColumnId;
    }

    val builder = origQualifiedColumnId.toBuilder()
            .schema(sm.getMappedSchemaName());

    val tm = sm.getTableMapping(origQualifiedColumnId.getTable());
    if (tm == null) {
      return builder.build();
    }

    builder.table(tm.getMappedTableName());

    val mappedColumnName = tm.getMappedColumnName(origQualifiedColumnId.getColumn());
    if (mappedColumnName == null) {
      return builder.build();
    }

    return builder
            .column(mappedColumnName)
            .build();
  }

  @Override
  public QualifiedTableId map(QualifiedTableId orig) {
    val sm = getSchemaMapping(orig.getSchema());
    if (sm == null) {
      return orig;
    }

    val builder = orig.toBuilder()
            .schema(sm.getMappedSchemaName());

    val tm = sm.getTableMapping(orig.getTable());
    if (tm == null) {
      return builder.build();
    }

    return builder
            .table(tm.getMappedTableName())
            .build();
  }
} /* class ArchiveMapping */
