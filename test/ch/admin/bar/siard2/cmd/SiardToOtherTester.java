package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.h2.*;
import ch.admin.bar.siard2.mssql.*;
import ch.admin.bar.siard2.oracle.*;
import ch.admin.bar.siard2.mysql.*;
import ch.admin.bar.siard2.db2.*;

public class SiardToOtherTester
{
  private static final String _sH2_DB_URL;
  private static final String _sH2_DB_USER;
  private static final String _sH2_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("h2");
    _sH2_DB_URL = "jdbc:h2:"+cp.getInstance()+"/"+cp.getCatalog();
    _sH2_DB_USER = cp.getUser();
    _sH2_DB_PASSWORD = cp.getPassword();
  }
  private static final String _sH2_SIARD_FILE = "testfiles\\sfdbh2.siard";

  private static final String _sMSSQL_DB_URL;
  private static final String _sMSSQL_DB_USER;
  private static final String _sMSSQL_DB_PASSWORD;
  private static final String _sMSSQL_DB_CATALOG;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("mssql");
    _sMSSQL_DB_CATALOG = cp.getCatalog();
    _sMSSQL_DB_URL = "jdbc:sqlserver://"+cp.getHost()+"\\"+_sMSSQL_DB_CATALOG+":"+cp.getPort();
    _sMSSQL_DB_USER = cp.getUser();
    _sMSSQL_DB_PASSWORD = cp.getPassword();
  }
  // private static final String _sMSSQL_SIARD_FILE = "testfiles\\sfdbmssql.siard";
  private static final String _sMSSQL_SIARD_FILE = "testfiles\\sfdbmssql.siard";
  
  private static final String _sORACLE_DB_URL;
  private static final String _sORACLE_DB_USER;
  // private static final String _sORACLE_DB_PASSWORD;
  private static final String _sORACLE_DBA_USER;
  private static final String _sORACLE_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("oracle");
    _sORACLE_DB_URL = "jdbc:oracle:thin:@" + cp.getHost() + ":" + cp.getPort() + ":" + cp.getInstance();
    _sORACLE_DB_USER = cp.getUser();
    // _sORACLE_DB_PASSWORD = cp.getPassword();
    _sORACLE_DBA_USER = cp.getDbaUser();
    _sORACLE_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sORACLE_SIARD_FILE = "testfiles\\sfdboracle.siard";

  private static final String _sMYSQL_DB_URL;
  private static final String _sMYSQL_DB_CATALOG;
  private static final String _sMYSQL_DB_USER;
  private static final String _sMYSQL_DB_PASSWORD;
  // private static final String _sMYSQL_DBA_USER;
  // private static final String _sMYSQL_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("mysql");
    _sMYSQL_DB_CATALOG = cp.getCatalog();
    _sMYSQL_DB_URL = "jdbc:mysql://"+cp.getHost()+":"+cp.getPort()+"/"+_sMYSQL_DB_CATALOG;
    _sMYSQL_DB_USER = cp.getUser();
    _sMYSQL_DB_PASSWORD = cp.getPassword();
    // _sMYSQL_DBA_USER = cp.getDbaUser();
    // _sMYSQL_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sMYSQL_SIARD_FILE = "logs\\sfdbmysql.siard";
  
  private static final String _sDB2_DB_URL;
  private static final String _sDB2_DB_CATALOG;
  private static final String _sDB2_DB_USER;
  private static final String _sDB2_DB_PASSWORD;
  // private static final String _sDB2_DBA_USER;
  // private static final String _sDB2_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("db2");
    _sDB2_DB_CATALOG = cp.getCatalog();
    _sDB2_DB_URL = "jdbc:db2://"+cp.getHost()+":"+cp.getPort()+"/"+_sDB2_DB_CATALOG;
    _sDB2_DB_USER = cp.getUser();
    _sDB2_DB_PASSWORD = cp.getPassword();
    // _sDB2_DBA_USER = cp.getDbaUser();
    // _sDB2_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sDB2_SIARD_FILE = "logs\\sfdbdb2.siard";

  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc\\jdbcdrivers.properties");
  }
  
  @Test
  public void testMsSqlToH2()
  {
    System.out.println("testMsSqlToH2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sH2_DB_URL,
        "-u:"+_sH2_DB_USER,
        "-p:"+_sH2_DB_PASSWORD,
        "-s:"+_sMSSQL_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMsSqlToH2 */
  
  @Test
  public void testOracleToH2()
  {
    System.out.println("testOracleToH2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sH2_DB_URL,
        "-u:"+_sH2_DB_USER,
        "-p:"+_sH2_DB_PASSWORD,
        "-s:"+_sORACLE_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testOracleToH2 */
  
  @Test
  public void testMySqlToH2()
  {
    System.out.println("testMySqlToH2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sH2_DB_URL,
        "-u:"+_sH2_DB_USER,
        "-p:"+_sH2_DB_PASSWORD,
        "-s:"+_sMYSQL_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMySqlToH2 */
  
  @Test
  public void testDb2ToH2()
  {
    System.out.println("testDb2ToH2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sH2_DB_URL,
        "-u:"+_sH2_DB_USER,
        "-p:"+_sH2_DB_PASSWORD,
        "-s:"+_sDB2_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testDb2ToH2 */
  
  @Test
  public void testOracleToMsSql()
  {
    System.out.println("testOracleToMsSql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-s:"+_sORACLE_SIARD_FILE,
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testOracleToMsSql */

  @Test
  public void testH2ToMsSql()
  {
    System.out.println("testH2ToMsSql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-s:"+_sH2_SIARD_FILE,
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testH2ToMsSql */

  @Test
  public void testMySqlToMsSql()
  {
    System.out.println("testMySqlToMsSql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-s:"+_sMYSQL_SIARD_FILE,
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMySqlToMsSql */
  
  @Test
  public void testDb2ToMsSql()
  {
    System.out.println("testDb2ToMsSql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-s:"+_sDB2_SIARD_FILE,
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testDb2ToMsSql */

  @Test
  public void testMsSqlToOracle()
  {
    System.out.println("testMsSqlToDb");
    try
    {
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sORACLE_DB_URL,
        "-u:"+_sORACLE_DBA_USER,
        "-p:"+_sORACLE_DBA_PASSWORD,
        "-s:"+_sMSSQL_SIARD_FILE,
        "SampleSchema", _sORACLE_DB_USER,
        TestMsSqlDatabase._sTEST_SCHEMA, TestOracleDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.mssql.TestSqlDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.oracle.TestSqlDatabase._sTEST_SCHEMA
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMsSqlToOracle */

  @Test
  public void testH2ToOracle()
  {
    System.out.println("testH2ToDb");
    try
    {
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sORACLE_DB_URL,
        "-u:"+_sORACLE_DBA_USER,
        "-p:"+_sORACLE_DBA_PASSWORD,
        "-s:"+_sH2_SIARD_FILE,
        "SampleSchema", _sORACLE_DB_USER,
        TestH2Database._sTEST_SCHEMA, TestOracleDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.h2.TestSqlDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.oracle.TestSqlDatabase._sTEST_SCHEMA
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testH2ToOracle */

  @Test
  public void testMySqlToOracle()
  {
    System.out.println("testMySqlToOracle");
    try
    {
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sORACLE_DB_URL,
        "-u:"+_sORACLE_DBA_USER,
        "-p:"+_sORACLE_DBA_PASSWORD,
        "-s:"+_sMYSQL_SIARD_FILE,
        "SampleSchema", _sORACLE_DB_USER,
        TestMySqlDatabase._sTEST_SCHEMA, TestOracleDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.mysql.TestSqlDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.oracle.TestSqlDatabase._sTEST_SCHEMA
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMySqlToOracle */

  @Test
  public void testDb2ToOracle()
  {
    System.out.println("testDb2ToOracle");
    try
    {
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sORACLE_DB_URL,
        "-u:"+_sORACLE_DBA_USER,
        "-p:"+_sORACLE_DBA_PASSWORD,
        "-s:"+_sDB2_SIARD_FILE,
        "SampleSchema", _sORACLE_DB_USER,
        TestDb2Database._sTEST_SCHEMA, TestOracleDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.db2.TestSqlDatabase._sTEST_SCHEMA,
        ch.admin.bar.siard2.oracle.TestSqlDatabase._sTEST_SCHEMA
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testDb2ToOracle */

  @Test
  public void testMsSqlToMySql()
  {
    System.out.println("testMsSqlToMySql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-s:"+_sMSSQL_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMsSqlToMySql */
  
  @Test
  public void testOracleToMySql()
  {
    System.out.println("testOracleToMySql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-s:"+_sORACLE_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testOracleToMySql */
  
  @Test
  public void testDb2ToMySql()
  {
    System.out.println("testDb2ToMySql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-s:"+_sDB2_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testDb2ToMySql */
  
  @Test
  public void testH2ToMySql()
  {
    System.out.println("testH2ToMySql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-s:"+_sH2_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testH2ToMySql */
  
  @Test
  public void testMsSqlToDb2()
  {
    System.out.println("testMsSqlToDb2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sDB2_DB_URL,
        "-u:"+_sDB2_DB_USER,
        "-p:"+_sDB2_DB_PASSWORD,
        "-s:"+_sMSSQL_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMsSqlToDb2 */
  
  @Test
  public void testOracleToDb2()
  {
    System.out.println("testOracleToDb2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sDB2_DB_URL,
        "-u:"+_sDB2_DB_USER,
        "-p:"+_sDB2_DB_PASSWORD,
        "-s:"+_sORACLE_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testOracleToDb2 */
  
  @Test
  public void testMySqlToDb2()
  {
    System.out.println("testMySqlToDb2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sDB2_DB_URL,
        "-u:"+_sDB2_DB_USER,
        "-p:"+_sDB2_DB_PASSWORD,
        "-s:"+_sMYSQL_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMySqlToDb2 */
  
  @Test
  public void testH2ToDb2()
  {
    System.out.println("testH2ToDb2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sDB2_DB_URL,
        "-u:"+_sDB2_DB_USER,
        "-p:"+_sDB2_DB_PASSWORD,
        "-s:"+_sH2_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testH2ToDb2 */

  // execute a single DDL statement
  // N.B.: It is important that AutoCommit on connection is set to false!
  private static void executeDdl(Connection conn, String sSql)
    throws SQLException
  {
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      int iResult = stmt.executeUpdate(sSql);
      System.out.println(sSql+": "+String.valueOf(iResult));
    }
    catch(SQLException se) { System.err.println(sSql+": "+EU.getExceptionMessage(se)); }
    finally { if (stmt != null) stmt.close(); }
    conn.commit();
  } /* executeDdl */
  // collect types from View/Table
  private void collectTypes(BaseDatabaseMetaData bdmd, QualifiedId qiViewOrTable, 
    Set<QualifiedId> setTypes)
    throws SQLException
  {
    ResultSet rsColumns = bdmd.getColumns(qiViewOrTable.getCatalog(),
      bdmd.toPattern(qiViewOrTable.getSchema()),
      bdmd.toPattern(qiViewOrTable.getName()),
      "%");
    while (rsColumns.next())
    {
      int iDataType = rsColumns.getInt("DATA_TYPE");
      if ((iDataType == Types.DISTINCT) || (iDataType == Types.STRUCT))
      {
        String sTypeName = rsColumns.getString("TYPE_NAME");
        try
        {
          QualifiedId qiType = new QualifiedId(sTypeName);
          setTypes.add(qiType);
        }
        catch(ParseException pe) {System.err.println(EU.getExceptionMessage(pe));}
      }
    }
    rsColumns.close();
  } /* collectTypes */
  // delete all content from a database
  private void cleanDb(String sUrl, String sUser, String sPassword)
    throws SQLException
  {
    MsSqlDataSource dsMsSql = new MsSqlDataSource();
    dsMsSql.setUrl(sUrl);
    dsMsSql.setUser(sUser);
    dsMsSql.setPassword(sPassword);
    Connection conn = null;
    try
    {
      conn = DriverManager.getConnection(sUrl,sUser,sPassword);
      conn.setAutoCommit(false);
      BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData)conn.getMetaData();
      Set<QualifiedId> setTypes = new HashSet<QualifiedId>();
      // delete all views
      ResultSet rsViews = bdmd.getTables(null, "%", "%", new String[] {"VIEW"});
      while (rsViews.next())
      {
        QualifiedId qiView = new QualifiedId(null,rsViews.getString("TABLE_SCHEM"),rsViews.getString("TABLE_NAME"));
        collectTypes(bdmd,qiView,setTypes);
        executeDdl(conn,"DROP VIEW "+qiView.format()+" RESTRICT");
      }
      rsViews.close();
      // delete all tables
      ResultSet rsTable = bdmd.getTables(null, "%", "%", new String[] {"TABLE"});
      while (rsTable.next())
      {
        QualifiedId qiTable = new QualifiedId(null,rsTable.getString("TABLE_SCHEM"),rsTable.getString("TABLE_NAME"));
        collectTypes(bdmd,qiTable,setTypes);
        executeDdl(conn,"DROP TABLE "+qiTable.format()+" CASCADE");
      }
      rsTable.close();
      // delete all types
      for (Iterator<QualifiedId> iterType = setTypes.iterator(); iterType.hasNext(); )
      {
        QualifiedId qiType = iterType.next();
        executeDdl(conn,"DROP TYPE "+qiType.format()+" RESTRICT");
      }
    }
    catch(SQLException se) 
    {
      String sException = EU.getExceptionMessage(se);
      System.err.println(sException);
      fail(sException); 
    }
    finally
    { 
      if (conn != null) 
        conn.close();
    }
  } /* cleanDb */

  String sBUG_MYSQL_CATALOG = "bugdb";
  String sBUG_MYSQL_URL = MsSqlDriver.getUrl("localhost\\"+sBUG_MYSQL_CATALOG+":1433");
  String sBUG_MYSQL_USER = "buglogin";
  String sBUG_MYSQL_PASSWORD = "bugpwd";
  @Test
  public void testSakilaToMsSql()
  {
    System.out.println("testSakilaToMsSql");
    {
      try
      {
        cleanDb(sBUG_MYSQL_URL, sBUG_MYSQL_USER, sBUG_MYSQL_PASSWORD);
        String sSiardFile = "testfiles\\sfdbsakila.siard";
        /* now upload sakila */
        String[] args = new String[]{
          "-o",
          "-j:"+sBUG_MYSQL_URL,
          "-u:"+sBUG_MYSQL_USER,
          "-p:"+sBUG_MYSQL_PASSWORD,
          "-s:"+sSiardFile
        };
        SiardToDb stdb = new SiardToDb(args);
        assertEquals("SiardToDb failed!",0, stdb.getReturn());
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) 
      {
        String sException = EU.getExceptionMessage(se);
        System.err.println(sException);
        fail(sException); 
      }
    }
  }

  String sBUG_ORACLE_URL = OracleDriver.getUrl("localhost:1521:ORCL");
  String sBUG_ORACLE_USER = "BUGUSER";
  String sBUG_ORACLE_PASSWORD = "bugpwd";
  @Test
  public void testTestToOracle()
  {
    System.out.println("testTestToOracle");
    {
      try
      {
        cleanDb(sBUG_ORACLE_URL, sBUG_ORACLE_USER, sBUG_ORACLE_PASSWORD);
        String sSiardFile = "testfiles\\test.siard";
        /* now upload test */
        String[] args = new String[]{
          "-o",
          "-j:"+sBUG_ORACLE_URL,
          "-u:"+sBUG_ORACLE_USER,
          "-p:"+sBUG_ORACLE_PASSWORD,
          "-s:"+sSiardFile,
          "admin", sBUG_ORACLE_USER
        };
        SiardToDb stdb = new SiardToDb(args);
        assertEquals("SiardToDb failed!",0, stdb.getReturn());
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) 
      {
        String sException = EU.getExceptionMessage(se);
        System.err.println(sException);
        fail(sException); 
      }
    }
  }
  @Test
  public void testSakilaToOracle()
  {
    System.out.println("testSakilaToOracle");
    {
      try
      {
        cleanDb(sBUG_ORACLE_URL, sBUG_ORACLE_USER, sBUG_ORACLE_PASSWORD);
        String sSiardFile = "testfiles\\sfdbsakila.siard";
        /* now upload sakila */
        String[] args = new String[]{
          "-o",
          "-j:"+sBUG_ORACLE_URL,
          "-u:"+sBUG_ORACLE_USER,
          "-p:"+sBUG_ORACLE_PASSWORD,
          "-s:"+sSiardFile,
          "sakila", sBUG_ORACLE_USER
        };
        SiardToDb stdb = new SiardToDb(args);
        assertEquals("SiardToDb failed!",0, stdb.getReturn());
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) 
      {
        String sException = EU.getExceptionMessage(se);
        System.err.println(sException);
        fail(sException); 
      }
    }
  }
  
}
