package gir;

import gir.io.Exec;
import gir.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

@SuppressWarnings( "SameParameterValue" )
public abstract class AbstractGirTest
{
  @BeforeMethod
  protected void beforeTest()
    throws Exception
  {
    BrainCheckTestUtil.resetConfig( false );
    FileUtil.setCurrentDirectory( FileUtil.cwd() );
  }

  @AfterMethod
  protected void afterTest()
    throws Exception
  {
    BrainCheckTestUtil.resetConfig( true );
  }

  @Nonnull
  protected final File createTempDirectory()
    throws IOException
  {
    final File file = File.createTempFile( "gir", "test" );
    assertTrue( file.delete() );
    assertTrue( file.mkdirs() );
    return file;
  }

  @Nonnull
  protected final File createGitRepository( @Nonnull final ThrowingConsumer<File> action )
    throws Exception
  {
    final File directory = createTempDirectory();
    action.accept( directory );
    TestSetupUtil.setupAsGitRepository( directory );
    return directory;
  }

  protected final void assertCommitSubject( @Nonnull final File repository, @Nonnull final String subject )
    throws Exception
  {
    assertCommitAttribute( repository, "%s", subject );
  }

  protected final void assertCommitAuthor( @Nonnull final File repository, @Nonnull final String author )
    throws Exception
  {
    assertCommitAttribute( repository, "%an", author );
  }

  protected final void assertCommitEmail( @Nonnull final File repository, @Nonnull final String email )
    throws Exception
  {
    assertCommitAttribute( repository, "%ae", email );
  }

  protected final void assertCommitAttribute( @Nonnull final File repository,
                                              @Nonnull final String formatKey,
                                              @Nonnull final Pattern pattern )
    throws Exception
  {
    assertCommitAttribute( repository, formatKey, pattern, 0 );
  }

  protected final void assertCommitAttribute( @Nonnull final File repository,
                                              @Nonnull final String formatKey,
                                              @Nonnull final String value )
    throws Exception
  {
    assertCommitAttribute( repository, formatKey, value, 0 );
  }

  protected final void assertCommitAttribute( @Nonnull final File repository,
                                              @Nonnull final String formatKey,
                                              @Nonnull final String value,
                                              final int commitIndex )
    throws Exception
  {
    assertCommitAttribute( repository, formatKey, Pattern.compile( Pattern.quote( value ) ), commitIndex );
  }

  protected final void assertCommitAttribute( @Nonnull final File repository,
                                              @Nonnull final String formatKey,
                                              @Nonnull final Pattern pattern,
                                              final int commitIndex )
    throws Exception
  {
    FileUtil.inDirectory( repository.toPath(), () -> {
      final String[] lines = Exec.capture( "git", "log", "--pretty=format:" + formatKey ).split( "\n" );
      final String line = lines[ commitIndex ];
      assertTrue( pattern.matcher( line ).matches(), "Pattern: " + pattern + " expected to match line: " + line );
    } );
  }
}
