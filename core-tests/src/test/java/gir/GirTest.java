package gir;

import java.util.concurrent.atomic.AtomicReference;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class GirTest
  extends AbstractGirTest
{
  @BeforeMethod
  @Override
  protected void beforeTest()
    throws Exception
  {
    super.beforeTest();
    Gir.setContext( null );
  }

  @Test
  public void context_noGo()
    throws Exception
  {
    final GirException exception = expectThrows( GirException.class, Gir::context );

    assertEquals( exception.getMessage(), "Gir.context() invocation outside the context of a Gir.go() action" );
  }

  @Test
  public void go()
    throws Exception
  {
    final AtomicReference<GirContext> reference = new AtomicReference<>();
    assertThrows( GirException.class, Gir::context );
    Gir.go( () -> {
      final GirContext context = Gir.context();
      assertNotNull( context );
      reference.set( context );
      assertEquals( context.isClosed(), false );
    } );
    assertThrows( GirException.class, Gir::context );
    assertEquals( reference.get().isClosed(), true );
  }
}
