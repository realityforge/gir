package gir.io;

import gir.test.util.AbstractGirTest;
import gir.test.util.TestUtil;
import java.io.File;
import java.nio.file.Files;
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

  @Test
  public void write()
    throws Exception
  {
    final Path directory = TestUtil.createTempDirectory().toPath();
    FileUtil.inDirectory( directory, () -> {
      FileUtil.write( "foo.txt", "A" );
      FileUtil.write( "bar/bar.txt", "B" );
      FileUtil.write( "baz.dat", new byte[ 1 ] );
    } );

    assertEquals( Files.readAllBytes( directory.resolve( "foo.txt" ) ), new byte[]{ 'A' } );
    assertEquals( Files.readAllBytes( directory.resolve( "bar/bar.txt" ) ), new byte[]{ 'B' } );
    assertEquals( Files.readAllBytes( directory.resolve( "baz.dat" ) ), new byte[]{ 0 } );

    FileUtil.deleteDir( directory );
  }
}
