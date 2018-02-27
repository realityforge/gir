package gir.ruby;

import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.Nonnull;
import gir.io.Exec;

public final class Ruby
{
  private Ruby()
  {
  }

  public static void rbenvExec( @Nonnull final String... args )
  {
    final int size = args.length + 2;
    final ArrayList<String> params = new ArrayList<>( size );
    params.add( "rbenv" );
    params.add( "exec" );
    params.addAll( Arrays.asList( args ) );
    Exec.system( params.toArray( new String[ size ] ) );
  }

  public static void bundleExec( @Nonnull final String... args )
  {
    final int size = args.length + 2;
    final ArrayList<String> params = new ArrayList<>( size );
    params.add( "bundle" );
    params.add( "exec" );
    params.addAll( Arrays.asList( args ) );
    rbenvExec( params.toArray( new String[ size ] ) );
  }

  public static void buildr( @Nonnull final String... args )
  {
    final int size = args.length + 1;
    final ArrayList<String> params = new ArrayList<>( size );
    params.add( "buildr" );
    params.addAll( Arrays.asList( args ) );
    bundleExec( params.toArray( new String[ size ] ) );
  }
}
