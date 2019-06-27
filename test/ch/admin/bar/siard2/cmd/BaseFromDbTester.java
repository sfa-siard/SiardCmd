package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.sql.*;
import java.util.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.EU;
import ch.enterag.utils.jdbc.*;

public class BaseFromDbTester 
{

  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  /*------------------------------------------------------------------*/
  private void dropTypes(Connection conn, String sSchema)
    throws SQLException
  {
    DatabaseMetaData dmd = conn.getMetaData(); 
    ResultSet rsTypes = dmd.getUDTs(null, 
      ((BaseDatabaseMetaData)dmd).toPattern(sSchema), "%", 
      new int[] {Types.DISTINCT,Types.STRUCT});
    Set<QualifiedId> setTypes = new HashSet<QualifiedId>();
    while (rsTypes.next())
    {
      String sTypeName = rsTypes.getString("TYPE_NAME");
      QualifiedId qiType = new QualifiedId(null, sSchema, sTypeName);
      setTypes.add(qiType);
    }
    Statement stmt = conn.createStatement();
    while (!setTypes.isEmpty())
    {
      int iSize = setTypes.size();
      for (Iterator<QualifiedId> iterType = setTypes.iterator(); iterType.hasNext(); )
      {
        QualifiedId qiType = iterType.next();
        try
        {
          String sSql = "DROP TYPE "+qiType.format()+" RESTRICT";
          int iResult = stmt.executeUpdate(sSql);
          if (iResult == 0)
          {
            System.out.println("Type "+qiType.format()+" dropped.");
            iterType.remove();
          }
          else
          	fail("Type "+qiType.format()+" NOT dropped!");
        }
        catch(SQLException se) { System.out.println("Type "+qiType.format()+" NOT dropped ("+EU.getExceptionMessage(se)+")!"); }
      }
      if (iSize == setTypes.size())
        throw new SQLException("Types "+setTypes.toString()+" could not be dropped!");
    }
    stmt.close();
    rsTypes.close();
    conn.commit();
  } /* dropTypes */
  
  /*------------------------------------------------------------------*/
  private void dropTables(Connection conn, String sSchema, String sType)
    throws SQLException
  {
    Statement stmt = conn.createStatement();
    DatabaseMetaData dmd = conn.getMetaData();
    ResultSet rsTables = dmd.getTables(null, 
      ((BaseDatabaseMetaData)dmd).toPattern(sSchema), "%", 
      new String[] {sType});
    while (rsTables.next())
    {
      String sTableName = rsTables.getString("TABLE_NAME");
      QualifiedId qiTable = new QualifiedId(null, sSchema, sTableName);
      String sSql = "DROP "+sType+" "+qiTable.format()+" CASCADE";
      int iResult = stmt.executeUpdate(sSql);
      if (iResult == 0)
        System.out.println(sType+" "+qiTable.format()+" dropped.");
      else
        fail(sType+" "+qiTable.format()+" NOT dropped!");
    }
    stmt.close();
    rsTables.close();
    conn.commit();
  } /* dropTables */
  
  protected void clearDatabase(Connection conn, String sDefaultSchema, String sDbSchema, String sSqlSchema, String sBlobSchema)
      throws SQLException
    {
      conn.setAutoCommit(false);
      dropTables(conn,sDefaultSchema,"VIEW");
      dropTables(conn,sDefaultSchema,"TABLE");
      dropTables(conn,sDbSchema,"VIEW");
      dropTables(conn,sDbSchema,"TABLE");
      dropTables(conn,sSqlSchema,"VIEW");
      dropTables(conn,sSqlSchema,"TABLE");
      dropTypes(conn,sDefaultSchema);
      dropTypes(conn,sDbSchema);
      dropTypes(conn,sSqlSchema);
      if (sBlobSchema != null)
      {
        dropTables(conn,sBlobSchema,"VIEW");
        dropTables(conn,sBlobSchema,"TABLE");
        dropTypes(conn,sBlobSchema);
      }
    } /* clearDatabase */

}
