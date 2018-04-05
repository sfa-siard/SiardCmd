package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;

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
    BaseDatabaseMetaData dmd = (BaseDatabaseMetaData)conn.getMetaData(); 
    ResultSet rsTypes = dmd.getUDTs(null, 
      dmd.toPattern(sSchema), "%", 
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
        catch(SQLException se) {}
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
    BaseDatabaseMetaData dmd = (BaseDatabaseMetaData)conn.getMetaData(); 
    ResultSet rsTables = dmd.getTables(null, 
      dmd.toPattern(sSchema), "%", 
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
  
  protected void clearDatabase(Connection conn, String sDefaultSchema, String sDbSchema, String sSqlSchema)
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
    } /* clearDatabase */

}
