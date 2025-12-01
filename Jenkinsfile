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
    }
    
    post {
        always {
            dir('/workspace/ui-automation') {
                // Raporu her zaman oluştur (fail olsa bile)
                sh './gradlew allureReport'
                
                // Raporu her zaman publish et (fail olsa bile)
                publishHTML([
                    reportDir: 'build/reports/allure-report/allureReport',
                    reportFiles: 'index.html',
                    reportName: 'Allure Test Report',
                    keepAll: true,
                    alwaysLinkToLastBuild: true,
                    allowMissing: false
                ])
                
                // Artifacts'ları arşivle
                archiveArtifacts artifacts: 'build/allure-results/**/*', fingerprint: true, allowEmptyArchive: true
            }
        }
    }
}
