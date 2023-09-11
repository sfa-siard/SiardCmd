package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.*;
import ch.enterag.utils.lang.Execute;

public class SiardToDbTester
{
  
  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  private static final String _sMYSQL_SAKILA_CATALOG = "sakila";
  private static final String _sMYSQL_SAKILA_URL = MySqlDriver.getUrl("localhost:3306/"+_sMYSQL_SAKILA_CATALOG,true);
  private static final String _sMYSQL_SAKILA_USER = "sakilauser";
  private static final String _sMYSQL_SAKILA_PASSWORD = "sakilapwd";
  private static final String _sMYSQL_SAKILA_SIARD_FILE = "testfiles/sfdbsakila.siard";
  
  @Test
  public void testSakilaToSakila()
  {
    System.out.println("testSakilaToDb");
    if (Execute.isOsWindows())
    {
      try
      {
        String[] args = new String[]{
          "-o",
          "-j:"+_sMYSQL_SAKILA_URL,
          "-u:"+_sMYSQL_SAKILA_USER,
          "-p:"+_sMYSQL_SAKILA_PASSWORD,
          "-s:"+_sMYSQL_SAKILA_SIARD_FILE,
          "sakila", _sMYSQL_SAKILA_CATALOG
        };
        SiardToDb stdb = new SiardToDb(args);
        assertEquals("SiardToDb failed!",0, stdb.getReturn());
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    }
  }

  private static final String _sMYSQL_ARCHIVESPACE_CATALOG = "archivespace";
  private static final String _sMYSQL_ARCHIVESPACE_URL = MySqlDriver.getUrl("localhost:3306/"+_sMYSQL_ARCHIVESPACE_CATALOG,true);
  private static final String _sMYSQL_ARCHIVESPACE_USER = "asuser";
  private static final String _sMYSQL_ARCHIVESPACE_PASSWORD = "aspwd";
  private static final String _sMYSQL_ARCHIVESPACE_SIARD_FILE = "testfiles/sfdbas.siard";
  
  @Test
  public void testArchiveSpaceToArchivespace()
  {
    System.out.println("testArchiveToDb");
    if (Execute.isOsWindows())
    {
	    try
	    {
	      String[] args = new String[]{
	        "-o",
	        "-j:"+_sMYSQL_ARCHIVESPACE_URL,
	        "-u:"+_sMYSQL_ARCHIVESPACE_USER,
	        "-p:"+_sMYSQL_ARCHIVESPACE_PASSWORD,
	        "-s:"+_sMYSQL_ARCHIVESPACE_SIARD_FILE,
	        "archivespace", _sMYSQL_ARCHIVESPACE_CATALOG
	      };
	      SiardToDb stdb = new SiardToDb(args);
	      assertEquals("SiardToDb failed!",0, stdb.getReturn());
	      System.out.println("---------------------------------------");
	    }
	    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
	    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    }
  }

}
