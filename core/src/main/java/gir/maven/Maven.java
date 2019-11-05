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

  /**
   * Replace maven coordinates in the specified content with the specified group
   * with an updated maven coordinate using a different version. Maven coordinates
   * are expected to be in the format <tt>group:artifact:version</tt>.
   *
   * @param content    the content to process.
   * @param group      the group of the maven artifacts to process.
   * @param newVersion the new version of the maven artifacts.
   * @return the update content with the maven coordinates updated.
   */
  @Nonnull
  public static String patchMaven3PartCoordinates( @Nonnull final String content,
                                                   @Nonnull final String group,
                                                   @Nonnull final String newVersion )
  {
    final String regex = Pattern.quote( group ) + "(:[.a-zA-Z0-9\\-_]+)(:[.a-zA-Z0-9\\-_]+)([ \n]|$)";
    final Pattern compile = Pattern.compile( regex );
    final Matcher matcher = compile.matcher( content );
    matcher.reset();
    boolean result = matcher.find();
    if ( result )
    {
      final StringBuffer sb = new StringBuffer();
      do
      {
        matcher.appendReplacement( sb, group + "$1:" + newVersion + "$3" );
        result = matcher.find();
      } while ( result );
      matcher.appendTail( sb );
      return sb.toString();
    }
    return content;
  }

  /**
   * Replace maven coordinates in the specified content with the specified group
   * with an updated maven coordinate using a different version. Maven coordinates
   * are expected to be in the format <tt>group:artifact:type:version</tt>.
   *
   * @param content    the content to process.
   * @param group      the group of the maven artifacts to process.
   * @param newVersion the new version of the maven artifacts.
   * @return the update content with the maven coordinates updated.
   */
  @Nonnull
  public static String patchMaven4PartCoordinates( @Nonnull final String content,
                                                   @Nonnull final String group,
                                                   @Nonnull final String newVersion )
  {
    final String regex =
      Pattern.quote( group ) + "(:[.a-zA-Z0-9\\-_]+)(:[.a-zA-Z0-9\\-_]+)(:[.a-zA-Z0-9\\-_]+)([ \n]|$)";
    final Pattern pattern = Pattern.compile( regex );
    final Matcher matcher = pattern.matcher( content );
    matcher.reset();
    boolean result = matcher.find();
    if ( result )
    {
      final StringBuffer sb = new StringBuffer();
      do
      {
        matcher.appendReplacement( sb, group + "$1$2:" + newVersion + "$4" );
        result = matcher.find();
      } while ( result );
      matcher.appendTail( sb );
      return sb.toString();
    }
    return content;
  }

  /**
   * Replace maven coordinates in the specified content with the specified group
   * with an updated maven coordinate using a different version. Maven coordinates
   * are expected to be in the format <tt>group:artifact:type:classifier:version</tt>.
   *
   * @param content    the content to process.
   * @param group      the group of the maven artifacts to process.
   * @param newVersion the new version of the maven artifacts.
   * @return the update content with the maven coordinates updated.
   */
  @Nonnull
  public static String patchMaven5PartCoordinates( @Nonnull final String content,
                                                   @Nonnull final String group,
                                                   @Nonnull final String newVersion )
  {
    final String regex = Pattern.quote( group ) +
                         "(:[.a-zA-Z0-9\\-_]+)(:[.a-zA-Z0-9\\-_]+)(:[.a-zA-Z0-9\\-_]+)(:[.a-zA-Z0-9\\-_]+)([ \n]|$)";
    final Pattern compile = Pattern.compile( regex );
    final Matcher matcher = compile.matcher( content );
    matcher.reset();
    boolean result = matcher.find();
    if ( result )
    {
      final StringBuffer sb = new StringBuffer();
      do
      {
        matcher.appendReplacement( sb, group + "$1$2$3:" + newVersion + "$5" );
        result = matcher.find();
      } while ( result );
      matcher.appendTail( sb );
      return sb.toString();
    }
    return content;
  }
}
