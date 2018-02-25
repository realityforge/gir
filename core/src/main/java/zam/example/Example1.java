package zam.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import zam.Zam;
import zam.delta.Patch;
import zam.git.Git;
import zam.io.IoUtil;

public final class Example1
{
  public static void main( String[] args )
    throws Exception
  {
    final Path workingDirectory = Paths.get( "/Users/peter/Code/zam" );
    IoUtil.inDirectory( workingDirectory, () -> {
      Git.clone( "https://github.com/realityforge/antix.git", "antix" );
      final Path appDirectory = workingDirectory.resolve( "antix" );
      IoUtil.inDirectory( appDirectory, () -> {
        Git.fetch();
        Git.resetBranch();
        Git.checkout();
        Git.pull();
        final Path file = appDirectory.resolve( "buildfile" );
        if ( Patch.file( file, c -> c.replaceAll( "1\\.6", "1.8" ) ) )
        {
          Git.add( file.toString() );
          Git.commit( "Patched version of java from 1.6 to 1.8" );
        }
      } );
    } );

    Zam.context().close();
  }
}
