package gir;

import javax.annotation.Nullable;

public final class GirTestUtil
{
  public GirTestUtil()
  {
  }

  public static void setContext( @Nullable final GirContext context )
  {
    Gir.setContext( context );
  }
}
