package gir.delta;

import gir.GirException;
import gir.git.Git;
import gir.io.FileUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class Patch
{
  private Patch()
  {
  }

  /**
   * Apply action to file to patch file if it exists and add the changes.
   * If the file does not exist or the patch function produces no changes then no action will be taken. Otherwise
   * the file will be modified and the changed version added to git's staging area.
   *
   * @param directory     the base directory of the git repository.
   * @param fileToPatch   the file to process.
   * @param patchFunction the action to process the contents of the file.
   * @return true if the file was patched.
   */
  public static boolean patchAndAddFile( @Nonnull final Path directory,
                                         @Nonnull final Path fileToPatch,
                                         @Nonnull final Function<String, String> patchFunction )
  {
    if ( fileToPatch.toFile().exists() && Patch.file( fileToPatch, patchFunction ) )
    {
      FileUtil.inDirectory( directory, () -> Git.add( fileToPatch.toString() ) );
      return true;
    }
    return false;
  }

  /**
   * Apply action to file to patch file if it exists and commit the changes.
   * If the file does not exist or the patch function produces no changes then no action will be taken. Otherwise
   * the file will be modified and the changed version committed to git.
   *
   * @param directory             the base directory of the git repository.
   * @param fileToPatch           the file to process.
   * @param commitMessageFunction the function that produces a commit message if the file was patched.
   * @param patchFunction         the action to process the contents of the file.
   * @return true if the file was patched.
   */
  public static boolean patchAndCommitFile( @Nonnull final Path directory,
                                            @Nonnull final Path fileToPatch,
                                            @Nonnull final Supplier<String> commitMessageFunction,
                                            @Nonnull final Function<String, String> patchFunction )
  {
    if ( fileToPatch.toFile().exists() && file( fileToPatch, patchFunction ) )
    {
      FileUtil.inDirectory( directory, () -> {
        Git.add( fileToPatch.toString() );
        Git.commit( commitMessageFunction.get() );
      } );
      return true;
    }
    return false;
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
