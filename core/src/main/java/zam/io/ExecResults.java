package zam.io;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExecResults
{
  /**
   * The builder from which this process was constructed.
   */
  @Nonnull
  private final ProcessBuilder _builder;
  /**
   * The process that executed if it could be created.
   */
  @Nonnull
  private final Process _process;
  /**
   * The exitcode if it could be extracted.
   */
  private final int _exitCode;
  /**
   * Output from the command.
   */
  @Nullable
  private String _output;

  public ExecResults( @Nonnull final ProcessBuilder builder,
                      @Nonnull final Process process,
                      final int exitCode )
  {
    _builder = Objects.requireNonNull( builder );
    _process = Objects.requireNonNull( process );
    _exitCode = exitCode;
  }

  @Nonnull
  public ProcessBuilder getBuilder()
  {
    return _builder;
  }

  @Nonnull
  public Process getProcess()
  {
    return _process;
  }

  public int getExitCode()
  {
    return _exitCode;
  }

  void setOutput( @Nonnull final String output )
  {
    _output = Objects.requireNonNull( output );
  }

  @Nullable
  public String getOutput()
  {
    return _output;
  }
}
