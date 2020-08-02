#!groovy
node() {
    env.JAVA_HOME = "${tool 'zulu-11.0.8'}"
    stage('Clean workspace') {
            cleanWs()
        }

        stage('Checkout') {
            checkout(scm)
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
}

def _gradle(String task) {
    sh "./gradlew ${task}"
}


