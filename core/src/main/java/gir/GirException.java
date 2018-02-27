package gir;

public class GirException
  extends RuntimeException
{
  public GirException()
  {
  }

  public GirException( final String message )
  {
    super( message );
  }

  public GirException( final String message, final Throwable cause )
  {
    super( message, cause );
  }

  public GirException( final Throwable cause )
  {
    super( cause );
  }
}
