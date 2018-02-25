package zam.delta;

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
    throws Exception
  {
    if ( file.toFile().exists() )
    {
      final String content = new String( Files.readAllBytes( file ) );
      final String output = action.apply( content );
      if ( null != output && !content.equals( output ) )
      {
        Files.write( file, output.getBytes() );
        return true;
      }
    }
    return false;
  }
}
