package gir;

import gir.io.Exec;
import gir.io.FileUtil;
import java.io.File;
import javax.annotation.Nonnull;

public final class TestSetupUtil
{
  public static final String DEFAULT_NAME = "Your Name";
  public static final String DEFAULT_EMAIL = "you@example.com";

  private TestSetupUtil()
  {
  }

  public static void setupAsGitRepository( @Nonnull final File directory )
    throws Exception
  {
    setupAsGitRepository( directory, DEFAULT_NAME, DEFAULT_EMAIL );
  }

  public static void setupAsGitRepository( @Nonnull final File directory,
                                           @Nonnull final String name,
                                           @Nonnull final String email )
    throws Exception
  {
    FileUtil.inDirectory( directory.toPath(), () -> {
      Exec.system( "git", "init" );
      Exec.system( "git", "config", "--local", "user.email", name );
      Exec.system( "git", "config", "--local", "user.email", email );
      Exec.system( "git", "config", "--local", "commit.gpgsign", "false" );
      Exec.system( "git", "add", "." );
      Exec.system( "git", "commit", "-m", "Initial commit" );
    } );
  }
}
