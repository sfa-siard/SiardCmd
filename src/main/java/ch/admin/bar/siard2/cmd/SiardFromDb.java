/*
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 29.08.2016, Hartwig Thomas, Enter AG, Rüti ZH
*/

package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import ch.enterag.utils.*;
import ch.enterag.utils.cli.*;
import ch.enterag.utils.configuration.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;


/**
 * Stores database content in a SIARD file.
 *
 * @author Hartwig Thomas
 */
public class SiardFromDb {
    private static final int iRETURN_OK = 0;
    private static final int iRETURN_WARNING = 4;
    private static final int iRETURN_ERROR = 8;
    private static final int iRETURN_FATAL = 12;

    private static IndentLogger _il = IndentLogger.getIndentLogger(SiardFromDb.class.getName());
    private static final ManifestAttributes MF = ManifestAttributes.getInstance(SiardFromDb.class);
    private static ProgramInfo _pi = ProgramInfo.getProgramInfo("SIARD Suite",
                                                                MF.getSpecificationVersion(),
                                                                "SiardFromDb",
                                                                MF.getImplementationVersion(),
                                                                "Program to store database content in a .siard file",
                                                                "Swiss Federal Archives, Berne, Switzerland, 2008-2016",
                                                                Arrays.asList((new String[]{"Hartwig Thomas, Enter AG, Rüti ZH, Switzerland", "Andreas Voss, Swiss Federal Archives, Berne, Switzerland", "Anders Bo Nielsen, Danish National Archives, Denmark", "Claire Röthlisberger-Jourdan, KOST, Berne, Switzerland"})),
                                                                Arrays.asList((new String[]{"Hartwig Thomas, Enter AG, Rüti ZH, Switzerland", "Simon Jutz, Cytex GmbH, Zurich, Switzerland"})),
                                                                Arrays.asList((new String[]{"Claudia Matthys, POOL Computer AG, Zurich, Switzerland", "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland", "Yvan Dutoit, Swiss Federal Archives, Berne, Switzerland"})),
                                                                Arrays.asList((new String[]{"Hartwig Thomas, Enter AG, Rüti ZH, Switzerland", "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland", "Alain Mast, Swiss Federal Archives, Berne, Switzerland", "Krystyna Ohnesorge, Swiss Federal Archives, Berne, Switzerland"})));

    private boolean _bOverwrite = false;
    private boolean _bViewsAsTables = false;
    private int _iLoginTimeoutSeconds = SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS;
    private int _iQueryTimeoutSeconds = SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS;
    private File _fileImportXml = null;
    private File _fileExternalLobFolder = null;
    private URI _uriExternalLobFolder = null;
    private String _sMimeType = null;
    private String _sJdbcUrl = null;
    private String _sDatabaseUser = null;
    private String _sDatabasePassword = null;
    private File _fileSiard = null;
    private File _fileExportXml = null;

    private Archive _archive = null;
    private Connection _conn = null;

    private int _iReturn = iRETURN_WARNING;

    public int getReturn() {
        return _iReturn;
    }

    /**
     * prints and logs the string
     */
    private static void logPrint(String s) {
        _il.info(s);
        System.out.println(s);
    }

