package gir;

import javax.annotation.Nonnull;

/**
 * Interface that implemented by code to communicate messages with user interface.
 */
public interface Messenger
{
  /**
   * Report an error message to user.
   *
   * @param message the message.
   */
  void error( @Nonnull String message );

  /**
   * Report an error message to user.
   *
   * @param message   the message.
   * @param throwable the exception that triggered message.
   */
  void error( @Nonnull String message, @Nonnull Throwable throwable );

  /**
   * Report an informational message to user.
   *
   * @param message the message.
   */
  void info( @Nonnull String message );

  /**
   * Report an informational message to user.
   *
   * @param message   the message.
   * @param throwable the exception that triggered message.
   */
  void info( @Nonnull String message, @Nonnull Throwable throwable );

  /**
   * Report a debug message to user.
   *
   * @param message the message.
   */
  void debug( @Nonnull String message );

  /**
   * Report a debug message to user.
   *
   * @param message   the message.
   * @param throwable the exception that triggered message.
   */
  void debug( @Nonnull String message, @Nonnull Throwable throwable );
}
