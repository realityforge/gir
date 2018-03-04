package gir;

import javax.annotation.Nonnull;

/**
 * The top level class from which to access Gir facilities.
 */
public final class Gir
{
  private Gir()
  {
  }

  private static GirContext c_context = new GirContext();

  /**
   * Return the current context.
   * There can only be one activate context at any one time.
   *
   * @return the current context.
   */
  @Nonnull
  public static GirContext context()
  {
    assert null != c_context;
    return c_context;
  }
}
