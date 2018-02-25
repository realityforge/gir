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

public final class Exec
{
  private Exec()
  {
  }

  @Nonnull
  private static ProcessBuilder newProcessBuilder()
  {

    final ProcessBuilder builder = new ProcessBuilder();
    builder.directory( FileUtil.getCurrentDirectory().toFile() );
    return builder;
  }

  private static int rawExec( @Nonnull final Consumer<ProcessBuilder> action,
                              @Nullable final Consumer<Process> processHandler )
    throws IOException, InterruptedException
  {
    final ProcessBuilder builder = newProcessBuilder();
    action.accept( builder );

    final Process process = builder.start();
    if ( null != processHandler )
    {
      processHandler.accept( process );
    }
    return process.waitFor();
  }

  private static int rawExec( @Nonnull final Consumer<ProcessBuilder> action )
    throws IOException, InterruptedException
  {
    return rawExec( action, null );
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
    throws Exception
  {
    final Consumer<ProcessBuilder> builderAction = builder -> {
      builder.inheritIO();
      action.accept( builder );
    };
    final int exitCode = rawExec( builderAction );
    if ( null != expectedExitCode && exitCode != expectedExitCode )
    {
      final String message = "Unexpected error code '" + exitCode + "' when expecting '" + expectedExitCode + "'";
      throw new IllegalStateException( message );
    }
  }

  public static void system( @Nonnull final Consumer<ProcessBuilder> action )
    throws Exception
  {
    system( action, 0 );
  }

  public static String capture( @Nonnull final Consumer<ProcessBuilder> action )
    throws Exception
  {
    return capture( action, 0 );
  }

  public static String capture( @Nonnull final Consumer<ProcessBuilder> action,
                                @Nullable final Integer expectedExitCode )
    throws Exception
  {
    final CompletableFuture<String> result = new CompletableFuture<>();
    final Consumer<ProcessBuilder> builderAction = builder -> {
      builder.redirectErrorStream( true );
      action.accept( builder );
    };
    final int exitCode = rawExec( builderAction, process -> pumpOutputToResult( result, process ) );
    if ( null != expectedExitCode && exitCode != expectedExitCode )
    {
      final String message = "Unexpected error code '" + exitCode + "' when expecting '" + expectedExitCode + "'";
      throw new IllegalStateException( message );
    }
    return result.get();
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
