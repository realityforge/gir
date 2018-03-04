package gir;

import gir.test.util.AbstractGirTest;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class GirContextTest
  extends AbstractGirTest
{
  @Test
  public void close()
    throws Exception
  {
    final GirContext context = new GirContext();
    assertFalse( context.isClosed() );
    assertFalse( context.getExecutorService().isShutdown() );

    context.close();

    assertTrue( context.isClosed() );
    assertTrue( context.getExecutorService().isShutdown() );
  }

  @Test
  public void close_onClose()
    throws Exception
  {
    final GirContext context = new GirContext();

    context.close();

    final GirException exception = expectThrows( GirException.class, context::close );

    assertEquals( exception.getMessage(), "GirContext.close() invoked on closed context" );
  }

  @Test
  public void run_Runnable()
    throws Exception
  {
    final GirContext context = new GirContext();

    final CountDownLatch step2 = new CountDownLatch( 1 );
    final Future<?> future = context.run( () -> {
      try
      {
        step2.await();
      }
      catch ( final InterruptedException ignored )
      {
      }
    } );

    assertFalse( future.isDone() );
    step2.countDown();
    Thread.sleep( 1 );
    assertTrue( future.isDone() );
    future.get();
  }

  @Test
  public void run_Callable()
    throws Exception
  {
    final String result = "SomeRandomValue";
    final GirContext context = new GirContext();

    final CountDownLatch step2 = new CountDownLatch( 1 );
    final Future<String> future = context.run( () -> {
      try
      {
        step2.await();
      }
      catch ( final InterruptedException ignored )
      {
      }
      return result;
    } );

    assertFalse( future.isDone() );
    step2.countDown();
    Thread.sleep( 1 );
    assertTrue( future.isDone() );
    assertEquals( future.get(), result );
  }

  @Test
  public void run_Callable_whenClosed()
    throws Exception
  {
    final String result = "SomeRandomValue";
    final GirContext context = new GirContext();

    context.close();

    final Callable<String> action = () -> result;
    final GirException exception = expectThrows( GirException.class, () -> context.run( action ) );
    assertEquals( exception.getMessage(), "GirContext.run() invoked on closed context" );
  }

  @Test
  public void run_Runnable_whenClosed()
    throws Exception
  {
    final String result = "SomeRandomValue";
    final GirContext context = new GirContext();

    context.close();

    final Runnable action = () -> {
    };
    final GirException exception = expectThrows( GirException.class, () -> context.run( action ) );
    assertEquals( exception.getMessage(), "GirContext.run() invoked on closed context" );
  }
}
