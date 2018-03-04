package gir;

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
  public static GirContext context()
  {
    return c_context;
  }
}
