pipeline {
  agent any

  options {
    skipDefaultCheckout()
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
        branches: [[name: '*/main']],
        userRemoteConfigs: [[
          url: env.REPO_URL,
          credentialsId: env.GIT_CREDENTIALS_ID
        ]]
      ])
      sh """
        git checkout -B ${params.BRANCH}
      """
    }
  }

    stage('Apply File Bundle') {
  steps {
    powershell '''
      $base64 = @"
${env.FILE_BUNDLE_BASE64}
"@

      $bytes = [System.Convert]::FromBase64String($base64)
      $json = [System.Text.Encoding]::UTF8.GetString($bytes)

      $bundle = $json | ConvertFrom-Json

      foreach ($file in $bundle.files) {
        $path = $file.path
        $action = $file.action
        $content = $file.content

        $dir = Split-Path $path
        if ($dir -and !(Test-Path $dir)) {
          New-Item -ItemType Directory -Path $dir -Force | Out-Null
        }

        if ($action -eq "delete") {
          if (Test-Path $path) {
            Remove-Item $path -Force
          }
        } else {
          Set-Content -Path $path -Value $content -Encoding UTF8
        }
      }
    '''
  }
}

    stage('Commit & Push') {
      steps {
        sh '''
          git status
    
          if git diff --cached --quiet && git diff --quiet; then
            echo "No changes detected. Skipping commit and push."
            exit 0
          fi
    
          git add .
          git commit -m "${COMMIT_MESSAGE}"
          git push origin ${BRANCH}
        '''
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
