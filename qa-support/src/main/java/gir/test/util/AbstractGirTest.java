package gir.test.util;

import gir.GirContext;
import gir.GirTestUtil;
import gir.io.FileUtil;
import java.nio.file.Path;
import org.realityforge.braincheck.BrainCheckTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@SuppressWarnings( "SameParameterValue" )
public abstract class AbstractGirTest
{
  private Path _workingDirectory;

  @BeforeMethod
  protected void beforeTest()
    throws Exception
  {
    BrainCheckTestUtil.resetConfig( false );
    resetWorkingDirectory();
    _workingDirectory = FileUtil.createTempDir();
    FileUtil.setCurrentDirectory( _workingDirectory );
    GirTestUtil.setContext( new GirContext() );
  }

  @AfterMethod
  protected void afterTest()
  {
    BrainCheckTestUtil.resetConfig( true );
    resetWorkingDirectory();
  }

  private void resetWorkingDirectory()
  {
    if( null != _workingDirectory )
    {
      final Path directory = _workingDirectory;
      _workingDirectory = null;
      FileUtil.deleteDirIfExists( directory );
    }
  }
}
