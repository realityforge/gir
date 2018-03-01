package gir.ruby;

import gir.AbstractGirTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BuildrTest
  extends AbstractGirTest
{
  @Test
  public void patchMavenCoordinates_matchSingleCoordinate()
    throws Exception
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
  public void patchMavenCoordinates_noTrailingContent()
    throws Exception
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
    throws Exception
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
    final String group = "com.google.elemental2";
    final String newContent = Buildr.patchMavenCoordinates( initialContent, group, "3.2-RTC456" );
    assertEquals( newContent, expectedContent );
  }
}
