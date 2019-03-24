package gir.io;

import gir.test.util.AbstractGirTest;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ExecTest
  extends AbstractGirTest
{
  @Test
  public void cmd()
  {
    final ProcessBuilder builder = new ProcessBuilder();
    Exec.cmd( builder, "echo", "hi" );

    final List<String> command = builder.command();
    assertEquals( command.size(), 2 );
    assertEquals( command.get( 0 ), "echo" );
    assertEquals( command.get( 1 ), "hi" );
  }

  @Test
  public void cmd_withNulls()
  {
    final ProcessBuilder builder = new ProcessBuilder();
    Exec.cmd( builder, "git", "push", null, "origin" );

    final List<String> command = builder.command();
    assertEquals( command.size(), 3 );
    assertEquals( command.get( 0 ), "git" );
    assertEquals( command.get( 1 ), "push" );
    assertEquals( command.get( 2 ), "origin" );
  }

  @Test
  public void exec()
  {
    final int exitCode = Exec.exec( builder -> builder.command( "echo", "hi" ) );

    assertEquals( exitCode, 0 );
  }

  @Test
  public void exec_withNonZeroExitCode()
  {
    final int exitCode = Exec.exec( builder -> builder.command( "false" ) );

    assertEquals( exitCode, 1 );
  }

  @Test
  public void exec_withProcessAction()
  {
    final int exitCode = Exec.exec( builder -> builder.command( "false" ), Assert::assertNotNull );

    assertEquals( exitCode, 1 );
  }

  @Test
  public void exec_errorStartingProcess()
  {
    final ErrorStartingProcessException exception =
      expectThrows( ErrorStartingProcessException.class,
                    () -> Exec.exec( builder -> builder.command( "no_exist_cmd" ) ) );

    final List<String> command = exception.getCommand();
    assertEquals( command.size(), 1 );
    assertEquals( command.get( 0 ), "no_exist_cmd" );
  }

  @Test
  public void exec_threadInterruptedProcess()
  {
    final Thread thread = Thread.currentThread();
    final ErrorWaitingForProcessException exception =
      expectThrows( ErrorWaitingForProcessException.class,
                    () -> Exec.exec( builder -> builder.command( "sleep", "10000" ),
                                     process -> new Thread( () -> {
                                       // Wait some small amount of time and then interupt the original thread
                                       try
                                       {
                                         Thread.sleep( 100 );
                                       }
                                       catch ( final InterruptedException ignored )
                                       {
                                       }
                                       thread.interrupt();
                                     } ).start() ) );

    final List<String> command = exception.getCommand();
    assertEquals( command.size(), 2 );
    assertEquals( command.get( 0 ), "sleep" );
    assertEquals( command.get( 1 ), "10000" );
  }

  @Test
  public void system()
  {
    Exec.system( "echo", "hello" );
  }

  @Test
  public void system_doNotCareAboutExitCode()
  {
    Exec.system( b -> Exec.cmd( b, "bash", "-c", "echo hello; exit 2" ), null );
  }

  @Test
  public void system_badExitStatus()
  {
    final BadExitCodeException exception =
      expectThrows( BadExitCodeException.class,
                    () -> Exec.system( b -> Exec.cmd( b, "bash", "-c", "exit 2" ) ) );

    final List<String> command = exception.getCommand();
    assertEquals( exception.getActualExitCode(), 2 );
    assertEquals( exception.getExpectedExitCode(), 0 );
    assertEquals( exception.getOutput(), null );
    assertEquals( command.size(), 3 );
    assertEquals( command.get( 0 ), "bash" );
    assertEquals( command.get( 1 ), "-c" );
    assertEquals( command.get( 2 ), "exit 2" );
  }

  @Test
  public void capture()
  {
    final String output = Exec.capture( "echo", "hello" );
    assertEquals( output, "hello\n" );
  }

  @Test
  public void capture_doNotCareAboutExitCode()
  {
    final String output = Exec.capture( b -> Exec.cmd( b, "bash", "-c", "echo hello; exit 2" ), null );
    assertEquals( output, "hello\n" );
  }

  @Test
  public void capture_badExitStatus()
  {
    final BadExitCodeException exception =
      expectThrows( BadExitCodeException.class,
                    () -> Exec.capture( b -> Exec.cmd( b, "bash", "-c", "exit 2" ) ) );

    final List<String> command = exception.getCommand();
    assertEquals( exception.getActualExitCode(), 2 );
    assertEquals( exception.getExpectedExitCode(), 0 );
    assertEquals( exception.getOutput(), "" );
    assertEquals( command.size(), 3 );
    assertEquals( command.get( 0 ), "bash" );
    assertEquals( command.get( 1 ), "-c" );
    assertEquals( command.get( 2 ), "exit 2" );
  }

  @Test
  public void capture_badExitStatusAndOutput()
  {
    final BadExitCodeException exception =
      expectThrows( BadExitCodeException.class,
                    () -> Exec.capture( b -> Exec.cmd( b, "bash", "-c", "echo hi; exit 2" ) ) );

    final List<String> command = exception.getCommand();
    assertEquals( exception.getActualExitCode(), 2 );
    assertEquals( exception.getExpectedExitCode(), 0 );
    assertEquals( exception.getOutput(), "hi\n" );
    assertEquals( command.size(), 3 );
    assertEquals( command.get( 0 ), "bash" );
    assertEquals( command.get( 1 ), "-c" );
    assertEquals( command.get( 2 ), "echo hi; exit 2" );
  }

  @Test
  public void capture_errorStartingProcess()
  {
    final ErrorStartingProcessException exception =
      expectThrows( ErrorStartingProcessException.class,
                    () -> Exec.capture( "no_exist_cmd" ) );

    final List<String> command = exception.getCommand();
    assertEquals( command.size(), 1 );
    assertEquals( command.get( 0 ), "no_exist_cmd" );
  }
}
