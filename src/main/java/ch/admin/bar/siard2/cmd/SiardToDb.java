/*== SiardToDb.java ====================================================
SiardFromDb loads the data from a siard file to a database instance.
Version     : $Id: SiardToDb.java 1922 2016-06-07 09:07:12Z hartwig $
Application : Siard2
Description : Loads the data from a siard file to a database instance.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 24.03.2008, Hartwig Thomas, Enter AG, Zurich
======================================================================*/

package ch.admin.bar.siard2.cmd;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.cmd.db.connector.ConnectorProperties;
import ch.admin.bar.siard2.cmd.db.connector.ConnectorsRegistry;
import ch.admin.bar.siard2.cmd.utils.ListAssembler;
import ch.admin.bar.siard2.cmd.utils.VersionsExplorer;
import ch.enterag.utils.EU;
import ch.enterag.utils.ProgramInfo;
import ch.enterag.utils.cli.Arguments;
import ch.enterag.utils.logging.IndentLogger;
import lombok.val;

/*====================================================================*/
/** Loads the data from a siard file to a database instance.
 @author Hartwig Thomas
 */
public class SiardToDb
{
  /*====================================================================
  (private) constants
  ====================================================================*/
	public static final int iRETURN_OK = 0;
    public static final int iRETURN_WARNING = 4;
    public static final int iRETURN_ERROR = 8;
    public static final int iRETURN_FATAL = 12;

  /*====================================================================
  (private) data members
  ====================================================================*/
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(SiardFromDb.class.getName());
  /** info */
  private static ProgramInfo _pi = ProgramInfo.getProgramInfo(
  		"SIARD Suite", VersionsExplorer.INSTANCE.getSiardVersion(),
      "SiardToDb",VersionsExplorer.INSTANCE.getAppVersion(),
      "Program to load database content from a .siard file",
  		"Swiss Federal Archives, Berne, Switzerland, 2008-2016",
      Arrays.asList((new String[] {"Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
        "Andreas Voss, Swiss Federal Archives, Berne, Switzerland",
        "Anders Bo Nielsen, Danish National Archives, Denmark",
        "Claire Röthlisberger-Jourdan, KOST, Berne, Switzerland"})),
      Arrays.asList((new String[] {"Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
              "Simon Jutz, Cytex GmbH, Zurich, Switzerland" })),
      Arrays.asList((new String[] {"Claudia Matthys, POOL Computer AG, Zurich, Switzerland",
              "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland",
              "Yvan Dutoit, Swiss Federal Archives, Berne, Switzerland"})),
      Arrays.asList((new String[] {"Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
              "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland",
              "Alain Mast, Swiss Federal Archives, Berne, Switzerland",
              "Krystyna Ohnesorge, Swiss Federal Archives, Berne, Switzerland"})));

  private int _iLoginTimeoutSeconds = SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS;
  private int _iQueryTimeoutSeconds = SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS;
  private boolean _bOverwrite = false;
  private String _sJdbcUrl = null;
  private String _sDatabaseUser = null;
  private String _sDatabasePassword = null;
  private File _fileSiard = null;
  private Map<String,String> _mapSchemas = new HashMap<String,String>();

  private Archive _archive = null;
  private Connection _conn = null;
  
  private int _iReturn = iRETURN_WARNING;
  public int getReturn() { return _iReturn; }
  
  /*====================================================================
  methods
  ====================================================================*/
	/*------------------------------------------------------------------*/
	/** prints and logs the string */
	private static void logPrint(String s)
	{
		_il.info(s);
		System.out.println(s);
	} /* logPrint */
	
