package ch.admin.bar.siard2.cmd;

import org.junit.*;

public class SiardConnectionTester {

  @Test
  public void test() 
  {
    /* the JUnit main resides not in the same place as the SiardDmc main */ 
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
    SiardConnection sc = SiardConnection.getSiardConnection();
    String[] asScheme = sc.getSchemes();
    for (int i = 0; i < asScheme.length; i++)
    {
      String sScheme = asScheme[i];
      System.out.println("\nScheme: "+sScheme);
      System.out.println("Title: "+sc.getTitle(sScheme));
      int iOptions = sc.getOptions(sScheme);
      if (iOptions > 1)
      {
	      for (int iOption = 0; iOption < iOptions; iOption++)
	        System.out.println("Sample URL("+sc.getOption(sScheme,iOption)+"): "+sc.getSampleUrl(sScheme,"dbserver.enterag.ch","D:\\dbfolder","testdb",iOption));
      }
      else
        System.out.println("Sample URL: "+sc.getSampleUrl(sScheme,"dbserver.enterag.ch","D:\\dbfolder","testdb"));
    }
  }

}
