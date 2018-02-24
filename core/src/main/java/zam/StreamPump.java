package zam;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import javax.annotation.Nonnull;

public class StreamPump
  implements Runnable
{
  private final InputStream _inputStream;
  private final OutputStream _outputStream;

  public StreamPump( @Nonnull final InputStream inputStream, @Nonnull final OutputStream outputStream )
  {
    _inputStream = Objects.requireNonNull( inputStream );
    _outputStream = Objects.requireNonNull( outputStream );
  }

  @Override
  public void run()
  {
    try
    {
      IoUtil.copy( _inputStream, _outputStream );
    }
    catch ( final IOException ioe )
    {
      //TODO: Fixme
      ioe.printStackTrace();
    }
  }
}
