package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.jdbc.*;

public class H2ToDbTester 
{
  private static final String _sH2_DB_URL;
  private static final String _sH2_DB_USER;
  private static final String _sH2_DB_PASSWORD;
  static
  {
    _sH2_DB_URL = H2Driver.getUrl("testfiles/conn");
    _sH2_DB_USER = "sa";
    _sH2_DB_PASSWORD = "sapwd";
  }
  private static final String _sH2_SAMPLE_FILE = "testfiles/sample.siard";
  private static final String _sH2_SIARD_FILE = "testfiles/sfdbh2.siard";

  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  @Test
  public void testH2ToH2()
  {
    System.out.println("testH2ToH2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sH2_DB_URL,
        "-u:"+_sH2_DB_USER,
        "-p:"+_sH2_DB_PASSWORD,
        "-s:"+_sH2_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testH2ToH2 */
  
  @Test
  public void testSampleToH2()
  {
    System.out.println("testSampleToH2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sH2_DB_URL,
        "-u:"+_sH2_DB_USER,
        "-p:"+_sH2_DB_PASSWORD,
        "-s:"+_sH2_SAMPLE_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSampleToH2 */
  
}
