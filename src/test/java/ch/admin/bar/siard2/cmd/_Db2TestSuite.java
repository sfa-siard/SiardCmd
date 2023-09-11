package ch.admin.bar.siard2.cmd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	Db2FromDbTester.class,
	Db2ToDbTester.class
})
public class _Db2TestSuite {

}
