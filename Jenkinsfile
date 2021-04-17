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

        stage('Build') {
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

        stage('License') {
            _gradle 'generateLicenseReport checkLicense'
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


