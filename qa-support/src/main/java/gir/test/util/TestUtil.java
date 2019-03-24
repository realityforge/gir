package gir.test.util;

import gir.io.Exec;
import gir.io.FileUtil;
import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import static org.testng.Assert.*;

public final class TestUtil
{
  public static final String DEFAULT_NAME = "Your Name";
  public static final String DEFAULT_EMAIL = "you@example.com";

  private TestUtil()
  {
  }

  @Nonnull
  public static Path createGitRepository( @Nonnull final ThrowingConsumer<Path> action )
    throws Exception
  {
    final Path directory = FileUtil.createTempDir();
    action.accept( directory );
    TestUtil.setupAsGitRepository( directory );
    return directory;
  }

  public static void assertCommitSubject( @Nonnull final Path repository, @Nonnull final String subject )
    throws Exception
  {
    assertCommitAttribute( repository, "%s", subject );
  }

  public static void assertCommitAuthor( @Nonnull final Path repository, @Nonnull final String author )
    throws Exception
  {
    assertCommitAttribute( repository, "%an", author );
  }

  public static void assertCommitEmail( @Nonnull final Path repository, @Nonnull final String email )
    throws Exception
  {
    assertCommitAttribute( repository, "%ae", email );
  }

  public static void assertCommitAttribute( @Nonnull final Path repository,
                                            @Nonnull final String formatKey,
                                            @Nonnull final Pattern pattern )
    throws Exception
  {
    assertCommitAttribute( repository, formatKey, pattern, 0 );
  }

  public static void assertCommitAttribute( @Nonnull final Path repository,
                                            @Nonnull final String formatKey,
                                            @Nonnull final String value )
    throws Exception
  {
    assertCommitAttribute( repository, formatKey, value, 0 );
  }

  public static void assertCommitAttribute( @Nonnull final Path repository,
                                            @Nonnull final String formatKey,
                                            @Nonnull final String value,
                                            final int commitIndex )
    throws Exception
  {
    assertCommitAttribute( repository, formatKey, Pattern.compile( Pattern.quote( value ) ), commitIndex );
  }

  public static void assertCommitAttribute( @Nonnull final Path repository,
                                            @Nonnull final String formatKey,
                                            @Nonnull final Pattern pattern,
                                            final int commitIndex )
  {
    FileUtil.inDirectory( repository, () -> {
      final String[] lines = Exec.capture( "git", "log", "--pretty=format:" + formatKey ).split( "\n" );
      final String line = lines[ commitIndex ];
      assertTrue( pattern.matcher( line ).matches(), "Pattern: " + pattern + " expected to match line: " + line );
    } );
  }

  public static void setupAsGitRepository( @Nonnull final Path directory )
    throws Exception
  {
    setupAsGitRepository( directory, DEFAULT_NAME, DEFAULT_EMAIL );
  }

  public static void setupAsGitRepository( @Nonnull final Path directory,
                                           @Nonnull final String name,
                                           @Nonnull final String email )
    throws Exception
  {
    FileUtil.inDirectory( directory, () -> {
      Exec.system( "git", "init" );
      Exec.system( "git", "config", "--local", "user.email", name );
      Exec.system( "git", "config", "--local", "user.email", email );
      Exec.system( "git", "config", "--local", "commit.gpgsign", "false" );
      Exec.system( "git", "add", "." );
      Exec.system( "git", "commit", "-m", "Initial commit" );
    } );
  }
}
