import jenkins.model.*
import hudson.model.*

// Allure HTML raporlarının çalışması için CSP'yi gevşet
// Bu, HTML Publisher plugin'inin JavaScript'lerinin çalışması için gerekli

def instance = Jenkins.getInstance()

// System property ile CSP'yi devre dışı bırak
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")

// Veya daha güvenli bir CSP policy
// System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "default-src 'self' 'unsafe-inline' 'unsafe-eval'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline';")

println "✓ Content Security Policy (CSP) disabled for HTML reports"

instance.save()
