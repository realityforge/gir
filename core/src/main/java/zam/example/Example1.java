package zam.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import zam.Zam;
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
      IoUtil.inDirectory( workingDirectory.resolve( "antix" ), () -> {
        Git.fetch();
        Git.resetBranch();
        Git.checkout();
        Git.pull();
      } );
    } );

    Zam.context().close();
  }
}