	/*------------------------------------------------------------------*/
  /** prints usage information */
  private void printUsage()
  {
  	System.out.println("Usage:");
  	System.out.println("java -cp <siardpath>/lib/siardcmd.jar ch.admin.bar.siard2.cmd.SiardToDb [-h]");
    System.out.println("  [-o][-q=<query timeout>][-l=<login timeout>]");
    System.out.println("  -s=<siard file> -j=<JDBC URL> -u=<database user> -p=<database password>");
  	System.out.println("  [<schema> <mappedschema>]*");
    System.out.println("where");  	
    System.out.println("  <siardpath>         installation path of SiardCmd");    
    System.out.println("  -h (help)           prints this usage information");
    System.out.println("  -o                  overwrite existing database objects");
    System.out.println("  <login timeout>     login timeout in seconds (default: "+String.valueOf(_iLoginTimeoutSeconds)+")");    
    System.out.println("  <query timeout>     query timeout in seconds (default: "+String.valueOf(_iQueryTimeoutSeconds)+")");    
    System.out.println("  <JDBC URL>          JDBC URL of database to be uploaded");
    System.out.print("                      e.g. ");
    SiardConnection sc = SiardConnection.getSiardConnection();
    String[] asSchemes = sc.getSchemes();
    for (int i = 0; i < asSchemes.length; i++)
    {
      if (i > 0)
        System.out.println("                           ");
      String sScheme = asSchemes[i];
      System.out.println("for "+sc.getTitle(sScheme)+": "+sc.getSampleUrl(sScheme,"dbserver.enterag.ch","D:\\dbfolder","testdb"));
    }
    System.out.println("  <database user>     database user");    
    System.out.println("  <database password> database password");    
    System.out.println("  <siard file>        name of .siard file (will be overwritten. if it exists!)");
    System.out.println("  <schema>            schema name in SIARD file");
    System.out.println("  <mappedschema>      schema name to be used in database");
  } /* printUsage */

	/*------------------------------------------------------------------*/
	/** reads the parameters from the command line or from the config file.
	*/
	private void getParameters(String[] asArgs)
  {
	  _il.enter();
    _iReturn = iRETURN_OK;
    Arguments args = Arguments.newInstance(asArgs);
    if (args.getOption("h") != null)
      _iReturn = iRETURN_WARNING;
    /* login time out */
    String sLoginTimeoutSeconds = args.getOption("l");
    /* query time out */
    String sQueryTimeoutSeconds = args.getOption("q");
    /* overwrite */
    if (args.getOption("o") != null)
      _bOverwrite = true;
    /* JDBC URI */
    _sJdbcUrl = args.getOption("j");
    /* db user */
    _sDatabaseUser = args.getOption("u");
    /* db password */
    _sDatabasePassword = args.getOption("p");
    /* siard file */
    String sSiardFile = args.getOption("s");
    /* schema mapping */
    for (int i = 0; i < args.getArguments()/2; i++)
    {
      String sSchema = args.getArgument(2*i);
      String sMappedSchema = args.getArgument(2*i+1);
      _mapSchemas.put(sSchema, sMappedSchema); 
    }
    
    /* analyze the parameters */
    if (_iReturn == iRETURN_OK)
    {
      if (sLoginTimeoutSeconds != null)
      {
        try { _iLoginTimeoutSeconds = Integer.parseInt(sLoginTimeoutSeconds); }
        catch(NumberFormatException nfe) 
        { 
          System.err.println("Invalid login timeout: "+sLoginTimeoutSeconds+"!");
          _iReturn = iRETURN_ERROR;
        }
      }
      if (sQueryTimeoutSeconds != null)
      {
        try { _iQueryTimeoutSeconds = Integer.parseInt(sQueryTimeoutSeconds); }
        catch(NumberFormatException nfe) 
        { 
          System.err.println("Invalid query timeout: "+sQueryTimeoutSeconds+"!");
          _iReturn = iRETURN_ERROR;
        }
      }
      String sError = SiardConnection.getSiardConnection().loadDriver(_sJdbcUrl);
      if (sError != null)
      {
        System.err.println(sError);
        _iReturn = iRETURN_ERROR;
      }
      if (_sDatabaseUser == null)
      {
        System.err.println("Database user must be given!");
        _iReturn = iRETURN_ERROR;
      }
      if (_sDatabasePassword == null)
      {
        System.err.println("Database password must be given!");
        _iReturn = iRETURN_ERROR;
      }
      if (sSiardFile != null)
        _fileSiard = new File(sSiardFile);
      else
      {
        System.err.println("SIARD file must be given!");
        _iReturn = iRETURN_ERROR;
      }
      if ((args.getArguments() % 2) != 0)
      {
        System.err.println("Dangling schema name on command line!");
        _iReturn = iRETURN_ERROR;
      }
    }
  	/* print and log the parameters */
    if (_iReturn == iRETURN_OK)
    {
      logPrint("");
      logPrint("Parameters");
      logPrint("  SIARD file             : "+_fileSiard.getAbsolutePath());
      logPrint("  JDBC URL               : "+_sJdbcUrl);
      logPrint("  Database user          : "+_sDatabaseUser);
      logPrint("  Database password      : ***");
      if (sLoginTimeoutSeconds != null)
        logPrint("  Login timeout          : "+String.valueOf(_iLoginTimeoutSeconds)+" seconds");
      if (sQueryTimeoutSeconds != null)
        logPrint("  Query timeout          : "+String.valueOf(_iQueryTimeoutSeconds)+" seconds");
      if (_bOverwrite)
        logPrint("  Overwrite              : Database objects may be overwritten on upload");
      if (_mapSchemas.size() > 0)
      {
        for (Iterator<String> iterSchema = _mapSchemas.keySet().iterator(); iterSchema.hasNext(); )
        {
          String sSchema = iterSchema.next();
          String sMappedSchema = _mapSchemas.get(sSchema);
          logPrint("  Mapped Schema          : \""+sSchema+"\" => \""+sMappedSchema+"\"");
        }
      }
      logPrint("");
    }
    else
      printUsage();
	  _il.exit();
  } /* getParameters */

