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

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.cmd.utils.RuntimeHelper;
import ch.admin.bar.siard2.cmd.utils.VersionsExplorer;
import ch.enterag.utils.EU;
import ch.enterag.utils.ProgramInfo;
import ch.enterag.utils.cli.Arguments;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * Loads the data from a siard file to a database instance.
 *
 * @author Hartwig Thomas
 */
@Slf4j
public class SiardToDb {
    public static final int iRETURN_OK = 0;
    public static final int iRETURN_WARNING = 4;
    public static final int iRETURN_ERROR = 8;
    public static final int iRETURN_FATAL = 12;

    /**
     * info
     */
    private static final ProgramInfo _pi = ProgramInfo.getProgramInfo(
            "SIARD Suite", VersionsExplorer.INSTANCE.getSiardVersion(),
            "SiardToDb", VersionsExplorer.INSTANCE.getAppVersion(),
            "Program to load database content from a .siard file",
            "Swiss Federal Archives, Berne, Switzerland, 2008-2016",
            Arrays.asList("Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
                          "Andreas Voss, Swiss Federal Archives, Berne, Switzerland",
                          "Anders Bo Nielsen, Danish National Archives, Denmark",
                          "Claire Röthlisberger-Jourdan, KOST, Berne, Switzerland"),
            Arrays.asList("Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
                          "Simon Jutz, Cytex GmbH, Zurich, Switzerland"),
            Arrays.asList("Claudia Matthys, POOL Computer AG, Zurich, Switzerland",
                          "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland",
                          "Yvan Dutoit, Swiss Federal Archives, Berne, Switzerland"),
            Arrays.asList("Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
                          "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland",
                          "Alain Mast, Swiss Federal Archives, Berne, Switzerland",
                          "Krystyna Ohnesorge, Swiss Federal Archives, Berne, Switzerland"));

    private int _iLoginTimeoutSeconds = SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS;
    private int _iQueryTimeoutSeconds = SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS;
    private boolean _bOverwrite = false;
    private String _sJdbcUrl = null;
    private String _sDatabaseUser = null;
    private String _sDatabasePassword = null;
    private File _fileSiard = null;
    private final Map<String, String> _mapSchemas = new HashMap<String, String>();

    private Archive _archive = null;
    private Connection _conn = null;

    private int _iReturn = iRETURN_WARNING;

    public int getReturn() {
        return _iReturn;
    }
  

