package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.db2.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class Db2FromDbTester extends BaseFromDbTester 
{

  private static final String _sDB2_DB_URL;
  private static final String _sDB2_DB_USER;
  private static final String _sDB2_DB_PASSWORD;
  private static final String _sDB2_DBA_USER;
  private static final String _sDB2_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("db2");
    _sDB2_DB_URL = Db2Driver.getUrl(cp.getHost()+":"+cp.getPort()+"/"+cp.getCatalog());
    _sDB2_DB_USER = cp.getUser();
    _sDB2_DB_PASSWORD = cp.getPassword();
    _sDB2_DBA_USER = cp.getDbaUser();
    _sDB2_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sDB2_SIARD_FILE = "tmp/sfdbdb2.siard";
  private static final String _sDB2_METADATA_FILE = "tmp/sfdbdb2.xml";
  private static final File _fileDB2_SIARD_FINAL = new File("testfiles/sfdbdb2.siard");
  
  @Test
  public void testDb2FromDb()
  {
    System.out.println("testDb2FromDb");
    try
    {
      Db2DataSource dsDb2 = new Db2DataSource();
      dsDb2.setUrl(_sDB2_DB_URL);
      dsDb2.setUser(_sDB2_DBA_USER);
      dsDb2.setPassword(_sDB2_DBA_PASSWORD);
      Db2Connection connDb2 = (Db2Connection)dsDb2.getConnection();
      /* drop and create the test database */
      clearDatabase(connDb2,
        _sDB2_DB_USER, // in DB/2 the default schema is the same as the DB user.
        TestDb2Database._sTEST_SCHEMA, 
        ch.admin.bar.siard2.db2.TestSqlDatabase._sTEST_SCHEMA);
      System.out.println("Create TestSqlDatabase");
      new ch.admin.bar.siard2.db2.TestSqlDatabase(connDb2,_sDB2_DB_USER);
      System.out.println("Create TestMySqlDatabase");
      new TestDb2Database(connDb2,_sDB2_DB_USER);
      connDb2.close();
      String[] args = new String[]{
        "-o",
        "-j:"+_sDB2_DB_URL,
        "-u:"+_sDB2_DB_USER,
        "-p:"+_sDB2_DB_PASSWORD,
        "-e:"+_sDB2_METADATA_FILE,
        "-s:"+_sDB2_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileDB2_SIARD_FINAL.exists())
        FU.copy(new File(_sDB2_SIARD_FILE),_fileDB2_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testDb2FromDb */

}
