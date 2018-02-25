package zam.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FileUtil
{
  @Nonnull
  private static Path c_directory = cwd();

  private FileUtil()
  {
  }

  @Nullable
  public static Path findInPath( @Nonnull final String command )
  {
    final String value = System.getenv( "PATH" );
    if ( null == value )
    {
      return null;
    }
    final String[] paths = value.split( File.pathSeparator );
    for ( final String path : paths )
    {
      final Path commandPath = Paths.get( path, command ).normalize();
      if ( commandPath.toFile().exists() )
      {
        return commandPath;
      }
    }
    return null;
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
    throws IOException
  {
    //noinspection ResultOfMethodCallIgnored
    Files.walk( directory ).sorted( Comparator.reverseOrder() ).map( Path::toFile ).forEach( File::delete );
  }
}
