package gir.bazel;

import gir.delta.Patch;
import gir.maven.Maven;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * Utilities to interact with the Bazel build system.
 */
public final class Bazel
{
  private Bazel()
  {
  }

  /**
   * Replace maven coordinates in the specified content with the specified group
   * with an updated maven coordinate using a different version. Maven coordinates
   * are expected to be in one of the formats:
   *
   * <ul>
   * <li>group:artifact:version</li>
   * <li>group:artifact:type:version</li>
   * <li>group:artifact:type:classifier:version</li>
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
    final String content2 = Maven.patchMaven3PartCoordinates( content, group, newVersion );
    final String content3 = Maven.patchMaven4PartCoordinates( content2, group, newVersion );
    return Maven.patchMaven5PartCoordinates( content3, group, newVersion );
  }
}
