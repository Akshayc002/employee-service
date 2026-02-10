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
          if (-not $env:FILE_BUNDLE_BASE64) {
              Write-Error "FILE_BUNDLE_BASE64 is empty"
              exit 1
          }
  
          $json = [System.Text.Encoding]::UTF8.GetString(
              [System.Convert]::FromBase64String($env:FILE_BUNDLE_BASE64)
          )
  
          Write-Host "Decoded file bundle:"
          Write-Host $json
  
          $bundle = $json | ConvertFrom-Json
  
          foreach ($file in $bundle.files) {
              $path = $file.path
              $action = $file.action
              $content = $file.content
  
              Write-Host "Applying $action to $path"
  
              $dir = Split-Path $path
              if ($dir -and !(Test-Path $dir)) {
                  New-Item -ItemType Directory -Force -Path $dir | Out-Null
              }
  
              if ($action -eq "create" -or $action -eq "update") {
                  Set-Content -Path $path -Value $content -Encoding UTF8
              }
              elseif ($action -eq "delete") {
                  if (Test-Path $path) {
                      Remove-Item -Force $path
                  }
              }
          }
          '''
      }
  }

  stage('Commit & Push') {
    steps {
        withCredentials([
            usernamePassword(
                credentialsId: 'github-pat',
                usernameVariable: 'GIT_USERNAME',
                passwordVariable: 'GIT_PASSWORD'
            )
        ]) {
            sh '''
            git status
            git add .

            if git diff --cached --quiet; then
              echo "No changes detected after staging. Skipping commit and push."
              exit 0
            fi

            git commit -m "test: apply file bundle via Jenkins"

            git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/Akshayc002/employee-service.git HEAD
            '''
        }
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
