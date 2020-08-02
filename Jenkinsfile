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
            gradle 'clean assemble'
        }

        stage('Test') {
            try {
                gradle 'test'
            } finally {
                junit '**/test-results/test/*.xml'
            }
        }
}

def _git(String command) {
    sh "git ${command}"
}



