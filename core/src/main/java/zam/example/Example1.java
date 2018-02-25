package zam.example;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import zam.Zam;
import zam.delta.Patch;
import zam.git.Git;
import zam.io.FileUtil;
import zam.ruby.Ruby;

public final class Example1
{
  public static void main( String[] args )
    throws Exception
  {
    final HashMap<String, Map<String, Long>> statistics = new HashMap<>();
    final Path workingDirectory = Paths.get( "/Users/peter/Code/zam" );
    FileUtil.inDirectory( workingDirectory, () -> {
      Git.clone( "https://github.com/react4j/react4j-todomvc.git", "react4j-todomvc" );
      final Path appDirectory = workingDirectory.resolve( "react4j-todomvc" );
      FileUtil.inDirectory( appDirectory, () -> {
        Git.fetch();
        Git.resetBranch();
        Git.checkout();
        Git.pull();
        Git.deleteLocalBranches();
        Stream.of( "raw", "arez", "dagger" ).forEach( branch -> {
          Git.checkout( branch );
          Git.pull();
          Git.clean();

          final Path file = appDirectory.resolve( "buildfile" );
          if ( Patch.file( file, c -> c.replaceAll( "1\\.6", "1.8" ) ) )
          {
            Git.add( file.toString() );
            Git.commit( "Patched version of java from 1.6 to 1.8" );
          }
          Ruby.buildr( "clean", "package" );

          final Map<String, Long> branchStatistics = statistics.computeIfAbsent( branch, b -> new HashMap<>() );
          final Path outputJsFile =
            appDirectory.resolve( "target/generated/gwt/react4j.todomvc.TodomvcProd/todomvc/todomvc.nocache.js" );
          final File jsFile = outputJsFile.toFile();
          assert jsFile.exists();
          final long length = jsFile.length();
          branchStatistics.put( "TodomvcProd/todomvc.size", length );
        } );
      } );
    } );

    for ( final Map.Entry<String, Map<String, Long>> branchStatistics : statistics.entrySet() )
    {
      final String branch = branchStatistics.getKey();
      System.out.println( branch + ": " + branchStatistics.getValue() );
    }

    Zam.context().close();
  }
}
