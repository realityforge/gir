package zam;

public final class Zam
{
  private static ZamContext c_context = new ZamContext();

  public static ZamContext context()
  {
    return c_context;
  }
}
