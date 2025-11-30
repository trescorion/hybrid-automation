# UI Automation

Selenium Grid ve Jenkins ile paralel UI test otomasyonu projesi.

## Gereksinimler

- Docker & Docker Compose
- Java 17+
- Git

## Kurulum

# Projeyi klonla
git clone <repo-url>
cd ui-automation

# Docker servislerini başlat (Selenium Grid + Jenkins)
docker-compose up -d

# Jenkins hazır olana kadar bekle (~30 saniye)
# Jenkins: http://localhost:8080 (admin/admin123)
# Selenium Grid: http://localhost:4444## Test Çalıştırma

### Lokal (Grid olmadan)
./gradlew clean test
./gradlew allureReport
./gradlew allureServe  # Raporu görüntüle### Grid ilesh
# application.yml'de selenium.grid.enabled: true olmalı
./gradlew clean test### Jenkins ile
1. `http://localhost:8080` → `ui-automation-tests` job'ını çalıştır
2. Build sonrası "Allure Test Report" linkinden raporu görüntüle

## Paralel Execution

- **JUnit**: `junit-platform.properties` dosyasında yapılandırılmış
- **Gradle**: `build.gradle` içinde `maxParallelForks` ayarlanabilir
- **Selenium Grid**: `docker-compose.yml` içinde `SE_NODE_MAX_SESSIONS` ile kontrol edilir

## Raporlar

- **Allure**: `build/reports/allure-report/allureReport/index.html`
- Jenkins'te otomatik olarak publish edilir