  /*====================================================================
  constructor
  ====================================================================*/
	/*------------------------------------------------------------------*/
	/** runs main program of SiardFromDb. */
	public SiardToDb(String asArgs[])
	  throws IOException, SQLException
  {
	  super();
	  _il.enter();
	  /* parameters */
    getParameters(asArgs);
    if (_iReturn == iRETURN_OK)
    {
      /* open SIARD file */
      _archive = ArchiveImpl.newInstance();
      _archive.open(_fileSiard);
      /* open connection */

      val connector = ConnectorsRegistry.INSTANCE.getConnector(ConnectorProperties.builder()
                      .jdbcUrl(_sJdbcUrl)
                      .user(_sDatabaseUser)
                      .password(_sDatabasePassword)
                      .queryTimeout(Duration.ofSeconds(_iQueryTimeoutSeconds))
                      .loginTimeout(Duration.ofSeconds(_iLoginTimeoutSeconds))
              .build());

      val dbFeatures = connector.getDbFeatures();

      val idMapper = new ArchiveMapping(
              dbFeatures.isArraysSupported(),
              dbFeatures.isUdtsSupported(),
              _mapSchemas,
              _archive.getMetaData(),
              dbFeatures.getMaxTableNameLength(),
              dbFeatures.getMaxColumnNameLength());

      val sqlExecutor = connector.createExecutor(idMapper);

      val metaDataToDb = new MetaDataToDb(_archive.getMetaData(), sqlExecutor, idMapper);
      val primaryDataToDb = new PrimaryDataToDb(_archive, connector, sqlExecutor, idMapper);

      if (!_bOverwrite && ((metaDataToDb.tablesDroppedByUpload() < 0) || (metaDataToDb.typesDroppedByUpload() > 0))) {
        System.err.println("Database objects exist which would be overwritten on upload!");
        System.err.println("Backup and delete them first or use -o option for overwriting them.");

        connector.close();
        _archive.close();
        _iReturn = iRETURN_WARNING;

        return;
      }

      if (!dbFeatures.isUdtsSupported() && containsUDTs(_archive.getMetaData())) {
        logPrint("Target database does not support UDTs. UDTs will be \"flattened\".\r\n");
      }

      metaDataToDb.upload(null);
      primaryDataToDb.upload(null);

      connector.close();
      _archive.close();
      _iReturn = iRETURN_OK;
    }
	  _il.exit();
  }

  private boolean containsUDTs(final MetaData metaData) {
    val nrOfUDTsInSiardArchive = ListAssembler.assemble(metaData.getMetaSchemas(), metaData::getMetaSchema).stream()
            .mapToInt(MetaSchema::getMetaTypes)
            .sum();

    return nrOfUDTsInSiardArchive > 0;
  }

  /*====================================================================
  factory
  ====================================================================*/
	/*------------------------------------------------------------------*/
	/** main entry point starts logging and creates running instance.
	@param asArgs command-line arguments are ignored
	*/
	public static void main(String[] asArgs)
	{
		int iReturn = iRETURN_WARNING;
		try
		{
			_pi.printStart();
	    _pi.logStart();
	    _il.systemProperties();
	    /* run application */
	    SiardToDb stdb = new SiardToDb(asArgs);
	    /* log termination info */
	    _pi.logTermination();
	    /* termination information */
	    _pi.printTermination();
	    iReturn = stdb.getReturn();
		}
    catch (Exception e)
    {
      _il.exception(e);
      System.err.println(EU.getExceptionMessage(e));
      iReturn = iRETURN_ERROR;
    }
    catch (Error e)
    {
      _il.error(e);
      System.err.println(EU.getErrorMessage(e));
      iReturn = iRETURN_FATAL;
    }
    System.exit(iReturn);
	} /* main */

} /* class SiardToDb */
