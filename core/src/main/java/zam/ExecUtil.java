package zam;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ExecUtil
{
  private ExecUtil()
  {
  }

  @Nonnull
  private static ProcessBuilder newProcessBuilder()
  {

    final ProcessBuilder builder = new ProcessBuilder();
    builder.directory( FileUtil.getCurrentDirectory().toFile() );
    return builder;
  }

  public static int rawExec( @Nonnull final Consumer<ProcessBuilder> action,
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

  public static int rawExec( @Nonnull final Consumer<ProcessBuilder> action )
    throws IOException, InterruptedException
  {
    return rawExec( action, null );
  }

  public static void system( @Nonnull final Consumer<ProcessBuilder> action,
                             @Nullable final Integer expectedExitCode )
    throws IOException, InterruptedException
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
    throws IOException, InterruptedException
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
