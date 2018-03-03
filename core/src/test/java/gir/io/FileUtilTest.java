package gir.io;

import gir.AbstractGirTest;
import gir.TestUtil;
import java.io.File;
import java.nio.file.Path;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class FileUtilTest
  extends AbstractGirTest
{
  @Test
  public void currentDirectory()
    throws Exception
  {
    assertEquals( FileUtil.getCurrentDirectory(), FileUtil.cwd() );

    final File file = TestUtil.createTempDirectory();
    final Path newDirectory = file.toPath();
    FileUtil.setCurrentDirectory( newDirectory );

    assertEquals( FileUtil.getCurrentDirectory(), newDirectory );
  }

  @Test
  public void inDirectory()
    throws Exception
  {
    final Path initialDirectory = FileUtil.getCurrentDirectory();

    final File file = TestUtil.createTempDirectory();
    final Path directory = file.toPath();
    assertEquals( FileUtil.getCurrentDirectory(), initialDirectory );
    FileUtil.inDirectory( directory, () -> assertEquals( FileUtil.getCurrentDirectory(), directory ) );
    assertEquals( FileUtil.getCurrentDirectory(), initialDirectory );
  }
}
