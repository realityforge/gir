package gir.ruby;

import gir.test.util.AbstractGirTest;
import gir.test.util.TestUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BuildrTest
  extends AbstractGirTest
{
  @Test
  public void patchMavenCoordinates_matchSingleCoordinate()
  {
    final String initialContent =
      "artifacts:\n" +
      "  idea_codestyle: au.com.stocksoftware.idea.codestyle:idea-codestyle:xml:1.12\n" +
      "  anodoc: org.realityforge.anodoc:anodoc:jar:1.0.0\n" +
      "  javax_jsr305: com.google.code.findbugs:jsr305:jar:3.0.1\n" +
      "  guiceyloops: org.realityforge.guiceyloops:guiceyloops:jar:0.92\n" +
      "  braincheck: org.realityforge.braincheck:braincheck:jar:gwt:1.4.0\n";
    final String expectedContent =
      "artifacts:\n" +
      "  idea_codestyle: au.com.stocksoftware.idea.codestyle:idea-codestyle:xml:1.12\n" +
      "  anodoc: org.realityforge.anodoc:anodoc:jar:1.0.0\n" +
      "  javax_jsr305: com.google.code.findbugs:jsr305:jar:3.0.1\n" +
      "  guiceyloops: org.realityforge.guiceyloops:guiceyloops:jar:0.92\n" +
      "  braincheck: org.realityforge.braincheck:braincheck:jar:gwt:345\n";
    final String group = "org.realityforge.braincheck";
    final String newContent = Buildr.patchMavenCoordinates( initialContent, group, "345" );
    assertEquals( newContent, expectedContent );
  }

  @Test
  public void patchMavenCoordinates_NoMatch()
  {
    final String initialContent =
      "artifacts:\n" +
      "  braincheck: org.realityforge.braincheck:braincheck:jar:1.4.0";
    final String group = "org.realityforge.other";
    final String newContent = Buildr.patchMavenCoordinates( initialContent, group, "345" );
    assertEquals( newContent, initialContent );
  }

  @Test
  public void patchMavenCoordinates_noTrailingContent()
  {
    final String initialContent =
      "artifacts:\n" +
      "  braincheck: org.realityforge.braincheck:braincheck:jar:1.4.0";
    final String expectedContent =
      "artifacts:\n" +
      "  braincheck: org.realityforge.braincheck:braincheck:jar:345";
    final String group = "org.realityforge.braincheck";
    final String newContent = Buildr.patchMavenCoordinates( initialContent, group, "345" );
    assertEquals( newContent, expectedContent );
  }

  @Test
  public void patchMavenCoordinates_includesClassifier()
  {
    final String initialContent =
      "artifacts:\n" +
      "  braincheck: org.realityforge.braincheck:braincheck:jar:gwt:1.4.0\n";
    final String expectedContent =
      "artifacts:\n" +
      "  braincheck: org.realityforge.braincheck:braincheck:jar:gwt:345\n";
    final String group = "org.realityforge.braincheck";
    final String newContent = Buildr.patchMavenCoordinates( initialContent, group, "345" );
    assertEquals( newContent, expectedContent );
  }

  @Test
  public void patchMavenCoordinates_matchMultipleCoordinates()
  {
    final String initialContent =
      "artifacts:\n" +
      "  elemental2_core: com.google.elemental2:elemental2-core:jar:1.0.0-RC1\n" +
      "  elemental2_dom: com.google.elemental2:elemental2-dom:jar:1.0.0-RC1\n" +
      "  elemental2_promise: com.google.elemental2:elemental2-promise:jar:1.0.0-RC1\n";
    final String expectedContent =
      "artifacts:\n" +
      "  elemental2_core: com.google.elemental2:elemental2-core:jar:3.2-RTC456\n" +
      "  elemental2_dom: com.google.elemental2:elemental2-dom:jar:3.2-RTC456\n" +
      "  elemental2_promise: com.google.elemental2:elemental2-promise:jar:3.2-RTC456\n";
    final String group = "com.google.elemental2";
    final String newContent = Buildr.patchMavenCoordinates( initialContent, group, "3.2-RTC456" );
    assertEquals( newContent, expectedContent );
  }

  @Test
  public void patchBuildYmlDependency()
    throws Exception
  {
    final String initialContent =
      "artifacts:\n" +
      "  elemental2_core: com.google.elemental2:elemental2-core:jar:1.0.0-RC1\n" +
      "  elemental2_dom: com.google.elemental2:elemental2-dom:jar:1.0.0-RC1\n" +
      "  elemental2_promise: com.google.elemental2:elemental2-promise:jar:1.0.0-RC1\n";
    final String expectedContent =
      "artifacts:\n" +
      "  elemental2_core: com.google.elemental2:elemental2-core:jar:3.2-RTC456\n" +
      "  elemental2_dom: com.google.elemental2:elemental2-dom:jar:3.2-RTC456\n" +
      "  elemental2_promise: com.google.elemental2:elemental2-promise:jar:3.2-RTC456\n";
    final Path repository =
      TestUtil.createGitRepository( d -> Files.write( d.resolve( "build.yaml" ), initialContent.getBytes() ) );

    final String group = "com.google.elemental2";
    final boolean patched = Buildr.patchBuildYmlDependency( repository, group, "3.2-RTC456" );
    assertTrue( patched );

    TestUtil.assertCommitSubject( repository,
                                  "Update the `com.google.elemental2` artifacts to version `3.2-RTC456`" );

    final String output = new String( Files.readAllBytes( repository.resolve( "build.yaml" ) ) );
    assertEquals( output, expectedContent );
  }

  @Test
  public void patchBuildYmlDependency_noMatch()
    throws Exception
  {
    final String initialContent =
      "artifacts:\n" +
      "  elemental2_core: com.google.elemental2:elemental2-core:jar:1.0.0-RC1\n" +
      "  elemental2_dom: com.google.elemental2:elemental2-dom:jar:1.0.0-RC1\n" +
      "  elemental2_promise: com.google.elemental2:elemental2-promise:jar:1.0.0-RC1\n";

    final Path repository =
      TestUtil.createGitRepository( d -> Files.write( d.resolve( "build.yaml" ), initialContent.getBytes() ) );

    final String group = "com.google.other";
    final boolean patched = Buildr.patchBuildYmlDependency( repository, group, "3.2-RTC456" );
    assertFalse( patched );

    final String output = new String( Files.readAllBytes( repository.resolve( "build.yaml" ) ) );
    assertEquals( output, initialContent );
  }

  @Test
  public void patchBuildYmlDependency_noFile()
    throws Exception
  {
    final Path repository =
      TestUtil.createGitRepository( d -> Files.write( d.resolve( "README.md" ), "Blah".getBytes() ) );

    final boolean patched =
      Buildr.patchBuildYmlDependency( repository, "com.google.other", "3.2-RTC456" );
    assertFalse( patched );
  }
}
