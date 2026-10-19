# Laboratorio SSDLC: seguridad móvil

Proyecto local para FP: app Android y API de una tienda ficticia con compras simuladas. Permite practicar permisos, análisis estático del APK, inspección de tráfico y validación de compras con MobSF y OWASP ZAP.

## Requisitos

- Node.js 20 o superior y npm 10 o superior.
- Android Studio, JDK 17, Android SDK Platform 35 y un AVD con Android 8 (API 26) o superior.
- OWASP ZAP Desktop para interceptar las peticiones.
- MobSF local para analizar el APK; Docker Desktop es una opción sencilla (recomendación para Docker: 4 CPU y 8 GB RAM disponibles).
- Android Studio y el backend deben ejecutarse en el mismo ordenador para usar `10.0.2.2` desde el emulador.

## Inicio rápido

Terminal 1, desde la raíz:

```bash
npm install
cp .env.example .env
npm run dev
```

Terminal 2: abre `mobile-android` con Android Studio, sincroniza Gradle, inicia un AVD y pulsa **Run**. En la app, crea/inicia sesión con `ana@aula.test` / `Aula-demo-2026!`, consulta el catálogo y prueba la compra de un céntimo.

Para generar el APK: **Build → Build APK(s)**. Se guarda en `mobile-android/app/build/outputs/apk/debug/app-debug.apk`.

La guía completa de actividades, permisos, instalación y rúbrica está en [docs/PRACTICA_SEGURIDAD_MOVIL.md](docs/PRACTICA_SEGURIDAD_MOVIL.md). La guía de compilación y configuración de red está en [mobile-android/README.md](mobile-android/README.md).

## Fallos didácticos intencionales

- Permiso de ubicación no necesario y cadena señuelo detectable en el APK.
- HTTP claro en el emulador local para poder observar el tráfico.
- Endpoint de compra que confía en el precio enviado por el cliente (la taza vale 850 céntimos; la app permite enviar 1).

La API no procesa pagos y solo devuelve `SIMULATED`. Usa únicamente el entorno local, los datos ficticios incluidos y herramientas contra este laboratorio. No desplegar en Internet ni introducir datos reales.
