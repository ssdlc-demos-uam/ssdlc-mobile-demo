# Laboratorio SSDLC: seguridad móvil

App Android y API local de tienda ficticia para FP. Practica permisos, análisis estático de APK con MobSF e inspección de tráfico y compras simuladas con OWASP ZAP.

## Requisitos

- Node.js 20+ y npm 10+.
- JDK 17, Gradle 8.9 y Android SDK Command-line Tools (Platform 35). No requiere Android Studio. Puedes usar el emulador CLI o un móvil Android de laboratorio.
- OWASP ZAP Desktop y MobSF local (Docker Desktop recomendado; 4 CPU y 8 GB RAM disponibles).

## Arranque rápido

```bash
npm install
cp .env.example .env
npm run dev
```

Sigue [las instrucciones sin Android Studio](mobile-android/README.md): compila con Gradle, instala con `adb` y ejecuta en el emulador CLI o en un móvil de laboratorio. En el emulador, la app usa `10.0.2.2:3000`. Credenciales ficticias: `ana@aula.test` / `Aula-demo-2026!`.

El APK se genera en `mobile-android/app/build/outputs/apk/debug/app-debug.apk`; analízalo en MobSF. Consulta [la práctica y rúbrica](docs/PRACTICA_SEGURIDAD_MOVIL.md).

La compra es simulada; no hay pasarela de pago. La app y API contienen fallos intencionales para el laboratorio. No desplegar ni introducir datos reales.
