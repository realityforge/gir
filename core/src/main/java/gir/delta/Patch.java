package gir.delta;

import gir.GirException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import javax.annotation.Nonnull;

public final class Patch
{
  private Patch()
  {
  }

  /**
   * Apply action to file to patch file.
   * If the action changes the contents of the file then the file will be updated on the filesystem.
   *
   * @param file   the file to process.
   * @param action the action to process the contents of the file.
   * @return true if the file was patched, false otherwise.
   */
  public static boolean file( @Nonnull final Path file, @Nonnull final Function<String, String> action )
  {
    if ( file.toFile().exists() )
    {
      final String content = loadContent( file );
      final String output = action.apply( content );
      if ( null != output && !content.equals( output ) )
      {
        try
        {
          Files.write( file, output.getBytes() );
        }
        catch ( final IOException e )
        {
          throw new GirException( "Error writing file '" + file + "' after file patched", e );
        }
        return true;
      }
    }
    return false;
  }

  @Nonnull
  private static String loadContent( @Nonnull final Path file )
  {
    try
    {
      return new String( Files.readAllBytes( file ) );
    }
    catch ( IOException e )
    {
      throw new GirException( "Error reading file '" + file + "' that attempting to patch", e );
    }
  }
}
