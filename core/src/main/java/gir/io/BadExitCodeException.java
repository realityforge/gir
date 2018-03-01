package gir.io;

import gir.GirException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BadExitCodeException
  extends GirException
{
  @Nonnull
  private final List<String> _command;
  private final int _expectedExitCode;
  private final int _actualExitCode;
  @Nullable
  private final String _output;

  public BadExitCodeException( @Nonnull final List<String> command,
                               final int expectedExitCode,
                               final int actualExitCode,
                               @Nullable final String output )
  {
    _command = Objects.requireNonNull( command );
    _expectedExitCode = expectedExitCode;
    _actualExitCode = actualExitCode;
    _output = output;
  }

  @Nonnull
  public List<String> getCommand()
  {
    return _command;
  }

  public int getExpectedExitCode()
  {
    return _expectedExitCode;
  }

  public int getActualExitCode()
  {
    return _actualExitCode;
  }

  @Nullable
  public String getOutput()
  {
    return _output;
  }

  @Override
  public String toString()
  {
    final String output = getOutput();
    return "Error executing command: " + getCommand() +
           "\n\tExpected Exitcode: " + getExpectedExitCode() +
           "\n\tActual Exitcode: " + getActualExitCode() +
           "\nOutput:\n" + ( null == output ? "" : output );
  }
}
