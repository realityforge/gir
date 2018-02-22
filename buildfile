require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/jacoco'

PROVIDED_DEPS = [:javax_jsr305, :anodoc]
TEST_DEPS = [:guiceyloops]

# JDK options passed to test environment. Essentially turns assertions on.
ZAM_TEST_OPTIONS =
  {
    'braincheck.environment' => 'development'
  }

desc 'Zam: Codebase Automation Library'
define 'zam' do
  project.group = 'org.realityforge.zam'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/zam')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'Zam Core'
  define 'core' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with PROVIDED_DEPS,
                 :braincheck

    test.options[:properties] = ZAM_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  desc 'Zam Integration Tests'
  define 'integration-tests' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    test.options[:properties] = ZAM_TEST_OPTIONS.merge('zam.integration_fixture_dir' => _('src/test/resources'))
    test.options[:java_args] = ['-ea']

    test.using :testng
    test.compile.with TEST_DEPS,
                      project('core').package(:jar),
                      project('core').compile.dependencies
  end

  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development -Dzam.output_fixture_data=false -Dzam.integration_fixture_dir=integration-tests/src/test/resources')
  ipr.add_component_from_artifact(:idea_codestyle)
  ipr.extra_modules << '../realityforge_backpack/realityforge_backpack.iml'
end

Buildr.projects.each do |project|
  unless project.name == 'zam'
    project.doc.options.merge!('Xdoclint:all,-reference' => true)
  end
end
