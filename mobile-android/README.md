# Tienda Lab Android

App Android nativa mínima para la práctica local. Solo usa productos y cuentas ficticias; compra siempre devuelve `SIMULATED` y no hay pasarela de pago.

## Requisitos

- Android Studio (incluye gestor de SDK y emulador).
- JDK 17, normalmente el JDK integrado de Android Studio.
- Android SDK Platform 35 y Build Tools instalados desde SDK Manager.
- Un emulador Android (API 26 o superior) o un dispositivo de laboratorio con depuración USB. En un dispositivo físico, cambia `API` en `MainActivity.java` a la IP local del ordenador y asegúrate de que ambos estén en una red de laboratorio.
- API: Node.js 20+, npm 10+; consulta `../README.md`.

Para completar el análisis dinámico/estático, instala además **OWASP ZAP Desktop** y **MobSF**. MobSF puede ejecutarse en Docker Desktop; prepara Docker con recursos suficientes para una VM/contenedor (recomendación de aula: 4 CPU y 8 GB de RAM disponibles para Docker, más espacio para imágenes y SDK). Como alternativa, el docente puede instalar MobSF con su método local y alojarlo en `127.0.0.1`; no publiques el servicio en Internet. Wireshark solo es necesario para la extensión. Frida queda fuera de la instalación básica.

## Compilar y ejecutar

1. Desde la raíz `mobile-android`, abre el proyecto con Android Studio y espera a que Gradle sincronice.
2. En el proyecto de API, inicia el backend en el ordenador: `npm install`, `cp .env.example .env`, `npm run dev`.
3. En Android Studio, inicia el AVD y pulsa **Run**. El host `10.0.2.2` del código del emulador apunta al `localhost` del ordenador.
4. Pulsa **Crear cuenta e iniciar sesión**, luego **Ver catálogo** y una compra. La cuenta de laboratorio es `ana@aula.test` / `Aula-demo-2026!`.
5. Para MobSF: **Build → Build APK(s)**. APK resultante: `app/build/outputs/apk/debug/app-debug.apk`.

ZAP y MobSF deben correr en el mismo ordenador que el emulador o estar accesibles en la red de laboratorio. Configura el proxy en Android Emulator apuntando a la IP del ordenador y al puerto que indique ZAP; el detalle de certificado TLS depende de la versión/imagen Android. Este laboratorio usa HTTP local para que la captura inicial no dependa de instalar una CA. Para observar tráfico HTTPS, el docente debe preparar un backend HTTPS local y explicar la confianza de CA en un AVD desechable.

Si vuelves a iniciar el backend, su almacén en memoria se reinicia. La API de desarrollo exige un `JWT_SECRET` de 32 caracteres en `.env`; usa el valor de ejemplo únicamente en local.

## Hallazgos intencionales

- `ACCESS_FINE_LOCATION` sobra para una tienda (manifiesto).
- `DEMO_API_KEY` es una cadena señuelo en el código, no una credencial real.
- Se permite HTTP claro para el emulador y el manifiesto declara `usesCleartextTraffic`.
- La compra confía en `priceCents` enviado por el cliente. El botón de prueba envía un céntimo para la taza cuyo catálogo indica 850 céntimos.

Estos fallos son parte de la práctica. La app y API solo deben usarse localmente en el laboratorio; no las despleguéis ni introduzcáis datos reales.