    /**
     * prints usage information
     */
    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("java -cp <siardpath>/lib/siardcmd.jar ch.admin.bar.siard2.cmd.SiardFromDb [-h]");
        System.out.println(
                "  [-o][-v][-l=<login timeout>][-q=<query timeout>][-i=<import xml>] [-x=<external lob folder>] [-m=<mime type>]");
        System.out.println(
                "  -j=<JDBC URL> -u=<database user> -p=<database password> (-s=<siard file> | -e=<export xml>)");
        System.out.println("where");
        System.out.println("  <siardpath>          installation path of SiardCmd");
        System.out.println("  -h (help)            prints this usage information");
        System.out.println("  -o (overwrite)       overwrite existing siard file");
        System.out.println("  -v (views as tables) archive views as tables");
        System.out.println("  <login timeout>      login timeout in seconds (default: " + String.valueOf(
                _iLoginTimeoutSeconds) + "), 0 for unlimited");
        System.out.println("  <query timeout>      query timeout in seconds (default: " + String.valueOf(
                _iQueryTimeoutSeconds) + "), 0 for unlimited");
        System.out.println("  <import xml>         name of meta data XML file to be used as a template");
        System.out.println("  <lob folder>         folder for storing largest LOB column of database externally");
        System.out.println("                       (contents will be deleted if they exist!)");
        System.out.println("  <mime type>          MIME type of data in the largest LOB column of database");
        System.out.println("  <JDBC URL>           JDBC URL of database to be downloaded");
        System.out.print("                       e.g. ");
        SiardConnection sc = SiardConnection.getSiardConnection();
        String[] asSchemes = sc.getSchemes();
        for (int i = 0; i < asSchemes.length; i++) {
            if (i > 0) System.out.println("                           ");
            String sScheme = asSchemes[i];
            System.out.println("for " + sc.getTitle(sScheme) + ": " + sc.getSampleUrl(sScheme,
                                                                                      "dbserver.enterag.ch",
                                                                                      "D:\\dbfolder",
                                                                                      "testdb"));
        }
        System.out.println("  <database user>      database user");
        System.out.println("  <database password>  database password");
        System.out.println("  <siard file>         name of .siard file to be written");
        System.out.println("  <export xml>         name of meta data XML file to be exported");
    }

    /**
     * reads the parameters from the command line or from the config file.
     */
    private void getParameters(String[] asArgs) {
        _il.enter();
        _iReturn = iRETURN_OK;
        Arguments args = Arguments.newInstance(asArgs);
        if (args.getOption("h") != null) _iReturn = iRETURN_WARNING;
        /* overwrite */
        if (args.getOption("o") != null) _bOverwrite = true;
        /* views as tables */
        if (args.getOption("v") != null) _bViewsAsTables = true;
        /* login time out */
        String sLoginTimeoutSeconds = args.getOption("l");
        /* query time out */
        String sQueryTimeoutSeconds = args.getOption("q");
        /* import XML */
        String sImportXml = args.getOption("i");
        /* export XML */
        String sExportXml = args.getOption("e");
        /* external LOB folder */
        String sExternalLobFolder = args.getOption("x");
        /* MIME type for external LOB */
        _sMimeType = args.getOption("m");
        /* JDBC URI */
        _sJdbcUrl = args.getOption("j");
        /* db user */
        _sDatabaseUser = args.getOption("u");
        /* db password */
        _sDatabasePassword = args.getOption("p");
        /* siard file */
        String sSiardFile = args.getOption("s");
        /* analyze the parameters */
        if (_iReturn == iRETURN_OK) {
            if (sSiardFile != null) _fileSiard = new File(sSiardFile);
            if (sExportXml != null) _fileExportXml = new File(sExportXml);

            if ((sSiardFile == null) && (sExportXml == null)) {
                System.out.println("SIARD file and/or export meta data XML must be given!");
                _iReturn = iRETURN_ERROR;
            }
            if (sLoginTimeoutSeconds != null) {
                try {
                    _iLoginTimeoutSeconds = Integer.parseInt(sLoginTimeoutSeconds);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid login timeout: " + sLoginTimeoutSeconds + "!");
                    _iReturn = iRETURN_ERROR;
                }
            }
            if (sQueryTimeoutSeconds != null) {
                try {
                    _iQueryTimeoutSeconds = Integer.parseInt(sQueryTimeoutSeconds);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid query timeout: " + sQueryTimeoutSeconds + "!");
                    _iReturn = iRETURN_ERROR;
                }
            }
            if (sImportXml != null) {
                _fileImportXml = new File(sImportXml);
                if (!(_fileImportXml.isFile() && _fileImportXml.exists())) {
                    System.out.println("File import XML " + _fileImportXml.getAbsolutePath() + " does not exist!");
                    _iReturn = iRETURN_ERROR;
                }
            }
            if (sExternalLobFolder != null) {
                _fileExternalLobFolder = new File(sExternalLobFolder);
                if (!(_fileExternalLobFolder.exists() && _fileExternalLobFolder.isDirectory())) {
                    System.out.println("External LOB folder  " + _fileExternalLobFolder.getAbsolutePath() + " does not exist!");
                    _iReturn = iRETURN_ERROR;
                } else {
                    if (_fileSiard.getParentFile() == null) {
                        System.out.println("To use external LOB folder, specify the parent directory of " + _fileSiard + "!");
                        _iReturn = iRETURN_ERROR;
                    }
                    /* relativize it from SIARD file's parent folder */
                    File fileRelative = _fileSiard.getParentFile()
                                                  .getAbsoluteFile()
                                                  .toPath()
                                                  .relativize(_fileExternalLobFolder.getAbsoluteFile().toPath())
                                                  .toFile();
                    /* prepend a ../ for exiting the SIARD file and append a / to indicate that it is a folder */
                    try {
                        _uriExternalLobFolder = new URI("../" + fileRelative.toString() + "/");
                    } catch (URISyntaxException use) {
                        System.out.println("External LOB folder  " + _fileExternalLobFolder.getAbsolutePath() + " could not be relativized!");
                        _iReturn = iRETURN_ERROR;
                    }
                }

            }
            String sError = SiardConnection.getSiardConnection().loadDriver(_sJdbcUrl);
            if (sError != null) {
                System.out.println("JDBC URL " + String.valueOf(_sJdbcUrl) + " is not valid!");
                System.out.println(sError);
                _iReturn = iRETURN_ERROR;
            }
            if (_sDatabaseUser == null) {
                System.out.println("Database user must be given!");
                _iReturn = iRETURN_ERROR;
            }
            if (_sDatabasePassword == null) {
                System.out.println("Database password must be given!");
                _iReturn = iRETURN_ERROR;
            }
        }
        /* print and log the parameters */
        if (_iReturn == iRETURN_OK) {
            logPrint("");
            logPrint("Parameters");
            logPrint("  JDBC URL               : " + _sJdbcUrl);
            logPrint("  Database user          : " + _sDatabaseUser);
            logPrint("  Database password      : ***");
            if (_fileSiard != null) logPrint("  SIARD file             : " + _fileSiard.getAbsolutePath());
            if (_fileExportXml != null) logPrint("  Export meta data XML   : " + _fileExportXml.getAbsolutePath());
            if (sLoginTimeoutSeconds != null)
                logPrint("  Login timeout          : " + String.valueOf(_iLoginTimeoutSeconds) + " seconds");
            if (sQueryTimeoutSeconds != null)
                logPrint("  Query timeout          : " + String.valueOf(_iQueryTimeoutSeconds) + " seconds");
            if (_fileImportXml != null) logPrint("  Import meta data XML   : " + _fileImportXml.getAbsolutePath());
            if (_uriExternalLobFolder != null)
                logPrint("  External LOB folder    : " + _uriExternalLobFolder.toString());
            if (_sMimeType != null) logPrint("  External LOB MIME type : " + _sMimeType);
            if (_bViewsAsTables) logPrint("  Archive views as tables: " + String.valueOf(_bViewsAsTables));
            logPrint("");
        } else printUsage();
        _il.exit();
    } /* getParameters */


    /**
     * runs main program of SiardFromDb.
     */
    public SiardFromDb(String asArgs[]) throws SQLException, IOException, ClassNotFoundException {
        super();
        _il.enter();
        /* parameters */
        getParameters(asArgs);
        if (_iReturn == iRETURN_OK) {
            if (_bOverwrite) {
                if (_fileSiard != null) {
                    if (_fileSiard.exists()) _fileSiard.delete();
                }
                if (_fileExportXml != null) {
                    if (_fileExportXml.exists()) _fileExportXml.delete();
                }
            }
            if (((_fileSiard == null) || !_fileSiard.exists()) && ((_fileExportXml == null) || !_fileExportXml.exists())) {
                /* open connection */
                String sError = SiardConnection.getSiardConnection().loadDriver(_sJdbcUrl);
                if ((sError == null) || (sError.length() == 0)) {
                    DriverManager.setLoginTimeout(_iLoginTimeoutSeconds);
                    _conn = DriverManager.getConnection(_sJdbcUrl, _sDatabaseUser, _sDatabasePassword);
                    if ((_conn != null) && (!_conn.isClosed())) {
                        System.out.println("Connected to " + _conn.getMetaData().getURL().toString());
                        _conn.setAutoCommit(false);
                        /* open SIARD archive */
                        _archive = ArchiveImpl.newInstance();
                        File fileSiard = _fileSiard;
                        if (fileSiard == null) {
                            fileSiard = File.createTempFile("siard", ".siard");
                            fileSiard.delete();
                        }
                        _archive.create(fileSiard);
                        if (_fileImportXml != null) {
                            FileInputStream fis = new FileInputStream(_fileImportXml);
                            _archive.importMetaDataTemplate(fis);
                            fis.close();
                        }
                        /* get meta data from DB */
                        MetaDataFromDb mdfd = MetaDataFromDb.newInstance(_conn.getMetaData(), _archive.getMetaData());
                        mdfd.setQueryTimeout(_iQueryTimeoutSeconds);
                        mdfd.download(_bViewsAsTables, (_uriExternalLobFolder != null), null);
                        /* set external LOB stuff */
                        if (_uriExternalLobFolder != null) {
                            MetaColumn mcMaxLob = mdfd.getMaxLobColumn();
                            if (mcMaxLob != null) {
                                String sColumnName = mcMaxLob.getName();
                                MetaTable mtLob = mcMaxLob.getParentMetaTable();
                                String sTableName = mtLob.getName();
                                MetaSchema msLob = mtLob.getParentMetaSchema();
                                String sSchemaName = msLob.getName();
                                String sMessage = "LOBs in database column \"" + sColumnName + "\" in table \"" + sTableName + "\" in schema \"" + sSchemaName + "\" will be stored externally in folder \"" + _fileExternalLobFolder.getAbsolutePath()
                                                                                                                                                                                                                                     .toString() + "\"";
                                mcMaxLob.setLobFolder(_uriExternalLobFolder);
                                if (_sMimeType != null) {
                                    mcMaxLob.setMimeType(_sMimeType);
                                    sMessage = sMessage + " with MIME type " + mcMaxLob.getMimeType();
                                }
                                System.out.println();
                                System.out.println(sMessage);
                            } else System.out.println("No LOB column found to be externalized!");
                        }
                        /* export meta data XML from DB */
                        if (_fileExportXml != null) {
                            OutputStream osXml = new FileOutputStream(_fileExportXml);
                            _archive.exportMetaData(osXml);
                            osXml.close();
                        }
                        if (_fileSiard != null) {
                            /* export primary data from DB */
                            PrimaryDataFromDb pdfd = PrimaryDataFromDb.newInstance(_conn, _archive);
                            pdfd.setQueryTimeout(_iQueryTimeoutSeconds);
                            pdfd.download(null);
                        } else fileSiard.deleteOnExit();
                        /* close SIARD archive */
                        /***
                         FileOutputStream fosXml = new FileOutputStream("D:\\Projekte\\SIARD2\\SiardCmd\\tmp\\export.xml");
                         _archive.exportMetaData(fosXml);
                         fosXml.close();
                         ***/
                        _archive.close();
                        /* close connection */
                        _conn.rollback();
                        _conn.close();
                    } else System.out.println("Connection to " + _conn.getMetaData().getURL().toString() + " failed!");
                } else System.out.println("Connection to " + _sJdbcUrl + " not supported (" + sError + ")!");
            } else {
                String sMessage = "File " + _fileSiard.getAbsolutePath();
                if (_fileExportXml != null)
                    sMessage = sMessage + " or " + _fileExportXml.getAbsolutePath() + " exist already!";
                else sMessage = sMessage + " exists already!";
                System.out.println(sMessage);
                System.out.println("Backup and delete it or use option -o for overwriting it.");
                _iReturn = iRETURN_WARNING;
            }
        }
        _il.exit();
    } /* constructor SiardFromDb */


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
            _il.systemProperties();
            SiardFromDb sfdb = new SiardFromDb(asArgs);
            _pi.logTermination();
            _pi.printTermination();
            iReturn = sfdb.getReturn();
        } catch (Exception e) {
            _il.exception(e);
            System.out.println(EU.getExceptionMessage(e));
            iReturn = iRETURN_ERROR;
        } catch (Error e) {
            _il.error(e);
            System.out.println(EU.getErrorMessage(e));
            iReturn = iRETURN_FATAL;
        }
        System.exit(iReturn);
    }
}