package gir.ruby;

import gir.delta.Patch;
import gir.maven.Maven;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;
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
    final String content3 = Maven.patchMaven4PartCoordinates( content, group, newVersion );
    return Maven.patchMaven5PartCoordinates( content3, group, newVersion );
  }

  /**
   * Modify the <code>build.yaml</code> file in the specified directory. If the <code>build.yaml</code> file
   * does not exist or the patch function produces no changes then no action will be taken. Otherwise
   * the <code>build.yaml</code> file will be modified and the changed version committed to git.
   *
   * @param directory             the directory potentially containing the build file.
   * @param commitMessageFunction the function to generate commit message.
   * @param patchFunction         the function that patches file.
   * @return true if a change was made, false otherwise.
   */
  public static boolean patchBuildYml( @Nonnull final Path directory,
                                       @Nonnull final Supplier<String> commitMessageFunction,
                                       @Nonnull final Function<String, String> patchFunction )
  {
    return Patch.patchAndCommitFile( directory,
                                     directory.resolve( "build.yaml" ),
                                     commitMessageFunction,
                                     patchFunction );
  }

  /**
   * Modify the <code>build.yaml</code> file in the specified directory, updating all artifacts with the
   * specified group to the specified version. If the <code>build.yaml</code> file does not exist or the
   * dependency does not exist in the <code>build.yaml</code> file then no action will be taken. Otherwise
   * the <code>build.yaml</code> file will be modified and the changed version committed to git.
   *
   * @param directory  the directory potentially containing the build file.
   * @param group      the group of the maven artifacts to process.
   * @param newVersion the new version of the maven artifacts.
   * @return true if a change was made, false otherwise.
   */
  public static boolean patchBuildYmlDependency( @Nonnull final Path directory,
                                                 @Nonnull final String group,
                                                 @Nonnull final String newVersion )
  {
    return patchBuildYml( directory,
                          () -> "Update the `" + group + "` artifacts to version `" + newVersion + "`",
                          c -> patchMavenCoordinates( c, group, newVersion ) );
  }
}
