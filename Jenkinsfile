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
                _gradle 'test'
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

        stage('Publish') {
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: 'build/distributions/**/*.zip', fingerprint: true
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


