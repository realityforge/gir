package gir.git;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import gir.io.Exec;
import gir.io.FileUtil;

public final class Git
{
  private static final Logger LOG = Logger.getLogger( Git.class.getName() );

  private Git()
  {
  }

  public static void clone( @Nonnull final String url, @Nonnull final String localName )
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
      final String remotes = Exec.capture( b -> {
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
      Exec.system( b -> {
        b.command( "git", "clone", url, targetPath.getFileName().toString() );
        b.directory( parentDirectory.toFile() );
      } );
    }
  }

  public static void fetch()
  {
    Exec.system( "git", "fetch", "--prune" );
  }

  public static void clean()
  {
    Exec.capture( "git", "clean", "-f", "-d", "-x" );
  }

  public static void pull()
  {
    pull( null );
  }

  public static void pull( @Nullable final String remote )
  {
    Exec.system( "git", "pull", remote );
  }

  public static void push()
  {
    push( null );
  }

  public static void push( @Nullable final String remote )
  {
    final String currentBranch = currentBranch();
    Exec.system( "git", "push", "--set-upstream", remote, currentBranch );
  }

  /**
   * Return the current branch.
   *
   * @return the current branch.
   */
  @Nonnull
  public static String currentBranch()
  {
    return Exec.capture( "git", "rev-parse", "--abbrev-ref", "HEAD" ).replace( "\n", "" );
  }

  /**
   * Return the local branches.
   *
   * @return the local branches.
   */
  public static List<String> localBranches()
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
  {
    return Arrays
      .stream( Exec.capture( "git", "branch", all ? "--all" : null ).split( "\n" ) )
      .filter( line -> !line.contains( " -> " ) )
      .map( line -> line.substring( 2 ) )
      .collect( Collectors.toList() );
  }

  public static void resetBranch()
  {
    resetBranch( null );
  }

  /**
   * Reset the index.
   */
  public static void resetIndex()
  {
    Exec.capture( "git", "reset" );
  }

  /**
   * Reset the index, local filesystem and potentially branch.
   *
   * @param branch the branch to reset to. Null to reset the branch to the current branch.
   */
  public static void resetBranch( @Nullable final String branch )
  {
    Exec.capture( "git", "reset", "--hard", branch );
    clean();
  }

  public static void add( @Nonnull final String path )
  {
    Exec.capture( "git", "add", path );
  }

  public static void checkout()
  {
    checkout( "master" );
  }

  public static void checkout( @Nonnull final String branch )
  {
    checkout( branch, false );
  }

  public static void checkout( @Nonnull final String branch, final boolean createUnlessPresent )
  {
    final boolean create =
      createUnlessPresent &&
      branches( true )
        .stream()
        .anyMatch( b -> b.equals( branch ) && b.equals( "remotes/origin/" + branch ) );
    Exec.capture( "git", "checkout", create ? "-b" : null, branch );
  }

  public static void commit( @Nonnull final String message )
  {
    Exec.capture( "git", "commit", "-m", message );
  }

  public static void deleteBranch( @Nonnull final String branch )
  {
    deleteBranch( branch, true );
  }

  public static void deleteBranch( @Nonnull final String branch, final boolean force )
  {
    Exec.capture( "git", "branch", force ? "-D" : "-d", branch );
  }

  public static void deleteLocalBranches()
  {
    localBranches().stream().filter( b -> !b.equals( "master" ) ).forEach( Git::deleteBranch );
  }
}
