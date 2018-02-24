package zam;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;

public final class ZamContext
  implements Closeable
{
  private final ExecutorService _executorService = Executors.newCachedThreadPool();
  private boolean _closed;

  public <V> Future<V> run( @Nonnull final Callable<V> action )
  {
    assert !isClosed();
    return _executorService.submit( action );
  }

  public Future<?> run( @Nonnull final Runnable action )
  {
    assert !isClosed();
    return _executorService.submit( action );
  }

  public boolean isClosed()
  {
    return _closed;
  }

  @Override
  public void close()
    throws IOException
  {
    _closed = true;
    _executorService.shutdown();
  }
}