    /**
     * prints usage information
     */
    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("java -cp <siardpath>/lib/siardcmd.jar ch.admin.bar.siard2.cmd.SiardToDb [-h]");
        System.out.println("  [-o][-q=<query timeout>][-l=<login timeout>]");
        System.out.println("  -s=<siard file> -j=<JDBC URL> -u=<database user> -p=<database password>");
        System.out.println("  [<schema> <mappedschema>]*");
        System.out.println("where");
        System.out.println("  <siardpath>         installation path of SiardCmd");
        System.out.println("  -h (help)           prints this usage information");
        System.out.println("  -o                  overwrite existing database objects");
        System.out.println("  <login timeout>     login timeout in seconds (default: " + _iLoginTimeoutSeconds + ")");
        System.out.println("  <query timeout>     query timeout in seconds (default: " + _iQueryTimeoutSeconds + ")");
        System.out.println("  <JDBC URL>          JDBC URL of database to be uploaded");
        System.out.print("                      e.g. ");
        SiardConnection sc = SiardConnection.getSiardConnection();
        String[] asSchemes = sc.getSchemes();
        for (int i = 0; i < asSchemes.length; i++) {
            if (i > 0)
                System.out.println("                           ");
            String sScheme = asSchemes[i];
            System.out.println("for " + sc.getTitle(sScheme) + ": " + sc.getSampleUrl(sScheme, "dbserver.enterag.ch", "D:\\dbfolder", "testdb"));
        }
        System.out.println("  <database user>     database user");
        System.out.println("  <database password> database password");
        System.out.println("  <siard file>        name of .siard file (will be overwritten. if it exists!)");
        System.out.println("  <schema>            schema name in SIARD file");
        System.out.println("  <mappedschema>      schema name to be used in database");
    }



    /**
     * reads the parameters from the command line or from the config file.
     */
    private void getParameters(String[] asArgs) {
        _iReturn = iRETURN_OK;
        Arguments args = Arguments.newInstance(asArgs);
        if (args.getOption("h") != null)
            _iReturn = iRETURN_WARNING;

        String sLoginTimeoutSeconds = args.getOption("l");

        String sQueryTimeoutSeconds = args.getOption("q");
       
        if (args.getOption("o") != null)
            _bOverwrite = true;

        _sJdbcUrl = args.getOption("j");

        _sDatabaseUser = args.getOption("u");

        _sDatabasePassword = args.getOption("p");

        String sSiardFile = args.getOption("s");

        for (int i = 0; i < args.getArguments() / 2; i++) {
            String sSchema = args.getArgument(2 * i);
            String sMappedSchema = args.getArgument(2 * i + 1);
            _mapSchemas.put(sSchema, sMappedSchema);
        }


        if (_iReturn == iRETURN_OK) {
            if (sLoginTimeoutSeconds != null) {
                try {
                    _iLoginTimeoutSeconds = Integer.parseInt(sLoginTimeoutSeconds);
                } catch (NumberFormatException nfe) {
                    System.err.println("Invalid login timeout: " + sLoginTimeoutSeconds + "!");
                    _iReturn = iRETURN_ERROR;
                }
            }
            if (sQueryTimeoutSeconds != null) {
                try {
                    _iQueryTimeoutSeconds = Integer.parseInt(sQueryTimeoutSeconds);
                } catch (NumberFormatException nfe) {
                    System.err.println("Invalid query timeout: " + sQueryTimeoutSeconds + "!");
                    _iReturn = iRETURN_ERROR;
                }
            }
            String sError = SiardConnection.getSiardConnection()
                                           .loadDriver(_sJdbcUrl);
            if (sError != null) {
                System.err.println(sError);
                _iReturn = iRETURN_ERROR;
            }
            if (_sDatabaseUser == null) {
                System.err.println("Database user must be given!");
                _iReturn = iRETURN_ERROR;
            }
            if (_sDatabasePassword == null) {
                System.err.println("Database password must be given!");
                _iReturn = iRETURN_ERROR;
            }
            if (sSiardFile != null)
                _fileSiard = new File(sSiardFile);
            else {
                System.err.println("SIARD file must be given!");
                _iReturn = iRETURN_ERROR;
            }
            if ((args.getArguments() % 2) != 0) {
                System.err.println("Dangling schema name on command line!");
                _iReturn = iRETURN_ERROR;
            }
        }

        if (_iReturn == iRETURN_OK) {
            val sb = new StringBuilder()
                    .append("\n")
                    .append("Parameters\n")
                    .append("  SIARD file             : ")
                    .append(_fileSiard.getAbsolutePath())
                    .append("\n")
                    .append("  JDBC URL               : ")
                    .append(_sJdbcUrl)
                    .append("\n")
                    .append("  Database user          : ")
                    .append(_sDatabaseUser)
                    .append("\n")
                    .append("  Database password      : ***\n");
            if (sLoginTimeoutSeconds != null)
                sb.append("  Login timeout          : ")
                  .append(_iLoginTimeoutSeconds)
                  .append(" seconds\n");
            if (sQueryTimeoutSeconds != null)
                sb.append("  Query timeout          : ")
                  .append(_iQueryTimeoutSeconds)
                  .append(" seconds\n");
            if (_bOverwrite)
                sb.append("  Overwrite              : Database objects may be overwritten on upload\n");
            if (_mapSchemas.size() > 0) {
                for (Iterator<String> iterSchema = _mapSchemas.keySet()
                                                              .iterator(); iterSchema.hasNext(); ) {
                    String sSchema = iterSchema.next();
                    String sMappedSchema = _mapSchemas.get(sSchema);
                    sb.append("  Mapped Schema          : \"")
                      .append(sSchema)
                      .append("\" => \"")
                      .append(sMappedSchema)
                      .append("\"");
                }
            }
            sb.append("\n");

            val message = sb.toString();
            LOG.info(message);
            System.out.println(message);
        } else
            printUsage();
    }



    /**
     * runs main program of SiardFromDb.
     */
    public SiardToDb(String[] asArgs)
            throws IOException, SQLException {
        super();
       
        getParameters(asArgs);
        if (_iReturn == iRETURN_OK) {

            _archive = ArchiveImpl.newInstance();
            _archive.open(_fileSiard);

            String sError = null;

            val siardConnection = SiardConnection.getSiardConnection();
            if ((sError == null) || (sError.length() == 0)) {
                DriverManager.setLoginTimeout(_iLoginTimeoutSeconds);
                _conn = siardConnection.createValidConnection(_sJdbcUrl, _sDatabaseUser, _sDatabasePassword);
                if ((_conn != null) && (!_conn.isClosed())) {
                    System.out.println("Connected to " + _conn.getMetaData()
                                                              .getURL());
                    _conn.setAutoCommit(false);

                    MetaData md = _archive.getMetaData();
                    MetaDataToDb mdtd = MetaDataToDb.newInstance(_conn.getMetaData(), md, _mapSchemas);
                    mdtd.setQueryTimeout(_iQueryTimeoutSeconds);
                    if (_bOverwrite || ((mdtd.tablesDroppedByUpload() == 0) && (mdtd.typesDroppedByUpload() == 0))) {
                        if (!mdtd.supportsUdts()) {
                            int iTypesInSiard = 0;
                            for (int iSchema = 0; iSchema < md.getMetaSchemas(); iSchema++) {
                                MetaSchema ms = md.getMetaSchema(iSchema);
                                iTypesInSiard = iTypesInSiard + ms.getMetaTypes();
                            }
                            if (iTypesInSiard > 0) {
                                val message = "Target database does not support UDTs. UDTs will be \"flattened\".";

                                LOG.info(message);
                                System.out.println(message);
                            }
                        }
                        mdtd.upload(null);

                        PrimaryDataToDb pdtd = PrimaryDataToDb.newInstance(_conn, _archive,
                                                                           mdtd.getArchiveMapping(), mdtd.supportsArrays(), mdtd.supportsDistincts(), mdtd.supportsUdts());
                        pdtd.setQueryTimeout(_iQueryTimeoutSeconds);
                        pdtd.upload(null);
                    } else {
                        System.err.println("Database objects exist which would be overwritten on upload!");
                        System.err.println("Backup and delete them first or use -o option for overwriting them.");
                        _iReturn = iRETURN_WARNING;
                    }
                    // _conn.commit();
                    _conn.close();
                } else
                    System.err.println("Connection to " + _conn.getMetaData()
                                                               .getURL() + " failed!");
            } else
                System.err.println("Connection to " + _sJdbcUrl + " not supported (" + sError + ")!");
            _archive.close();
        }
    }



    /**
     * main entry point starts logging and creates running instance.
     *
     * @param asArgs command-line arguments are ignored
     */
    public static void main(String[] asArgs) {
        int iReturn = iRETURN_WARNING;
        try {
            _pi.printStart();
            _pi.logStart();
            LOG.info(RuntimeHelper.getRuntimeInformation());

            SiardToDb stdb = new SiardToDb(asArgs);

            _pi.logTermination();

            _pi.printTermination();
            iReturn = stdb.getReturn();
        } catch (Exception e) {
            LOG.error("Exception while uploading SIARD archive", e);
            System.err.println(EU.getExceptionMessage(e));
            iReturn = iRETURN_ERROR;
        } catch (Error e) {
            LOG.error("Error while uploading SIARD archive", e);
            System.err.println(EU.getErrorMessage(e));
            iReturn = iRETURN_FATAL;
        }
        System.exit(iReturn);
    }

}
