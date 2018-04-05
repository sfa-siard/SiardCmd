package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.lang.*;
import ch.admin.bar.siard2.jdbc.*;

public class SiardFromDbTester
{
  private static final String _sACCESS_DB_USER;
  private static final String _sACCESS_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("access");
    _sACCESS_DB_USER = cp.getUser();
    _sACCESS_DB_PASSWORD = cp.getPassword();
  }
  
  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  public static void listDrivers()
  {
    for (Enumeration<Driver> enumDrivers = DriverManager.getDrivers(); enumDrivers.hasMoreElements(); )
    {
      Driver driver = enumDrivers.nextElement();
      System.out.println(driver.getClass().getName());
    }
  }
  
  private static final String _sMYSQL_EXE = "C:/Program Files/MySQL/MySQL Server 5.6/bin/mysql.exe";
  private static final String _sMYSQL_PASSWORD_ARG = "--password=rootpwd";
  private static final String _sMYSQL_USER_ARG = "--user=root";
  private static final String _sMYSQL_EXECUTE_OPT = "--execute";
  private static final String _sMYSQL_SAKILA_URL = MySqlDriver.getUrl("localhost:3306/sakila",true);
  private static final String _sMYSQL_SAKILA_USER = "sakilauser";
  private static final String _sMYSQL_SAKILA_PASSWORD = "sakilapwd";
  private static final String _sMYSQL_SAKILA_SIARD_FILE = "tmp/sfdbsakila.siard";
  private static final String _sMYSQL_SAKILA_SIARD_VIEWS_FILE = "tmp/sfdbsakilav.siard";
  private static final String _sMYSQL_SAKILA_METADATA_FILE = "tmp/sfdbsakila.xml";
  private static final File _fileMYSQL_SAKILA_SIARD_FINAL = new File("testfiles/sfdbsakila.siard");
  private static final File _fileMYSQL_SAKILA_SIARD_VIEWS_FINAL = new File("testfiles/sfdbsakilav.siard");
  
  @Test
  public void testSakilaFromDb()
  {
    System.out.println("testSakilaFromDb");
    if (Execute.isOsWindows())
    {
      try
      {
        String sDatabase = "sakila";
    
        String sCommand = "DROP DATABASE IF EXISTS "+sDatabase;
        Execute ex = Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, _sMYSQL_EXECUTE_OPT, sCommand});
        assertEquals("Database "+sDatabase+" could not be dropped!",0,ex.getResult());
        
        sCommand = "CREATE DATABASE "+sDatabase;
        ex = Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, _sMYSQL_EXECUTE_OPT, sCommand});
        assertEquals("Database "+sDatabase+" could not be created!",0,ex.getResult());
    
        File fileDump = new File("../Bugs/Testing_SIARD_Suite_2_0_59/dumps/Dump20170704_sakila.sql");
        Reader rdr = new FileReader(fileDump);
        Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, sDatabase},rdr);
        rdr.close();
        assertEquals("Database "+sDatabase+" could not be imported!",0,ex.getResult());
      
        String[] args = new String[]{
          "-o",
          "-j:"+_sMYSQL_SAKILA_URL,
          "-u:"+_sMYSQL_SAKILA_USER,
          "-p:"+_sMYSQL_SAKILA_PASSWORD,
          "-e:"+_sMYSQL_SAKILA_METADATA_FILE,
          "-s:"+_sMYSQL_SAKILA_SIARD_FILE
        };
        SiardFromDb sfdb = new SiardFromDb(args);
        assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
        if (!_fileMYSQL_SAKILA_SIARD_FINAL.exists())
          FU.copy(new File(_sMYSQL_SAKILA_SIARD_FILE),_fileMYSQL_SAKILA_SIARD_FINAL);
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
      catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
    }
  }

  @Test
  public void testSakilaFromDbViews()
  {
    System.out.println("testSakilaFromDb");
    if (Execute.isOsWindows())
    {
      try
      {
        String sDatabase = "sakila";
    
        String sCommand = "DROP DATABASE IF EXISTS "+sDatabase;
        Execute ex = Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, _sMYSQL_EXECUTE_OPT, sCommand});
        assertEquals("Database "+sDatabase+" could not be dropped!",0,ex.getResult());
        
        sCommand = "CREATE DATABASE "+sDatabase;
        ex = Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, _sMYSQL_EXECUTE_OPT, sCommand});
        assertEquals("Database "+sDatabase+" could not be created!",0,ex.getResult());
    
        File fileDump = new File("../Bugs/Testing_SIARD_Suite_2_0_59/dumps/Dump20170704_sakila.sql");
        Reader rdr = new FileReader(fileDump);
        Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, sDatabase},rdr);
        rdr.close();
        assertEquals("Database "+sDatabase+" could not be imported!",0,ex.getResult());
      
        String[] args = new String[]{
          "-o",
          "-v",
          "-j:"+_sMYSQL_SAKILA_URL,
          "-u:"+_sMYSQL_SAKILA_USER,
          "-p:"+_sMYSQL_SAKILA_PASSWORD,
          "-e:"+_sMYSQL_SAKILA_METADATA_FILE,
          "-s:"+_sMYSQL_SAKILA_SIARD_VIEWS_FILE
        };
        SiardFromDb sfdb = new SiardFromDb(args);
        assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
        if (!_fileMYSQL_SAKILA_SIARD_VIEWS_FINAL.exists())
          FU.copy(new File(_sMYSQL_SAKILA_SIARD_VIEWS_FILE),_fileMYSQL_SAKILA_SIARD_VIEWS_FINAL);
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
      catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
    }
  }

  private static final String _sMYSQL_ARCHIVESPACE_URL = MySqlDriver.getUrl("localhost:3306/archivespace",true);
  private static final String _sMYSQL_ARCHIVESPACE_USER = "asuser";
  private static final String _sMYSQL_ARCHIVESPACE_PASSWORD = "aspwd";
  private static final String _sMYSQL_ARCHIVESPACE_SIARD_FILE = "tmp/sfdbas.siard";
  private static final String _sMYSQL_ARCHIVESPACE_METADATA_FILE = "tmp/sfdbas.xml";
  private static final String _sMYSQL_ARCHIVESPACE_SIARD_FINAL = "testfiles/sfdbas.siard";
  
  @Test
  public void testArchiveSpaceFromDb()
  {
    System.out.println("testArchiveSpaceFromDb");
    if (Execute.isOsWindows())
    {
      try
      {
        String sDatabase = "archivespace";
    
        String sCommand = "DROP DATABASE IF EXISTS "+sDatabase;
        Execute ex = Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, _sMYSQL_EXECUTE_OPT, sCommand});
        assertEquals("Database "+sDatabase+" could not be dropped!",0,ex.getResult());
        
        sCommand = "CREATE DATABASE "+sDatabase;
        ex = Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, _sMYSQL_EXECUTE_OPT, sCommand});
        assertEquals("Database "+sDatabase+" could not be created!",0,ex.getResult());
    
        File fileDump = new File("../Bugs/Testing_SIARD_Suite_2_0_59/dumps/Dump20170704_archivesspace.sql");
        Reader rdr = new FileReader(fileDump);
        Execute.execute(new String[] {_sMYSQL_EXE, _sMYSQL_PASSWORD_ARG, _sMYSQL_USER_ARG, sDatabase},rdr);
        rdr.close();
        assertEquals("Database "+sDatabase+" could not be imported!",0,ex.getResult());
      
        String[] args = new String[]{
          "-o",
          "-j:"+_sMYSQL_ARCHIVESPACE_URL,
          "-u:"+_sMYSQL_ARCHIVESPACE_USER,
          "-p:"+_sMYSQL_ARCHIVESPACE_PASSWORD,
          "-e:"+_sMYSQL_ARCHIVESPACE_METADATA_FILE,
          "-s:"+_sMYSQL_ARCHIVESPACE_SIARD_FILE
        };
        SiardFromDb sfdb = new SiardFromDb(args);
        assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
        Files.copy(new File(_sMYSQL_ARCHIVESPACE_SIARD_FILE).toPath(), new File(_sMYSQL_ARCHIVESPACE_SIARD_FINAL).toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
      catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
    }
  }
  
  File fileBugDatabase = new File("../Bugs/445/Empty.accdb");
  File fileAccessDatabase = new File("tmp/Empty.accdb");
  String sAccessUrl = AccessDriver.getUrl(fileAccessDatabase.getAbsolutePath()); 
  String sBugSiardFile = "tmp/bug445.siard";
  String sBugMetaDataFile = "tmp/bug445.xml";
  @Test
  public void testBug445FromDb()
  {
    System.out.println("testBug445FromDb");
    if (Execute.isOsWindows())
    {
      try
      {
        Files.copy(fileBugDatabase.toPath(), fileAccessDatabase.toPath(), StandardCopyOption.REPLACE_EXISTING);
        String[] args = new String[]{
          "-o",
          "-j:"+sAccessUrl,
          "-u:"+_sACCESS_DB_USER,
          "-p:"+_sACCESS_DB_PASSWORD,
          "-e:"+sBugMetaDataFile,
          "-s:"+sBugSiardFile
        };
        SiardFromDb sfdb = new SiardFromDb(args);
        assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
        System.out.println("---------------------------------------");
      }
      catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
      catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
      catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
    }
  }
  
  String sBugUrl = OracleDriver.getUrl("localhost:1521:ORCL");
  String sBugUser = "BUGUSER";
  String sBugPwd = "bugpwd";
  String sBugFile = "tmp/bug450.siard";
  private void createBug450Db()
    throws SQLException
  {
    String sError = SiardConnection.getSiardConnection().loadDriver(sBugUrl);
    if ((sError == null) || (sError.length() == 0))
    {
      Connection conn = DriverManager.getConnection(sBugUrl, sBugUser, sBugPwd);
      if ((conn != null) && (!conn.isClosed()))
      {
        System.out.println("Connected to "+conn.getMetaData().getURL().toString());
        conn.setAutoCommit(true);
        Statement stmt = conn.createStatement().unwrap(Statement.class);
        stmt.execute("DELETE FROM TEST_ADDRESS");
        stmt.execute("DROP TABLE TEST_ADDRESS");
        stmt.execute("CREATE TABLE TEST_ADDRESS(ADD_ID NUMBER(15),ADD_LINE1 VARCHAR2(255 Char))");
        stmt.execute("INSERT INTO TEST_ADDRESS(ADD_ID, ADD_LINE1) VALUES(5326860,'?' || CHR(20) || '?' || CHR(20))");
        conn.close();
      }
      else
        fail("Connection to "+sBugUrl+" failed!");
    }
    else
      fail("Invalid URL "+sBugUrl+" ("+sError+")!");
  } /* createBug450Db */
  
  @Test
  public void testBug450FromDb()
  {
    System.out.println("testBug450FromDb");
    try
    {
      createBug450Db();
      String[] args = new String[]{
        "-o",
        "-j:"+sBugUrl,
        "-u:"+sBugUser,
        "-p:"+sBugPwd,
        "-s:"+sBugFile
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  }
  
  
  @Test
  public void testHelp()
  {
    System.out.println("testHelp");
    try
    {
      String[] args = new String[]{
        "-h"
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",4, sfdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  }

}
