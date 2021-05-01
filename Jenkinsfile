#!groovy
node() {
    env.JAVA_HOME = "${tool 'zulu-11.0.8'}"
    try {
        stage('Clean workspace') {
            cleanWs()
        }

        stage('Checkout') {
            checkout(scm)
        }

        stage('Assemble') {
            _gradle 'assemble'
        }

        stage('Test') {
            try {
                _gradle 'test -x :test'
            } finally {
                junit '**/test-results/test/*.xml'
            }
        }

        // see https://www.jenkins.io/doc/pipeline/steps/jacoco/
        stage('Test coverage') {
            jacoco(
                    classPattern: '**/build/classes/java/main'
            )
        }

        stage('Publish Artifacts') {
            archiveArtifacts artifacts: 'copper2go-api/build/libs/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: 'build/distributions/**/*.zip', fingerprint: true
        }

        if (env.BRANCH_NAME == 'master') {
            stage('Publish Image') {
                _gradle jib
            }
            stage('Image System Test') {
                _gradle ':test'
            }
        }

        currentBuild.result = 'SUCCESS'
    } catch (Exception exception) {
        currentBuild.result = 'FAILURE'
        throw exception
    }
}

def _gradle(String task) {
    sh "./gradlew ${task}"
}


