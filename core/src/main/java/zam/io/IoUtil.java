package zam.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import zam.Task;

public final class IoUtil
{
  public static final int DEFAULT_BUFFER_SIZE = 2 * 1024;

  public static void inDirectory( @Nonnull final Path directory, @Nonnull final Task action )
    throws Exception
  {
    final Path initial = FileUtil.getCurrentDirectory();
    try
    {
      FileUtil.setCurrentDirectory( directory );
      action.call();
    }
    finally
    {
      FileUtil.setCurrentDirectory( initial );
    }
  }

  public static void copy( @Nonnull final InputStream input, @Nonnull final OutputStream output )
    throws IOException
  {
    copy( input, output, DEFAULT_BUFFER_SIZE );
  }

  public static void copy( @Nonnull final InputStream input, @Nonnull final OutputStream output, final int bufferSize )
    throws IOException
  {
    // Java9 can use input.transferTo(output)
    try ( final InputStream in = input; final OutputStream out = output )
    {
      final byte[] buffer = new byte[ bufferSize ];
      int bytesRead = in.read( buffer );
      while ( -1 != bytesRead )
      {
        out.write( buffer, 0, bytesRead );
        bytesRead = in.read( buffer );
      }
    }
    output.flush();
  }
}
