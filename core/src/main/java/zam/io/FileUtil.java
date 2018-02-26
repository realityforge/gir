package zam.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import javax.annotation.Nonnull;
import zam.Task;
import zam.ZamException;

public final class FileUtil
{
  @Nonnull
  private static Path c_directory = cwd();

  private FileUtil()
  {
  }

  @Nonnull
  public static Path cwd()
  {
    return Paths.get( "." ).toAbsolutePath().normalize();
  }

  public static void setCurrentDirectory( @Nonnull final Path directory )
  {
    // Ensure directory is non null
    Objects.requireNonNull( directory );

    if ( !directory.toFile().exists() )
    {
      throw new IllegalStateException( "Attempted to set current directory to '" + directory +
                                       "' but directory does not exist" );
    }
    else if ( !directory.toFile().isDirectory() )
    {
      throw new IllegalStateException( "Attempted to set current directory to '" + directory +
                                       "' but path exists and is not a directory" );
    }

    c_directory = directory;
  }

  @Nonnull
  public static Path getCurrentDirectory()
  {
    return c_directory;
  }

  @Nonnull
  public static Path homeDir()
  {
    return Paths.get( System.getProperty( "user.home" ) ).toAbsolutePath().normalize();
  }

  /**
   * Recursively delete directory.
   */
  public static void deleteDir( @Nonnull final Path directory )
  {
    try
    {
      //noinspection ResultOfMethodCallIgnored
      Files.walk( directory ).sorted( Comparator.reverseOrder() ).map( Path::toFile ).forEach( File::delete );
    }
    catch ( final IOException e )
    {
      throw new ZamException( "Failure to delete directory: " + directory, e );
    }
  }

  public static void inDirectory( @Nonnull final Path directory, @Nonnull final Task action )
    throws Exception
  {
    final Path initial = getCurrentDirectory();
    try
    {
      setCurrentDirectory( directory );
      action.call();
    }
    finally
    {
      setCurrentDirectory( initial );
    }
  }
}
