package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;

public class OeFromDbTester extends BaseFromDbTester 
{
  private static final String _sOE_DB_URL;
  private static final String _sOE_DB_USER;
  private static final String _sOE_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("oe");
    _sOE_DB_URL = OracleDriver.getUrl(cp.getHost() + ":" + cp.getPort() + ":" + cp.getInstance());
    _sOE_DB_USER = cp.getUser();
    _sOE_DB_PASSWORD = cp.getPassword();
  }
  private static final String _sOE_SIARD_FILE = "tmp/sfdboe.siard";
  private static final String _sOE_METADATA_FILE = "tmp/sfdboe.xml";
  private static final File _fileOE_SIARD_FINAL = new File("testfiles/sfdboe.siard");
  private static final String _sPRODUCT_TABLE = "PRODUCT_INFORMATION";
  private static final String _sPICTURE_COLUMN = "PICTURE";
  private static final String _sSIARD_PNG = "testfiles/splash.png";

  @Test
  public void testOeExtend()
  {
    try
    {
      OracleDataSource dsOracle = new OracleDataSource();
      dsOracle.setUrl(_sOE_DB_URL);
      dsOracle.setUser(_sOE_DB_USER);
      dsOracle.setPassword(_sOE_DB_PASSWORD);
      Connection conn = dsOracle.getConnection();
      conn.setAutoCommit(false);
      BaseDatabaseMetaData dmd = (BaseDatabaseMetaData)conn.getMetaData();
      Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
      String sSql = null;
      int iResult = -1;
      ResultSet rsColumns = dmd.getColumns(null, 
        dmd.toPattern(_sOE_DB_USER),
        dmd.toPattern(_sPRODUCT_TABLE),
        dmd.toPattern(_sPICTURE_COLUMN));
      if (rsColumns.next())
      {
        sSql = "DELETE FROM "+_sPRODUCT_TABLE+" WHERE PRODUCT_ID = 4000";
        iResult = stmt.unwrap(Statement.class).executeUpdate(sSql);
        sSql = "ALTER TABLE "+_sPRODUCT_TABLE+" DROP COLUMN "+_sPICTURE_COLUMN;
        iResult = stmt.unwrap(Statement.class).executeUpdate(sSql);
      }
      rsColumns.close();
      sSql = "ALTER TABLE "+_sPRODUCT_TABLE+" ADD "+_sPICTURE_COLUMN + " BLOB";
      iResult = stmt.unwrap(Statement.class).executeUpdate(sSql);
      assertEquals("Add column failed!",0,iResult);
      sSql = "SELECT PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_STATUS, "+_sPICTURE_COLUMN+" FROM "+_sPRODUCT_TABLE;
      ResultSet rs = stmt.executeQuery(sSql);
      rs.next();
      rs.moveToInsertRow();
      rs.updateShort("PRODUCT_ID", (short)4000);
      rs.updateString("PRODUCT_NAME", "SIARD Suite 2.1");
      rs.updateString("PRODUCT_DESCRIPTION", "Software to load content of relational database from an RDBMS (Relational Database Management System) to a file in SIARD Format 2.1");
      rs.updateString("PRODUCT_STATUS", "orderable");
      InputStream is = new FileInputStream(_sSIARD_PNG);
      rs.updateBinaryStream(_sPICTURE_COLUMN, is);
      rs.insertRow();
      rs.moveToCurrentRow();
      is.close();
      rs.close();
      stmt.close();
      conn.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  @Test
  public void testOeFromDb()
  {
    System.out.println("testOeFromDb");
    try
    {
      String[] args = new String[]{
        "-o",
        "-j:"+_sOE_DB_URL,
        "-u:"+_sOE_DB_USER,
        "-p:"+_sOE_DB_PASSWORD,
        "-e:"+_sOE_METADATA_FILE,
        "-s:"+_sOE_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileOE_SIARD_FINAL.exists())
        FU.copy(new File(_sOE_SIARD_FILE),_fileOE_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testOeFromDb */
  
  @Test
  public void testOeFromDbXml()
  {
    System.out.println("testOeFromDbXml");
    try
    {
      String[] args = new String[]{
        "-o",
        "-j:"+_sOE_DB_URL,
        "-u:"+_sOE_DB_USER,
        "-p:"+_sOE_DB_PASSWORD,
        "-e:"+_sOE_METADATA_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testOeFromDb */
  
}
