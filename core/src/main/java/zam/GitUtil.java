package zam;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class GitUtil
{
  private static final Logger LOG = Logger.getLogger( GitUtil.class.getName() );

  private GitUtil()
  {
  }

  public static void clone( @Nonnull final String url, @Nonnull final String localName )
    throws Exception
  {
    final Path parentDirectory = FileUtil.getCurrentDirectory();
    final Path targetPath = parentDirectory.resolve( localName );
    if ( targetPath.toFile().exists() && !targetPath.toFile().isDirectory() )
    {
      LOG.info( "Target path exists and is not a directory. Deleting." );
      FileUtil.deleteDir( targetPath );
    }

    if ( targetPath.toFile().exists() )
    {
      final String remotes = ExecUtil.capture( b -> {
        b.command( "git", "remote", "-v" );
        b.directory( targetPath.toFile() );
      }, null );
      final boolean found = Arrays.stream( remotes.split( "\n" ) )
        .map( line -> line.split( "[ \t]+" ) )
        .anyMatch( elements -> 3 == elements.length &&
                               "origin".equals( elements[ 0 ] ) &&
                               url.equals( elements[ 1 ] ) &&
                               "(fetch)".equals( elements[ 2 ] ) );
      if ( !found )
      {
        LOG.info( "Target path exists but is not a git directory with a remote " +
                  "named 'origin' that matches expect url '" + url + "'." );
        FileUtil.deleteDir( targetPath );
      }
    }

    if ( !targetPath.toFile().exists() )
    {
      ExecUtil.system( b -> {
        b.command( "git", "clone", url, targetPath.getFileName().toString() );
        b.directory( parentDirectory.toFile() );
      } );
    }
  }

  public static void fetch()
    throws Exception
  {
    ExecUtil.system( b -> b.command( "git", "fetch", "--prune" ) );
  }

  public static void clean()
    throws Exception
  {
    ExecUtil.capture( b -> b.command( "git", "clean", "-f", "-d", "-x" ) );
  }

  public static void pull()
    throws Exception
  {
    pull( null );
  }

  public static void pull( @Nullable final String remote )
    throws Exception
  {
    ExecUtil.system( b -> ExecUtil.cmd( b, "git", "pull", remote ) );
  }

  public static void push()
    throws Exception
  {
    push( null );
  }

  public static void push( @Nullable final String remote )
    throws Exception
  {
    final String currentBranch = currentBranch();
    ExecUtil.system( b -> ExecUtil.cmd( b, "git", "push", "--set-upstream", remote, currentBranch ) );
  }

  /**
   * Return the current branch.
   *
   * @return the current branch.
   */
  @Nonnull
  public static String currentBranch()
    throws Exception
  {
    return ExecUtil.capture( b -> b.command( "git", "rev-parse", "--abbrev-ref", "HEAD" ) ).replace( "\n", "" );
  }

  /**
   * Return the local branches.
   *
   * @return the local branches.
   */
  public static List<String> localBranches()
    throws Exception
  {
    return branches( false );
  }

  /**
   * Return the branches.
   *
   * @param all true if all branches should be returned.
   * @return the branches.
   */
  public static List<String> branches( final boolean all )
    throws Exception
  {
    return Arrays
      .stream( ExecUtil
                 .capture( b -> b.command( "git", "branch", all ? "--all" : null ) )
                 .split( "\n" ) )
      .filter( line -> !line.contains( " -> " ) )
      .map( line -> line.substring( 2 ) )
      .collect( Collectors.toList() );
  }

  public static void resetBranch()
    throws Exception
  {
    resetBranch( null );
  }

  /**
   * Reset the index.
   */
  public static void resetIndex()
    throws Exception
  {
    ExecUtil.capture( b -> ExecUtil.cmd( b, "git", "reset" ) );
  }

  /**
   * Reset the index, local filesystem and potentially branch.
   */
  public static void resetBranch( @Nullable final String branch )
    throws Exception
  {
    ExecUtil.capture( b -> ExecUtil.cmd( b, "git", "reset", "--hard", branch ) );
    clean();
  }

  public static void checkout()
    throws Exception
  {
    checkout( "master" );
  }

  public static void checkout( @Nonnull final String branch )
    throws Exception
  {
    checkout( branch, false );
  }

  public static void checkout( @Nonnull final String branch, final boolean createUnlessPresent )
    throws Exception
  {
    final boolean create =
      createUnlessPresent &&
      branches( true )
        .stream()
        .anyMatch( b -> b.equals( branch ) && b.equals( "remotes/origin/" + branch ) );
    ExecUtil.capture( b -> ExecUtil.cmd( b, "git", "checkout", create ? "-b" : null, branch ) );
  }
}
