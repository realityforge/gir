package zam.io;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import zam.Zam;
import zam.ZamException;

public final class Exec
{
  private Exec()
  {
  }

  @Nonnull
  private static ExecResults exec( @Nonnull final Consumer<ProcessBuilder> action,
                                   @Nullable final Consumer<Process> processHandler )
  {
    final ProcessBuilder builder = new ProcessBuilder();
    builder.directory( FileUtil.getCurrentDirectory().toFile() );
    action.accept( builder );
    try
    {
      final Process process = builder.start();
      if ( null != processHandler )
      {
        processHandler.accept( process );
      }
      final int exitCode = process.waitFor();
      return new ExecResults( builder, process, exitCode );
    }
    catch ( final IOException ioe )
    {
      throw new ErrorStartingProcessException( builder.command(), ioe );
    }
    catch ( final InterruptedException ie )
    {
      throw new ErrorWaitingForProcessException( builder.command(), ie );
    }
  }

  @Nonnull
  private static ExecResults exec( @Nonnull final Consumer<ProcessBuilder> action )
  {
    return exec( action, null );
  }

  /**
   * Configure ProcessBuilder with specified command.
   * If any arguments are null then they are skipped when building up commands.
   *
   * @param builder the ProcessBuilder to update.
   * @param args    the command and arguments with possible null values.
   */
  public static void cmd( @Nonnull final ProcessBuilder builder, @Nonnull final String... args )
  {
    builder.command( Arrays.stream( args )
                       .filter( Objects::nonNull )
                       .collect( Collectors.toCollection( ArrayList::new ) ) );
  }

  public static void system( @Nonnull final Consumer<ProcessBuilder> action,
                             @Nullable final Integer expectedExitCode )
  {
    final Consumer<ProcessBuilder> builderAction = builder -> {
      builder.inheritIO();
      action.accept( builder );
    };
    final ExecResults results = exec( builderAction );
    final int exitCode = results.getExitCode();
    if ( null != expectedExitCode && exitCode != expectedExitCode )
    {
      throw new BadExitCodeException( results.getBuilder().command(), expectedExitCode, exitCode, results.getOutput() );
    }
  }

  public static void system( @Nonnull final Consumer<ProcessBuilder> action )
  {
    system( action, 0 );
  }

  public static void system( @Nonnull final String... args )
  {
    system( b -> cmd( b, args ) );
  }

  public static String capture( @Nonnull final String... args )
  {
    return capture( b -> cmd( b, args ) );
  }

  public static String capture( @Nonnull final Consumer<ProcessBuilder> action )
  {
    return capture( action, 0 );
  }

  public static String capture( @Nonnull final Consumer<ProcessBuilder> action,
                                @Nullable final Integer expectedExitCode )
  {
    final CompletableFuture<String> result = new CompletableFuture<>();
    final Consumer<ProcessBuilder> builderAction = builder -> {
      builder.redirectErrorStream( true );
      action.accept( builder );
    };
    final ExecResults results = exec( builderAction, process -> pumpOutputToResult( result, process ) );
    final int exitCode = results.getExitCode();

    try
    {
      results.setOutput( result.get() );
    }
    catch ( final InterruptedException | ExecutionException e )
    {
      throw new ZamException( "Failure to extract process output", e );
    }
    if ( null != expectedExitCode && exitCode != expectedExitCode )
    {
      throw new BadExitCodeException( results.getBuilder().command(), expectedExitCode, exitCode, results.getOutput() );
    }

    return results.getOutput();
  }

  private static void pumpOutputToResult( @Nonnull final CompletableFuture<String> result,
                                          @Nonnull final Process process )
  {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final BufferedOutputStream outputStream = new BufferedOutputStream( baos );
    final Future<?> future = Zam.context().run( new StreamPump( process.getInputStream(), outputStream ) );
    try
    {
      future.get();
      result.complete( baos.toString() );
    }
    catch ( final InterruptedException | ExecutionException ignored )
    {
      //ignore exception
    }
  }
}
