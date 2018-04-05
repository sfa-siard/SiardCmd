package ch.admin.bar.siard2.cmd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  SiardConnectionTester.class,
  AccessFromDbTester.class,
  AccessToDbTester.class,
  Db2FromDbTester.class,
  Db2ToDbTester.class,
  H2FromDbTester.class,
  H2ToDbTester.class,
  MsSqlFromDbTester.class,
  MsSqlToDbTester.class,
  MySqlFromDbTester.class,
  MySqlToDbTester.class,
  OracleFromDbTester.class,
  OracleToDbTester.class,
  AwFromDbTester.class,
  OeFromDbTester.class,
	SiardFromDbTester.class,
	SiardToDbTester.class
})
public class _SiardCmdTestSuite {

}
