package zam;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

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
}
