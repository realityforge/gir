package gir.io;

import gir.GirException;
import gir.Task;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

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
   * Recursively delete directory if it exists.
   *
   * @param directory the directory to delete.
   */
  public static void deleteDirIfExists( @Nonnull final Path directory )
  {
    if ( directory.toFile().exists() )
    {
      deleteDir( directory );
    }
  }

  /**
   * Recursively delete directory.
   *
   * @param directory the directory to delete.
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
      throw new GirException( "Failure to delete directory: " + directory, e );
    }
  }

  /**
   * Copy the src directory to the dest directory.
   *
   * @param src  the source directory.
   * @param dest the destination directory.
   */
  public static void copyDirectory( @Nonnull final Path src, @Nonnull final Path dest )
  {
    try
    {
      try ( final Stream<Path> stream = Files.walk( src ) )
      {
        stream.forEach( sourcePath -> {
          try
          {
            final Path targetPath = dest.resolve( src.relativize( sourcePath ) );
            Files.copy( sourcePath, targetPath );
          }
          catch ( final IOException ioe )
          {
            throw new GirException( ioe );
          }
        } );
      }
    }
    catch ( final IOException ioe )
    {
      throw new GirException( ioe );
    }
  }

  public static void inDirectory( @Nonnull final Path directory, @Nonnull final Task action )
  {
    final Path initial = getCurrentDirectory();
    try
    {
      setCurrentDirectory( directory );
      action.call();
    }
    catch ( final RuntimeException | Error e )
    {
      throw e;
    }
    catch ( final Exception e )
    {
      throw new GirException( e );
    }
    finally
    {
      setCurrentDirectory( initial );
    }
  }

  /**
   * Write contents of file relative to the current directory.
   *
   * @param path    path to file relative to current directory.
   * @param content the file contents.
   */
  public static void write( @Nonnull final String path, @Nonnull final String content )
    throws IOException
  {
    write( path, content.getBytes() );
  }

  /**
   * Write contents of file relative to the current directory.
   *
   * @param path    path to file relative to current directory.
   * @param content the file contents.
   */
  public static void write( @Nonnull final String path, @Nonnull final byte[] content )
    throws IOException
  {
    final Path file = FileUtil.getCurrentDirectory().resolve( path ).toAbsolutePath().normalize();
    //noinspection ResultOfMethodCallIgnored
    file.getParent().toFile().mkdirs();
    Files.write( file, content );
  }

  /**
   * Create temp directory.
   *
   * @return the new temp directory.
   */
  @Nonnull
  public static Path createTempDir()
    throws IOException
  {
    final File dir = File.createTempFile( "gir", "dir" );
    if ( !dir.delete() )
    {
      throw new GirException( "Failed to delete intermediate tmp file: " + dir );
    }
    if ( !dir.mkdir() )
    {
      throw new GirException( "Failed to create tmp dir: " + dir );
    }
    return dir.toPath().toAbsolutePath().normalize();
  }
}
