package gir.io;

import gir.test.util.AbstractGirTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
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

    final Path newDirectory = FileUtil.createTempDir();
    FileUtil.setCurrentDirectory( newDirectory );

    assertEquals( FileUtil.getCurrentDirectory(), newDirectory );
  }

  @Test
  public void inDirectory()
    throws Exception
  {
    final Path initialDirectory = FileUtil.getCurrentDirectory();

    final Path directory = FileUtil.createTempDir();
    assertEquals( FileUtil.getCurrentDirectory(), initialDirectory );
    FileUtil.inDirectory( directory, () -> assertEquals( FileUtil.getCurrentDirectory(), directory ) );
    assertEquals( FileUtil.getCurrentDirectory(), initialDirectory );
  }

  @Test
  public void write()
    throws Exception
  {
    final Path directory = FileUtil.createTempDir();
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

  @Test
  public void inTempDir()
    throws Exception
  {
    final Path initialDirectory = FileUtil.getCurrentDirectory();
    final AtomicReference<Path> tempDir = new AtomicReference<>();
    FileUtil.inTempDir( () -> {
      assertNotEquals( FileUtil.getCurrentDirectory(), initialDirectory );
      tempDir.set( FileUtil.getCurrentDirectory() );
    } );

    assertFalse( tempDir.get().toFile().exists() );
  }
}
