require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/jacoco'

Buildr::MavenCentral.define_publish_tasks(:profile_name => 'org.realityforge', :username => 'realityforge')

PROVIDED_DEPS = [:javax_annotation]
TEST_DEPS = [:guiceyloops]

# JDK options passed to test environment. Essentially turns assertions on.
GIR_TEST_OPTIONS =
  {
    'braincheck.environment' => 'development'
  }

desc 'Gir: Codebase Automation Library'
define 'gir' do
  project.group = 'org.realityforge.gir'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/gir')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'Gir Core'
  define 'core' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with PROVIDED_DEPS,
                 :braincheck

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'Gir QA util'
  define 'qa-support' do
    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :testng

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'Gir QA util'
  define 'core-tests' do
    test.compile.with project('core').package(:jar),
                      project('core').compile.dependencies,
                      project('qa-support').package(:jar),
                      project('qa-support').compile.dependencies

    test.options[:properties] = GIR_TEST_OPTIONS
    test.options[:java_args] = ['-ea']
  end

  desc 'Gir Integration Tests'
  define 'integration-tests' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    test.options[:properties] = GIR_TEST_OPTIONS.merge('gir.integration_fixture_dir' => _('src/test/resources'))
    test.options[:java_args] = ['-ea']

    test.using :testng
    test.compile.with TEST_DEPS,
                      project('core').package(:jar),
                      project('core').compile.dependencies
  end

  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development -Dgir.output_fixture_data=false -Dgir.integration_fixture_dir=integration-tests/src/test/resources')
  ipr.add_component_from_artifact(:idea_codestyle)
  ipr.extra_modules << '../realityforge_backpack/realityforge_backpack.iml'
end

Buildr.projects.each do |project|
  unless project.name == 'gir'
    project.doc.options.merge!('Xdoclint:all,-reference' => true)
  end
end
