pipeline {
    agent any

    options {
        timestamps()              // Show timestamps in logs
        disableConcurrentBuilds() // Avoid parallel builds on same job
    }

    environment {
        APP_NAME = "employee-service"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out source code"
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Building application"
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                echo "Running unit tests"
                sh 'mvn test'
            }
        }
    }

    post {
        success {
            echo "✅ PIPELINE SUCCESS: ${APP_NAME}"
        }

        failure {
            echo "❌ PIPELINE FAILED: ${APP_NAME}"
        }

        always {
            echo "📌 Pipeline finished for branch: ${env.BRANCH_NAME}"
        }
    }
}
