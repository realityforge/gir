package gir.maven;

import gir.delta.Patch;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

public final class Maven
{
  private Maven()
  {
  }

  public static boolean patchPomProperty( @Nonnull final Path directory,
                                          @Nonnull final Supplier<String> commitMessageFunction,
                                          @Nonnull final String key,
                                          @Nonnull final String newValue )
  {
    return patchPom( directory,
                     commitMessageFunction,
                     c -> patchMavenProperty( c, key, newValue ) );
  }

  public static boolean patchPom( @Nonnull final Path directory,
                                  @Nonnull final Supplier<String> commitMessageFunction,
                                  @Nonnull final Function<String, String> patchFunction )
  {
    return Patch.patchAndCommitFile( directory, directory.resolve( "pom.xml" ), commitMessageFunction, patchFunction );
  }

  @Nonnull
  public static String patchMavenProperty( @Nonnull final String content,
                                           @Nonnull final String property,
                                           @Nonnull final String newValue )
  {
    final String regex =
      Pattern.quote( "<" + property + ">" ) + "(.*)" + Pattern.quote( "</" + property + ">" );
    final Pattern compile = Pattern.compile( regex );
    final Matcher matcher = compile.matcher( content );
    matcher.reset();
    boolean result = matcher.find();
    if ( result )
    {
      final StringBuffer sb = new StringBuffer();
      do
      {
        matcher.appendReplacement( sb, "<" + property + ">" + newValue + "</" + property + ">" );
        result = matcher.find();
      } while ( result );
      matcher.appendTail( sb );
      return sb.toString();
    }
    return content;
  }
}
