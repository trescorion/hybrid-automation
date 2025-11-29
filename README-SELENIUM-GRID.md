# Selenium Grid Kurulumu ve Kullanımı

Bu proje Selenium Grid ile paralel test execution'ı desteklemektedir.

## 🚀 Hızlı Başlangıç

### 1. Selenium Grid'i Başlatma

```bash
# Docker Compose ile Grid'i başlat
docker-compose up -d

# Grid durumunu kontrol et
curl http://localhost:4444/wd/hub/status
```

### 2. Grid UI'ı Görüntüleme

Tarayıcınızda şu adresi açın:
```
http://localhost:4444/ui
```

Bu sayfada:
- Aktif node'ları görebilirsiniz
- Çalışan session'ları izleyebilirsiniz
- Grid durumunu kontrol edebilirsiniz

### 3. Testleri Paralel Çalıştırma

```bash
# Tüm testleri paralel çalıştır
./gradlew clean test

# Belirli bir test sınıfını çalıştır
./gradlew test --tests YepyTest

# Test sonuçlarını görüntüle
./gradlew allureServe
```

## 📋 Konfigürasyon

### Grid'i Aktif/Deaktif Etme

`src/test/resources/application.yml` dosyasında:

```yaml
selenium:
  grid:
    enabled: true  # false yaparak local driver kullanabilirsiniz
    hub-url: http://localhost:4444/wd/hub
```

### Paralel Execution Ayarları

Paralel execution ayarları iki yerde yapılabilir:

1. **junit-platform.properties** (Önerilen)
   - `src/test/resources/junit-platform.properties`
   - Tüm testler için geçerli

2. **build.gradle**
   - `maxParallelForks` ayarı
   - Gradle seviyesinde paralel execution

### Test Sınıflarını Paralel Çalıştırma

Test sınıflarınıza `@Execution(ExecutionMode.CONCURRENT)` ekleyin:

```java
@Execution(ExecutionMode.CONCURRENT)
public class YepyTest extends BaseTest {
    // ...
}
```

## 🐳 Docker Compose Servisleri

### Selenium Hub
- Port: `4444` (Grid hub)
- Port: `4442` (Grid router)
- Port: `4443` (Grid sessions)

### Chrome Node
- Maksimum 4 instance
- Maksimum 4 session

### Firefox Node
- Maksimum 4 instance
- Maksimum 4 session

## 🔧 Troubleshooting

### Grid'e Bağlanamıyorum

1. Docker container'ların çalıştığını kontrol edin:
```bash
docker ps
```

2. Hub'ın sağlıklı olduğunu kontrol edin:
```bash
curl http://localhost:4444/wd/hub/status
```

3. Logları kontrol edin:
```bash
docker-compose logs selenium-hub
```

### Testler Paralel Çalışmıyor

1. `junit-platform.properties` dosyasının doğru konumda olduğundan emin olun
2. `application.yml`'de `grid.enabled: true` olduğunu kontrol edin
3. Test sınıflarında `@Execution(ExecutionMode.CONCURRENT)` olduğunu kontrol edin

### Out of Memory Hatası

Docker container'ların `shm_size` değerini artırın veya daha az node instance kullanın.

## 📊 Grid Durumunu İzleme

### Web UI
```
http://localhost:4444/ui
```

### API Endpoint
```bash
# Grid durumu
curl http://localhost:4444/wd/hub/status

# Detaylı durum (JSON)
curl http://localhost:4444/status | jq
```

## 🛑 Grid'i Durdurma

```bash
# Container'ları durdur (veriler korunur)
docker-compose stop

# Container'ları durdur ve sil
docker-compose down

# Container'ları, volume'ları ve network'leri sil
docker-compose down -v
```

## 📝 Notlar

- Grid kullanırken `application.yml`'de `grid.enabled: true` olmalı
- Local driver kullanmak için `grid.enabled: false` yapın
- Grid, farklı browser'ları (Chrome, Firefox) paralel çalıştırabilir
- Her node maksimum 4 session destekler (toplam 8 paralel test)

## 🔗 Faydalı Linkler

- [Selenium Grid Documentation](https://www.selenium.dev/documentation/grid/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

