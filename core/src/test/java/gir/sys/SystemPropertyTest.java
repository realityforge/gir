package gir.sys;

import gir.AbstractGirTest;
import gir.GirException;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class SystemPropertyTest
  extends AbstractGirTest
{
  @Test
  public void get()
    throws Exception
  {
    assertEquals( SystemProperty.get( "user.dir" ), System.getProperty( "user.dir" ) );
  }

  @Test
  public void get_missing()
    throws Exception
  {
    final GirException exception = expectThrows( GirException.class, () -> SystemProperty.get( "no-exist" ) );
    assertEquals( exception.getMessage(), "Failed to locate required system property 'no-exist'" );
  }
}
