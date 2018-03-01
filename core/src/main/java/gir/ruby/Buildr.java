package gir.ruby;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Utilities to interact with the Build build system.
 */
public final class Buildr
{
  private Buildr()
  {
  }

  /**
   * Replace maven coordinates in the specified content with the specified group
   * with an updated maven coordinate using a different version. Maven coordinates
   * are expected to be in one of the formats:
   *
   * <ul>
   * <li>group::artifact:type:version</li>
   * <li>group::artifact:type:classifier:version</li>
   * </ul>
   *
   * @param content    the content to process.
   * @param group      the group of the maven artifacts to process.
   * @param newVersion the new version of the maven artifacts.
   * @return the update content with the maven coordinates updated.
   */
  @Nonnull
  public static String patchMavenCoordinates( @Nonnull final String content,
                                              @Nonnull final String group,
                                              @Nonnull final String newVersion )
  {
    final String regex =
      Pattern.quote( group ) +
      "(\\:[.a-zA-Z0-9\\-_]+)(\\:[.a-zA-Z0-9\\-_]+)(\\:[.a-zA-Z0-9\\-_]+)?(\\:[.a-zA-Z0-9\\-_]+)";
    final Pattern compile = Pattern.compile( regex );
    final Matcher matcher = compile.matcher( content );
    matcher.reset();
    boolean result = matcher.find();
    if ( result )
    {
      final StringBuffer sb = new StringBuffer();
      do
      {
        matcher.appendReplacement( sb, group + "$1$2$3:" + newVersion );
        result = matcher.find();
      } while ( result );
      matcher.appendTail( sb );
      return sb.toString();
    }
    return content;
  }
}
