package ch.admin.bar.siard2.cmd;

import java.util.regex.*;
import org.junit.Test;

public class ArrayConstructorTester
{

  @Test
  public void test()
  {
    Matcher m = Pattern.compile("^\\s*(.*?)\\s+ARRAY\\s*\\[\\s*(\\d+)\\s*\\]$").matcher("VARCHAR(256) ARRAY[ 34 ]");
    if (m.matches())
    {
      for (int i = 1; i <= m.groupCount(); i++)
      {
        System.out.println("\""+m.group(i)+"\"");
      }
    }
  }

}
