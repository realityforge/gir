package gir;

public class ZamException
  extends RuntimeException
{
  public ZamException()
  {
  }

  public ZamException( final String message )
  {
    super( message );
  }

  public ZamException( final String message, final Throwable cause )
  {
    super( message, cause );
  }

  public ZamException( final Throwable cause )
  {
    super( cause );
  }
}
