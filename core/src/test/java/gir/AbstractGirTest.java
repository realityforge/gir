package gir;

import gir.io.FileUtil;
import org.realityforge.braincheck.BrainCheckTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@SuppressWarnings( "SameParameterValue" )
public abstract class AbstractGirTest
{
  @BeforeMethod
  protected void beforeTest()
    throws Exception
  {
    BrainCheckTestUtil.resetConfig( false );
    FileUtil.setCurrentDirectory( FileUtil.cwd() );
  }

  @AfterMethod
  protected void afterTest()
    throws Exception
  {
    BrainCheckTestUtil.resetConfig( true );
  }
}
