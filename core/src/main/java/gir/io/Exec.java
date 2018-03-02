package gir.io;

import gir.Gir;
import gir.GirException;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility methods for invoking native commands.
 */
public final class Exec
{
  private Exec()
  {
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

  /**
   * Execute a command, attach output to current processes output and expect 0 exit code.
   *
   * @param args the strings that make up command. If a null parameter is passed, it is skipped.
   */
  public static void system( @Nonnull final String... args )
  {
    system( b -> cmd( b, args ) );
  }

  /**
   * Execute a command, attach output to current processes output and expect 0 exit code.
   *
   * @param action the callback responsible for setting up ProcessBuilder.
   */
  public static void system( @Nonnull final Consumer<ProcessBuilder> action )
  {
    system( action, 0 );
  }

  /**
   * Execute a command and attach output to current processes output.
   *
   * @param action           the callback responsible for setting up ProcessBuilder.
   * @param expectedExitCode the expected exitCode if the process.
   */
  public static void system( @Nonnull final Consumer<ProcessBuilder> action,
                             @Nullable final Integer expectedExitCode )
  {
    final AtomicReference<ProcessBuilder> builderRef = new AtomicReference<>();
    final Consumer<ProcessBuilder> builderAction = builder -> {
      builderRef.set( builder );
      builder.inheritIO();
      action.accept( builder );
    };
    final int exitCode = exec( builderAction );
    if ( null != expectedExitCode && exitCode != expectedExitCode )
    {
      throw new BadExitCodeException( builderRef.get().command(), expectedExitCode, exitCode, null );
    }
  }

  /**
   * Execute a command, capture the output and expect a 0 exit code.
   *
   * @param args the strings that make up command. If a null parameter is passed, it is skipped.
   * @return the output of the command.
   */
  public static String capture( @Nonnull final String... args )
  {
    return capture( b -> cmd( b, args ) );
  }

  /**
   * Execute a command, capture the output and expect a 0 exit code.
   *
   * @param action the callback responsible for setting up ProcessBuilder.
   * @return the output of the command.
   */
  public static String capture( @Nonnull final Consumer<ProcessBuilder> action )
  {
    return capture( action, 0 );
  }

  /**
   * Execute a command and capture the output.
   *
   * @param action           the callback responsible for setting up ProcessBuilder.
   * @param expectedExitCode the expected exitCode if the process.
   * @return the output of the command.
   */
  @Nonnull
  public static String capture( @Nonnull final Consumer<ProcessBuilder> action,
                                @Nullable final Integer expectedExitCode )
  {
    final AtomicReference<ProcessBuilder> builderRef = new AtomicReference<>();
    final CompletableFuture<String> result = new CompletableFuture<>();
    final Consumer<ProcessBuilder> builderAction = builder -> {
      builderRef.set( builder );
      builder.redirectErrorStream( true );
      action.accept( builder );
    };
    final int exitCode = exec( builderAction, process -> pumpOutputToResult( result, process ) );

    final String output;
    try
    {
      output = result.get();
    }
    catch ( final InterruptedException | ExecutionException e )
    {
      throw new GirException( "Failure to extract process output", e );
    }
    if ( null != expectedExitCode && exitCode != expectedExitCode )
    {
      throw new BadExitCodeException( builderRef.get().command(), expectedExitCode, exitCode, output );
    }
    return output;
  }

  /**
   * Low level utility for executing a process.
   * This method will return when the process completes.
   *
   * @param action the callback responsible for setting up ProcessBuilder.
   * @return the exitCode.
   * @see #exec(Consumer, Consumer)
   */
  static int exec( @Nonnull final Consumer<ProcessBuilder> action )
  {
    return exec( action, null );
  }

  /**
   * Low level utility for executing a process.
   * This method will return when the process completes.
   *
   * @param action         the callback responsible for setting up ProcessBuilder.
   * @param processHandler the callback passed a process.
   * @return the exitCode.
   */
  static int exec( @Nonnull final Consumer<ProcessBuilder> action, @Nullable final Consumer<Process> processHandler )
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
      return process.waitFor();
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

  /**
   * Pump the standard output from the process to the result future.
   *
   * @param process the process being monitored.
   * @param result  the future that output is pushed to.
   */
  private static void pumpOutputToResult( @Nonnull final CompletableFuture<String> result,
                                          @Nonnull final Process process )
  {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final BufferedOutputStream outputStream = new BufferedOutputStream( baos );
    final Future<?> future = Gir.context().run( new StreamPump( process.getInputStream(), outputStream ) );
    try
    {
      future.get();
    }
    catch ( final InterruptedException | ExecutionException ignored )
    {
      //ignore exception
    }
    result.complete( baos.toString() );
  }
}
