#!groovy
@Library('Reform')
import uk.gov.hmcts.Packager
import uk.gov.hmcts.Versioner

//noinspection GroovyAssignabilityCheck Jenkins API requires this format
properties(
  [[$class: 'GithubProjectProperty', projectUrlStr: 'https://github.com/hmcts/draft-store/'],
   pipelineTriggers([[$class: 'GitHubPushTrigger']])]
)

Packager packager = new Packager(this, 'reform')
Versioner versioner = new Versioner(this)

def channel = '#cmc-tech-notification'

node {
  try {
    stage('Checkout') {
      deleteDir()
      checkout scm
    }

    stage('Build') {
      sh "./gradlew clean build -x test"
    }

    stage('OWASP dependency check') {
      try {
        sh "./gradlew -DdependencyCheck.failBuild=true dependencyCheck"
      } catch (ignored) {
        archiveArtifacts 'build/reports/dependency-check-report.html'
        notifyBuildResult channel: '#development', color: 'warning',
          message: 'OWASP dependency check failed for draft-store see the report for the errors'
      }
    }

    stage('Test (unit)') {
      sh "./gradlew test"
    }

    stage('Test (integration)') {
      sh "./gradlew integrationTest"
    }

    stage('Package (JAR)') {
      versioner.addJavaVersionInfo()
      sh "./gradlew bootRepackage installDist"
    }

    stage('Package (RPM)') {
      packager.javaRPM('draft-store', 'build/libs/draft-store-$(./gradlew -q printVersion)-all.jar',
        'springboot', 'src/main/resources/application.yaml')

      onMaster {
        packager.publishJavaRPM('draft-store')
      }
    }

    stage('Package (Docker)') {
      dockerImage imageName: 'reform/draft-store-api'
      dockerImage imageName: 'reform/draft-store-database', context: 'docker/database'
    }
  } catch (err) {
    archiveArtifacts 'build/reports/**/*.html'
    notifyBuildFailure channel: channel
    throw err
  } finally {
        step([$class: 'InfluxDbPublisher',
               customProjectName: 'Draftstore',
               target: 'Jenkins Data'])
    }
  notifyBuildFixed channel: channel
}
