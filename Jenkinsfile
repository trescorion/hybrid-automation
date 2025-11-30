pipeline {
    agent any
    
    environment {
        SELENIUM_GRID_HUB_URL = 'http://selenium-hub:4444/wd/hub'
    }
    
    stages {
        stage('Run Tests') {
            steps {
                dir('/workspace/ui-automation') {
                    sh './gradlew clean test'
                }
            }
        }
        
        stage('Allure Report') {
            steps {
                dir('/workspace/ui-automation') {
                    sh './gradlew allureReport'
                    publishHTML([
                        reportDir: 'build/reports/allure-report/allureReport',
                        reportFiles: 'index.html',
                        reportName: 'Allure Test Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: false
                    ])
                }
            }
        }
    }
    
    post {
        always {
            dir('/workspace/ui-automation') {
                archiveArtifacts artifacts: 'build/allure-results/**/*', fingerprint: true, allowEmptyArchive: true
            }
        }
    }
}
