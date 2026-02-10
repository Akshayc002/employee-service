pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  parameters {
    string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to create or update')
    string(name: 'COMMIT_MESSAGE', defaultValue: '', description: 'Commit message for this deployment')
    text(name: 'FILE_BUNDLE_BASE64', defaultValue: '', description: 'Base64-encoded JSON file bundle')
  }

  environment {
    REPO_URL = 'https://github.com/Akshayc002/employee-service.git'
    GIT_CREDENTIALS_ID = 'github-pat'   // Jenkins credential ID
  }

  stages {

    stage('Validate Input') {
      steps {
        script {
          if (!params.COMMIT_MESSAGE?.trim()) {
            error 'COMMIT_MESSAGE is required'
          }
          if (!params.FILE_BUNDLE_BASE64?.trim()) {
            error 'FILE_BUNDLE_BASE64 is required'
          }
        }
      }
    }

    stage('Checkout') {
      steps {
        checkout([
          $class: 'GitSCM',
          branches: [[name: "*/${params.BRANCH}"]],
          userRemoteConfigs: [[
            url: env.REPO_URL,
            credentialsId: env.GIT_CREDENTIALS_ID
          ]]
        ])
      }
    }

    stage('Apply File Bundle') {
      steps {
        script {
          def jsonFile = 'file_bundle.json'
          writeFile file: jsonFile, text: new String(params.FILE_BUNDLE_BASE64.decodeBase64(), 'UTF-8')

          def bundle = readJSON file: jsonFile

          bundle.files.each { f ->
            if (f.action == 'delete') {
              sh "rm -f '${f.path}'"
            } else {
              sh """
                mkdir -p "\$(dirname '${f.path}')"
                cat > '${f.path}' << 'EOF'
${f.content}
EOF
              """
            }
          }
        }
      }
    }

    stage('Commit & Push') {
      steps {
        sh """
          git status
          git add .
          git commit -m "${params.COMMIT_MESSAGE}"
          git push origin ${params.BRANCH}
        """
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn clean test'
      }
    }
  }

  post {
    success {
      echo 'Deployment and CI completed successfully.'
    }
    failure {
      echo 'Deployment failed. Check logs for details.'
    }
  }
}
