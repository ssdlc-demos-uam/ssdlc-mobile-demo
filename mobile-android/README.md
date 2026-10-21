# Tienda Lab Android (sin Android Studio)

La práctica se puede hacer con herramientas de línea de comandos. No hace falta instalar Android Studio. VS Code es opcional: solo sirve para editar archivos.

## Requisitos de instalación

- **JDK 17**.
- **Gradle 8.9** para compilar el proyecto (usa Android Gradle Plugin 8.7.3).
- **Android SDK Command-line Tools**, **Android SDK Platform 35**, **Build Tools 35.0.0** y **Platform Tools** (`adb`). Descarga las Command-line Tools desde [Android Developers](https://developer.android.com/studio#command-line-tools-only).
- Para emular: **Android Emulator** y una imagen de sistema API 35. Elige la arquitectura que corresponda al ordenador: `arm64-v8a` para Mac Apple Silicon; `x86_64` normalmente para PC Intel/AMD. Si se usa un móvil Android de laboratorio con depuración USB, se puede omitir el emulador y la imagen.
- API: Node.js 20+ y npm 10+; consulta el README de la raíz.
- **OWASP ZAP Desktop** y **MobSF** local. Docker Desktop facilita MobSF; como referencia de aula, reserva 4 CPU y 8 GB de RAM para Docker.

## Preparar Android SDK (una vez)

Descarga y extrae el paquete de Command-line Tools de Google. Coloca su contenido bajo `cmdline-tools/latest` dentro del directorio del SDK. Ejemplo en macOS:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0" "emulator" "cmdline-tools;latest"
```

En Linux, define `ANDROID_HOME` con la ruta del SDK que hayas elegido, por ejemplo `"$HOME/Android/Sdk"`. Conserva estas variables en el perfil de terminal para futuras sesiones.

Para crear un emulador, instala **una** imagen compatible y usa el mismo identificador en `avdmanager`:

```bash
sdkmanager "system-images;android-35;google_apis;arm64-v8a"
avdmanager create avd -n fp-lab -k "system-images;android-35;google_apis;arm64-v8a"
emulator -avd fp-lab
```

En PC Intel/AMD, sustituye `arm64-v8a` por `x86_64` si esa imagen está disponible para tu sistema. Android publica el [uso de sdkmanager](https://developer.android.com/tools/sdkmanager) y [la gestión del emulador por terminal](https://developer.android.com/studio/run/emulator-commandline). En versiones recientes también puede usarse Android CLI para gestionar SDK y AVD.

## Compilar e instalar

Desde una terminal, en `mobile-android`:

```bash
gradle --version       # debe indicar Gradle 8.9 y JVM 17
gradle :app:assembleDebug
adb devices            # comprueba que aparece el emulador o móvil de laboratorio
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Abre la app Tienda Lab desde el lanzador del dispositivo. Si usas un móvil físico, conecta por USB y activa depuración USB. Cambia `API` en `app/src/main/java/test/fp/tiendalab/MainActivity.java` a la IP local del ordenador; el móvil y ordenador deben estar en la red aislada del aula. En el emulador, `10.0.2.2` apunta al `localhost` del ordenador.

## Ejecutar la práctica

1. En la raíz del repositorio, arranca la API: `npm install`, `cp .env.example .env`, `npm run dev`.
2. Inicia el AVD (`emulator -avd fp-lab`) o conecta el móvil de laboratorio. Instala el APK con el comando anterior.
3. En la app, pulsa **Crear cuenta e iniciar sesión**, luego **Ver catálogo** y una compra. Cuenta ficticia: `ana@aula.test` / `Aula-demo-2026!`.
4. Configura el proxy del emulador/móvil para ZAP y captura el inicio de sesión, catálogo y compra. Para la API local se usa HTTP y no hace falta instalar una CA.
5. Sube `app/build/outputs/apk/debug/app-debug.apk` a la instancia local de MobSF para inspeccionar manifiesto, permiso y cadena señuelo.
6. En Ajustes del dispositivo, deniega ubicación y confirma que la tienda sigue funcionando.

Con un móvil físico, permite temporalmente que ZAP reciba conexiones de la red local y configura el proxy hacia la IP del ordenador y el puerto de ZAP. No instales certificados de interceptación en dispositivos personales.

## Hallazgos intencionales

- `ACCESS_FINE_LOCATION` sobra para una tienda.
- `DEMO_API_KEY` es una cadena señuelo, no una credencial.
- Se permite HTTP claro para el laboratorio local.
- La compra confía en `priceCents` enviado por el cliente. El botón de prueba manda 1 céntimo para la taza del catálogo (850 céntimos).

No hay pasarela de pago: cada compra devuelve `SIMULATED`. Usa solo el entorno local, el dispositivo de laboratorio y datos ficticios. No desplegar ni introducir datos reales.
