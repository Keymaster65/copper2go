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

        stage('Unit Test') {
            try {
                _gradle 'test'
            } finally {
                junit '**/test-results/test/*.xml'
            }
        }

        try {
            stage('Integration Test') {
                _gradle 'integrationTest'
            }
        } finally {
            junit '**/test-results/test/*.xml'
        }

        try {
            stage('System Test') {
                _gradle 'systemTest'
            }
        } finally {
            junit '**/test-results/test/*.xml'
        }

        stage('Publish Artifacts') {
            archiveArtifacts artifacts: 'copper2go-api/build/libs/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: 'build/distributions/**/*.zip', fingerprint: true
        }

        // see https://www.jenkins.io/doc/pipeline/steps/jacoco/
        stage('Test Coverage') {
            jacoco(
                    classPattern: '**/build/classes/java/main'
            )
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


