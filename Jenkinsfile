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

        stage('License') {
            _gradle 'generateLicenseReport checkLicense'
        }

        stage('Build') {
            _gradle 'clean assemble'
        }

        stage('Test') {
            try {
                _gradle 'test'
            } finally {
                junit '**/test-results/test/*.xml'
            }
        }

        stage('Test coverage') {
            jacoco()
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


