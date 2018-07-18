package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import org.junit.*;

import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.mysql.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class MySqlFromDbTester extends BaseFromDbTester
{

  private static final String _sMYSQL_DB_URL;
  private static final String _sMYSQL_DB_USER;
  private static final String _sMYSQL_DB_PASSWORD;
  private static final String _sMYSQL_DBA_USER;
  private static final String _sMYSQL_DBA_PASSWORD;
  private static List<String> _listPNGS = new ArrayList<String>();
  private static List<String> _listFLACS = new ArrayList<String>();
  static
  {
    ConnectionProperties cp = new ConnectionProperties("mysql");
    _sMYSQL_DB_URL = MySqlDriver.getUrl(cp.getHost()+":"+cp.getPort()+"/"+cp.getCatalog(),true);
    _sMYSQL_DB_USER = cp.getUser();
    _sMYSQL_DB_PASSWORD = cp.getPassword();
    _sMYSQL_DBA_USER = cp.getDbaUser();
    _sMYSQL_DBA_PASSWORD = cp.getDbaPassword();
    for (int iRecord = 0; cp.getBlobPng(iRecord) != null; iRecord++)
      _listPNGS.add(cp.getBlobPng(iRecord));
    for (int iRecord = 0; cp.getBlobFlac(iRecord) != null; iRecord++)
      _listFLACS.add(cp.getBlobFlac(iRecord));
  }
  private static final File _fileMYSQL_EXT_LOBS = new File("testfiles/lobs");
  private static final File _fileMYSQL_TMP_LOBS = new File("tmp/lobs");
  private static final File _fileMYSQL_TMP_PNG = new File("tmp/lobs/png");
  private static final File _fileMYSQL_TMP_FLAC = new File("tmp/lobs/flac");
  private static final String _sMYSQL_SIARD_FILE = "tmp/sfdbmysql.siard";
  private static final String _sMYSQL_METADATA_FILE = "tmp/sfdbmysql.xml";
  private static final String _sMYSQL_BLOB_TEMPLATE_FILE = "testfiles/mysqlblob.xml";
  private static final String _sMYSQL_BLOB_TMP_FILE = "tmp/mysqlblob.xml";
  private static final File _fileMYSQL_SIARD_FINAL = new File("testfiles/sfdbmysql.siard");
  static
  {
    System.setProperty("java.util.logging.config.file", "etc\\\\debug.properties");
    _fileMYSQL_TMP_LOBS.mkdirs();
    _fileMYSQL_TMP_PNG.mkdirs();
    _fileMYSQL_TMP_FLAC.mkdirs();
  }
  
  private void initializeTestDatabases()
    throws SQLException
  {
    MySqlDataSource dsMySql = new MySqlDataSource();
    dsMySql.setUrl(_sMYSQL_DB_URL);
    dsMySql.setUser(_sMYSQL_DBA_USER);
    dsMySql.setPassword(_sMYSQL_DBA_PASSWORD);
    MySqlConnection connMySql = (MySqlConnection)dsMySql.getConnection();
    // drop and create the test database
    clearDatabase(connMySql,
      "testschema",
      TestMySqlDatabase._sTEST_SCHEMA, 
      ch.admin.bar.siard2.mysql.TestSqlDatabase._sTEST_SCHEMA,
      TestBlobDatabase._sTEST_SCHEMA);
    System.out.println("Create TestSqlDatabase");
    new ch.admin.bar.siard2.mysql.TestSqlDatabase(connMySql);
    TestMySqlDatabase.grantSchemaUser(connMySql, ch.admin.bar.siard2.mysql.TestSqlDatabase._sTEST_SCHEMA, _sMYSQL_DB_USER);
    System.out.println("Create TestMySqlDatabase");
    new TestMySqlDatabase(connMySql);
    TestMySqlDatabase.grantSchemaUser(connMySql, TestMySqlDatabase._sTEST_SCHEMA, _sMYSQL_DB_USER);
    connMySql.commit();
    connMySql.close();
  }
  
  @Test
  public void testMySqlFromDb()
  {
    System.out.println("testMySqlFromDb");
    try
    {
      initializeTestDatabases();
      String[] args = new String[]{
        "-o",
        "-x:"+_fileMYSQL_TMP_LOBS.getPath(),
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-e:"+_sMYSQL_METADATA_FILE,
        "-s:"+_sMYSQL_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileMYSQL_SIARD_FINAL.exists())
      {
        FU.copy(new File(_sMYSQL_SIARD_FILE),_fileMYSQL_SIARD_FINAL);
        FU.copyFiles(_fileMYSQL_TMP_LOBS, _fileMYSQL_EXT_LOBS, true);
      }
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testMySqlFromDb */

  private void initializeBlobDatabase()
    throws SQLException, IOException
  {
    MySqlDataSource dsMySql = new MySqlDataSource();
    dsMySql.setUrl(_sMYSQL_DB_URL);
    dsMySql.setUser(_sMYSQL_DBA_USER);
    dsMySql.setPassword(_sMYSQL_DBA_PASSWORD);
    MySqlConnection connMySql = (MySqlConnection)dsMySql.getConnection();
    // drop and create the test database
    clearDatabase(connMySql,
      "testschema",
      TestMySqlDatabase._sTEST_SCHEMA, 
      ch.admin.bar.siard2.mysql.TestSqlDatabase._sTEST_SCHEMA,
      null);
    System.out.println("Create TestBlobDatabase");
    new TestBlobDatabase(connMySql,_listPNGS,_listFLACS);
    TestMySqlDatabase.grantSchemaUser(connMySql, TestBlobDatabase._sTEST_SCHEMA, _sMYSQL_DB_USER);
    connMySql.commit();
    connMySql.close();
  }

  @Test
  public void testMySqlBlobXmlFromDb()
  {
    System.out.println("testMySqlFromDb");
    try
    {
      initializeBlobDatabase();
      _fileMYSQL_TMP_PNG.mkdirs();
      _fileMYSQL_TMP_FLAC.mkdirs();
      System.out.println("Run SiardFromDb");
      String[] args = new String[]{
        "-o",
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-e:"+_sMYSQL_BLOB_TMP_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testMySqlFromDb */
  
  
  @Test
  public void testMySqlBlobsFromDb()
  {
    System.out.println("testMySqlFromDb");
    try
    {
      initializeBlobDatabase();
      _fileMYSQL_TMP_PNG.mkdirs();
      _fileMYSQL_TMP_FLAC.mkdirs();
      System.out.println("Run SiardFromDb");
      String[] args = new String[]{
        "-o",
        "-i:"+_sMYSQL_BLOB_TEMPLATE_FILE,
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-e:"+_sMYSQL_METADATA_FILE,
        "-s:"+_sMYSQL_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testMySqlFromDb */
  
}
