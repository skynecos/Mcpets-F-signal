# MCPets F Signal

Kirazium için hazırlanmış küçük bir Paper addonudur. MCPets petlerinin `Signal Stick`
ile kullanılan tek manuel yeteneğini, oyuncunun el değiştirme tuşuna (`F`) bağlar.

## İndir

- [KiraziumPetKeys-1.0.0.jar](https://github.com/skynecos/Mcpets-F-signal/releases/download/v1.0.0/KiraziumPetKeys-1.0.0.jar)
- [Kaynak Kodu ZIP (main)](https://github.com/skynecos/Mcpets-F-signal/archive/refs/heads/main.zip)
- [v1.0.0 Release sayfası](https://github.com/skynecos/Mcpets-F-signal/releases/tag/v1.0.0)

## Davranış

- Aktif ve yüklü bir petin `Signals.Values` listesinde sinyal varsa ilk sinyal gönderilir.
- Sinyal başarıyla MCPets'e iletildiğinde el değiştirme işlemi iptal edilir.
- Pet yoksa, pet yüklü değilse veya sinyal tanımlı değilse `F` normal çalışır.
- Bir companion ve bir mount aynı anda aktifse, oyuncu binerken mount; yaya iken
  companion yeteneği öncelikli çalışır.
- Kullanılan MCPets sürümünde mount bilgisi okunamazsa ilk uyumlu aktif pet güvenli
  geri dönüş olarak seçilir.
- MythicMobs tarafındaki mevcut koşullar ve cooldownlar değiştirilmez.
- İstemci modu veya kaynak paketi gerektirmez.

## Gereksinimler

- Java 17 veya üzeri
- Paper 1.20.6 veya üzeri
- MCPets 4.x
- MCPets petlerinin ihtiyaç duyduğu MythicMobs/ModelEngine kurulumu

## Kurulum

1. `KiraziumPetKeys-1.0.0.jar` dosyasını sunucunun `plugins` klasörüne koyun.
2. Sinyal çubuğunu menüden kaldırmak istediğiniz petlerde aşağıdaki değeri kullanın:

```yaml
Signals:
  Values:
    - DASH
  Item:
    GetFromMenu: false
```

3. Sunucuyu tamamen yeniden başlatın.

## Derleme

```bash
mvn clean package
```

Oluşan JAR: `target/KiraziumPetKeys-1.0.0.jar`

Eklenti MCPets sınıflarını kendi JAR'ına gömmez. MCPets'in güncel çoklu-pet API'sini,
gerekirse eski tek-pet API'sine geri düşerek çalışma anında bağlar.

## Lisans

Bu proje [MIT Lisansı](LICENSE) ile açık kaynak olarak yayımlanmaktadır.
