package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.postgres.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class PostgresDvdRentalTester extends BaseFromDbTester
{
  private static final String _sPOSTGRES_DB_URL;
  private static final String _sPOSTGRES_DB_USER;
  private static final String _sPOSTGRES_DB_PASSWORD;
  private static final String _sPOSTGRES_DBA_USER;
  private static final String _sPOSTGRES_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("postgres");
    _sPOSTGRES_DB_URL = PostgresDriver.getUrl(cp.getHost() + ":" + cp.getPort() + "/dvd_rental");
    _sPOSTGRES_DB_USER = "buguser";
    _sPOSTGRES_DB_PASSWORD = "bugpwd";
    _sPOSTGRES_DBA_USER = cp.getDbaUser();
    _sPOSTGRES_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sPOSTGRES_SIARD_FILE = "tmp/dvd_rental.siard";
  private static final String _sPOSTGRES_METADATA_FILE = "tmp/dvd_rental.xml";
  private static final File _filePOSTGRES_SIARD_FINAL = new File("testfiles/dvd_rental.siard");

  @Test
  public void testPostgresDvdRental()
  {
    System.out.println("testPostgresDvdRental");
    try
    {
		  PostgresDataSource dsPostgres = new PostgresDataSource();
		  dsPostgres.setUrl(_sPOSTGRES_DB_URL);
		  dsPostgres.setUser(_sPOSTGRES_DBA_USER);
		  dsPostgres.setPassword(_sPOSTGRES_DBA_PASSWORD);
		  PostgresConnection connPostgres = (PostgresConnection)dsPostgres.getConnection();
		  connPostgres.setAutoCommit(false);
		  /* grant ownership to dvd_rental */
		  TestPostgresDatabase.grantSchemaUser(connPostgres, "public", "buguser");
		  connPostgres.close();
      String[] args = new String[]{
        "-o",
        "-j:"+_sPOSTGRES_DB_URL,
        "-u:"+_sPOSTGRES_DB_USER,
        "-p:"+_sPOSTGRES_DB_PASSWORD,
        "-e:"+_sPOSTGRES_METADATA_FILE,
        "-s:"+_sPOSTGRES_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_filePOSTGRES_SIARD_FINAL.exists())
        FU.copy(new File(_sPOSTGRES_SIARD_FILE),_filePOSTGRES_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testPostgresDvdRental */
  
}
