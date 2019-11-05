package gir.bazel;

import gir.test.util.AbstractGirTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BazelTest
  extends AbstractGirTest
{
  @Test
  public void patchMavenCoordinates_3partCoord()
  {
    // This test covers off when this method is invoked for bazel-depgen configurations
    final String initialContent =
      "repositories:\n" +
      "  - name: central\n" +
      "    url: https://repo.maven.apache.org/maven2\n" +
      "  - name: stock\n" +
      "    url: https://stocksoftware.jfrog.io/stocksoftware/staging\n" +
      "options:\n" +
      "  aliasStrategy: ArtifactId\n" +
      "artifacts:\n" +
      "  - coord: com.google.gwt:gwt-user:2.8.2\n" +
      "  - coord: org.realityforge.arez:arez-core:0.152\n" +
      "  - coord: org.realityforge.arez:arez-processor:0.152\n" +
      "  - coord: org.realityforge.arez.spytools:arez-spytools:0.78\n" +
      "  - coord: org.realityforge.react4j:react4j-core:0.133\n" +
      "  - coord: org.realityforge.react4j:react4j-dom:0.133\n" +
      "  - coord: org.realityforge.react4j:react4j-processor:0.133\n";
    final String expectedContent =
      "repositories:\n" +
      "  - name: central\n" +
      "    url: https://repo.maven.apache.org/maven2\n" +
      "  - name: stock\n" +
      "    url: https://stocksoftware.jfrog.io/stocksoftware/staging\n" +
      "options:\n" +
      "  aliasStrategy: ArtifactId\n" +
      "artifacts:\n" +
      "  - coord: com.google.gwt:gwt-user:2.8.2\n" +
      "  - coord: org.realityforge.arez:arez-core:345\n" +
      "  - coord: org.realityforge.arez:arez-processor:345\n" +
      "  - coord: org.realityforge.arez.spytools:arez-spytools:0.78\n" +
      "  - coord: org.realityforge.react4j:react4j-core:0.133\n" +
      "  - coord: org.realityforge.react4j:react4j-dom:0.133\n" +
      "  - coord: org.realityforge.react4j:react4j-processor:0.133\n";
    final String group = "org.realityforge.arez";
    final String newContent = Bazel.patchMavenCoordinates( initialContent, group, "345" );
    assertEquals( newContent, expectedContent );
  }
}
