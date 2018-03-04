package gir;

import java.io.PrintStream;
import javax.annotation.Nonnull;

/**
 * A basic Messenger that emits to standard output.
 */
public class StandardMessenger
  implements Messenger
{
  private static final String PREFIX = "Gir: ";
  public static final int ERROR = 0;
  public static final int INFO = 1;
  public static final int DEBUG = 2;

  private final int _level;

  /**
   * Create the messenger.
   *
   * @param level the level of messages emitted.
   */
  public StandardMessenger( final int level )
  {
    _level = level;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void error( @Nonnull final String message )
  {
    log( ERROR, System.err, message );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void error( @Nonnull final String message, @Nonnull final Throwable throwable )
  {
    log( ERROR, System.err, message );
    log( ERROR, System.err, throwable.toString() );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void info( @Nonnull final String message )
  {
    log( INFO, System.out, message );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void info( @Nonnull final String message, @Nonnull final Throwable throwable )
  {
    log( INFO, System.out, message );
    log( INFO, System.out, throwable.toString() );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void debug( @Nonnull final String message )
  {
    log( DEBUG, System.out, message );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void debug( @Nonnull final String message, @Nonnull final Throwable throwable )
  {
    log( DEBUG, System.out, message );
    if ( _level >= DEBUG )
    {
      throwable.printStackTrace( System.out );
    }
  }

  /**
   * Log specified message to stream.
   *
   * @param level   the log level of message.
   * @param stream  the stream where message is logged.
   * @param message the message to log.
   */
  private void log( final int level, @Nonnull final PrintStream stream, @Nonnull final String message )
  {
    if ( _level >= level )
    {
      stream.println( PREFIX + message );
    }
  }
}
