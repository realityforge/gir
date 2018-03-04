package gir;

public final class Gir
{
  private Gir()
  {
  }

  private static GirContext c_context = new GirContext();

  public static GirContext context()
  {
    return c_context;
  }
}
