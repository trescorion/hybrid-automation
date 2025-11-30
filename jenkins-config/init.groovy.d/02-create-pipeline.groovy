import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

def instance = Jenkins.getInstance()

def jobName = 'ui-automation-tests'

def pipelineScript = '''
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
'''

try {
    def existingJob = instance.getItem(jobName)
    
    if (existingJob == null) {
        // Job yoksa oluştur
        def job = new WorkflowJob(instance, jobName)
        job.setDisplayName('UI Automation Tests')
        job.setDescription('Automated test pipeline for UI automation')
        
        def flowDefinition = new CpsFlowDefinition(pipelineScript, true)
        job.setDefinition(flowDefinition)
        
        instance.reload()
        println "✓ Pipeline job '${jobName}' created successfully"
    } else {
        // Job varsa güncelle
        def flowDefinition = new CpsFlowDefinition(pipelineScript, true)
        existingJob.setDefinition(flowDefinition)
        existingJob.save()
        instance.reload()
        println "✓ Pipeline job '${jobName}' updated successfully"
    }
} catch (Exception e) {
    // Hata durumunda job'ı sil ve yeniden oluştur
    println "⚠ Error: ${e.message}, attempting to recreate job..."
    try {
        def existingJob = instance.getItem(jobName)
        if (existingJob != null) {
            existingJob.delete()
        }
    } catch (Exception deleteEx) {
        println "⚠ Could not delete existing job: ${deleteEx.message}"
    }
    
    // Yeniden oluştur
    def job = new WorkflowJob(instance, jobName)
    job.setDisplayName('UI Automation Tests')
    job.setDescription('Automated test pipeline for UI automation')
    def flowDefinition = new CpsFlowDefinition(pipelineScript, true)
    job.setDefinition(flowDefinition)
    
    instance.reload()
    println "✓ Pipeline job '${jobName}' recreated successfully after error"
}

instance.save()
