package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.h2.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;

public class H2FromDbTester extends BaseFromDbTester 
{
  private static final String _sH2_DB_URL;
  private static final String _sH2_DB_USER;
  private static final String _sH2_DB_PASSWORD;
  static
  {
    _sH2_DB_URL = H2Driver.getUrl("tmp/conn");
    _sH2_DB_USER = "sa";
    _sH2_DB_PASSWORD = "sapwd";
  }
  private static final String _sH2_SIARD_FILE = "tmp/sfdbh2.siard";
  private static final String _sH2_METADATA_FILE = "tmp/sfdbh2.xml";
  private static final File _fileH2_SIARD_FINAL = new File("testfiles/sfdbh2.siard");

  @Test
  public void testH2FromDb()
  {
    System.out.println("testH2FromDb");
    try 
    {
      FU.copy(new File("testfiles/conn.h2.db"), new File("tmp/conn.h2.db"));
      FU.copy(new File("testfiles/conn.trace.db"), new File("tmp/conn.trace.db"));
      /* create the databases */
      H2DataSource dsH2 = new H2DataSource();
      dsH2.setUrl(_sH2_DB_URL);
      dsH2.setUser(_sH2_DB_USER);
      dsH2.setPassword(_sH2_DB_PASSWORD);
      Connection connH2 = (Connection)dsH2.getConnection();
      /* drop and create the test databases */
      clearDatabase(connH2,
        "PUBLIC",
        TestH2Database._sTEST_SCHEMA, 
        ch.admin.bar.siard2.h2.TestSqlDatabase._sTEST_SCHEMA,
        null);
      System.out.println("Create TestSqlDatabase");
      new ch.admin.bar.siard2.h2.TestSqlDatabase(connH2);
      System.out.println("Create TestH2Database");
      new TestH2Database(connH2);
      connH2.close();
      /* now download it */
      String[] args = new String[]{
        "-o",
        "-j:"+_sH2_DB_URL,
        "-u:"+_sH2_DB_USER,
        "-p:"+_sH2_DB_PASSWORD,
        "-e:"+_sH2_METADATA_FILE,
        "-s:"+_sH2_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileH2_SIARD_FINAL.exists())
        FU.copy(new File(_sH2_SIARD_FILE),_fileH2_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testH2FromDb */

}
