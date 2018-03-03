package gir;

import gir.io.FileUtil;
import java.io.File;
import java.io.IOException;
import org.realityforge.braincheck.BrainCheckTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

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

  protected final File createTempDirectory()
    throws IOException
  {
    final File file = File.createTempFile( "gir", "test" );
    assertTrue( file.delete() );
    assertTrue( file.mkdirs() );
    return file;
  }
}
