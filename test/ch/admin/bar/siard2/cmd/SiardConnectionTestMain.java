package ch.admin.bar.siard2.cmd;

public class SiardConnectionTestMain
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    SiardConnection sc = SiardConnection.getSiardConnection();
    String[] asScheme = sc.getSchemes();
    for (int i = 0; i < asScheme.length; i++)
    {
      String sScheme = asScheme[i];
      System.out.println(sScheme+": "+sc.getTitle(sScheme)+", "+
        sc.getDriverClass(sScheme)+", "+
        sc.getSampleUrl(sScheme,"dbserver.enterag.ch","D:\\dbfolder","testdb"));
    }
  }

}
