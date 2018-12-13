package gir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The top level class from which to access Gir facilities.
 */
public final class Gir
{
  private Gir()
  {
  }

  @Nullable
  private static GirContext c_context;

  /**
   * Run the supplied Gir action.
   *
   * @param action the action.
   * @throws Exception if action throws an error.
   */
  public static void go( @Nonnull final Task action )
    throws Exception
  {
    if ( null != c_context )
    {
      throw new GirException( "Gir.go() invocation nested in another go() invocation" );
    }
    c_context = new GirContext();
    try
    {
      action.call();
    }
    finally
    {
      c_context.close();
      c_context = null;
    }
  }

  /**
   * Return the current context.
   * There can only be one activate context at any one time.
   *
   * @return the current context.
   */
  @Nonnull
  public static GirContext context()
  {
    if ( null == c_context )
    {
      throw new GirException( "Gir.context() invocation outside the context of a Gir.go() action" );
    }
    return c_context;
  }

  /**
   * Return the messenger associated with the context.
   *
   * @return the messenger associated with the context.
   */
  @Nonnull
  public static Messenger messenger()
  {
    return context().getMessenger();
  }

  static void setContext( @Nullable final GirContext context )
  {
    c_context = context;
  }
}
