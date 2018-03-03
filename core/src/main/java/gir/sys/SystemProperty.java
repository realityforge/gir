package gir.sys;

import gir.GirException;
import javax.annotation.Nonnull;

public final class SystemProperty
{
  private SystemProperty()
  {
  }

  @Nonnull
  public static String get( @Nonnull final String key )
  {
    final String workDirValue = System.getProperty( key );
    if ( null == workDirValue )
    {
      throw new GirException( "Failed to locate required system property '" + key + "'" );
    }
    return workDirValue;
  }
}
