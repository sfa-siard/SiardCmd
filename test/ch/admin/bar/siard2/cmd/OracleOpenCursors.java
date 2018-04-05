package ch.admin.bar.siard2.cmd;

import java.sql.*;
import ch.admin.bar.siard2.jdbc.*;

public class OracleOpenCursors
{
  public static void printOpenCursors(Connection conn)
  {
    if (conn.getClass().equals(OracleConnection.class))
    {
      String sSql = "SELECT SQL_TEXT FROM V$OPEN_CURSOR WHERE USER_NAME = USER AND CURSOR_TYPE='OPEN'";
      try
      {
        Statement stmt = null;
        try
        {
          stmt = conn.createStatement();
          ResultSet rs = null;
          try
          {
            rs = stmt.unwrap(Statement.class).executeQuery(sSql);
            while (rs.next())
            {
              String sSqlText = rs.getString("SQL_TEXT");
              if (!sSql.startsWith(sSqlText))
                System.out.println(sSqlText.replace('\n', ' ').replace('\r', ' ')+"\t");
            }
            System.out.println();
            rs.close();
            stmt.close();
          }
          finally
          {
            if ((rs != null) && (!rs.isClosed()))
              rs.close();
          }
        }
        finally
        {
          if ((stmt != null) && (!stmt.isClosed()))
            stmt.close();
        }
      }
      catch(SQLException se) {} // if not enough privileges etc. ignore
    } /* if Oracle connection */
  } /* printOpenCursors */
} /* class OracleOpenCursors */
