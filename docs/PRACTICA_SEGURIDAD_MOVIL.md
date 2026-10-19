# Práctica: Misión móvil segura

## Ficha

- **Nivel:** FP (DAM/DAW/ASIR), trabajo en parejas.
- **Duración:** 3 horas.
- **Plataformas:** Android como recorrido principal; iOS como comparación guiada.
- **Reto:** revisar una app ficticia de tienda, detectar fallos de permisos, exposición de secretos y uso inseguro de API, y proponer una corrección comprobable.
- **Entorno:** emulador/dispositivo de laboratorio y API local o sandbox preparada por el docente. Solo se inspeccionan apps y servicios propios del laboratorio.

La sesión no requiere experiencia previa con instrumentación dinámica. Se puede realizar íntegramente sobre Android. Para iOS se incluye una actividad de comparación; ejecutar análisis dinámico en iOS requiere macOS, Xcode y simulador/dispositivo configurado por el centro.

## Objetivos de aprendizaje

Al terminar, el equipo podrá:

1. justificar permisos móviles según la función y el principio de mínimo privilegio;
2. relacionar hallazgos con categorías de OWASP MASVS;
3. observar una petición de API y comprobar autenticación, validación y transporte;
4. inspeccionar un APK en busca de secretos o datos sensibles incluidos por error;
5. redactar un hallazgo con evidencia, impacto y mitigación.

## Herramientas elegidas

| Herramienta | Uso en esta práctica | Preparación |
|---|---|---|
| **MobSF** | Análisis estático del APK: permisos, configuración, cadenas y posibles secretos | Docente deja una instancia local lista y un APK de laboratorio |
| **OWASP ZAP** | Proxy para ver las llamadas de la app a la API y revisar cabeceras/respuestas | ZAP instalado; proxy configurado en el emulador |
| **Android Studio Emulator** | Ejecutar la app y conceder/denegar permisos | AVD creado y app instalada |
| **MASVS/MASWE** | Marco para clasificar y comunicar resultados | Hoja de referencia incluida abajo |

**Extensiones opcionales:** Wireshark para observar metadatos de red en una red aislada; Frida para instrumentación dinámica de una app propia de laboratorio; Burp Suite Community como alternativa a ZAP. No se necesitan para completar el reto. En documentos se escribe **Burp Suite** (no “Burb Suite”).

## Preparación del docente

Antes de la clase, usar el proyecto incluido en [`mobile-android`](../mobile-android/README.md): ya contiene una tienda ficticia, catálogo, compra simulada, permisos y cadena señuelo para MobSF. Debe incluir deliberadamente problemas inocuos, por ejemplo:

- permiso de ubicación declarado aunque la app no lo necesita;
- clave ficticia `DEMO_API_KEY` o URL de entorno de pruebas dentro del APK;
- token de sesión guardado en preferencias sin protección;
- endpoint de compra de sandbox que confía en el precio enviado por el cliente;
- errores de API demasiado detallados.

Usar datos y credenciales ficticios, productos sin valor y una API de sandbox. No conectar servicios de pago reales. Entregar el APK y una ficha de arquitectura simplificada (qué datos transmite y a qué host). La app puede ser una plantilla Android ya compilada: programar una aplicación desde cero no es requisito.

El propio alumnado puede compilar el APK desde Android Studio según el README de la app. Si no hay tiempo de compilación, el docente puede distribuir el APK generado por ese proyecto.

## Guion del alumnado

### Misión 0 — Reglas del laboratorio (10 min)

Trabajad solo con la app, cuentas, APK y API entregados por el docente. No probéis dominios públicos, redes de terceros ni pagos reales. No compartáis APK con datos personales.

En equipo, anotad: nombre de la app, versión, dispositivo/emulador, host de API autorizado y datos ficticios usados.

### Misión 1 — ¿De verdad necesita ese permiso? (25 min)

1. Abrid el panel de permisos en MobSF y anotad los permisos declarados.
2. En el emulador, abrid Ajustes → Aplicaciones → app del laboratorio → Permisos. Denegad ubicación, contactos y cámara si aparecen.
3. Probad catálogo y compra simulada. ¿Qué función deja de trabajar? ¿Tiene sentido pedir cada permiso?
4. Proponed el permiso mínimo y cuándo debe solicitarse. Un permiso sensible debe pedirse en contexto, con explicación, y la app debe degradarse correctamente si se deniega.

**Evidencia:** tabla `permiso | función que lo justifica | decisión (mantener/quitar/solo al usar) | prueba`.

### Misión 2 — ¿Qué revela el paquete? (30 min)

1. Subid el APK únicamente a la instancia local de MobSF indicada por el docente.
2. Revisad permisos, manifiesto, configuración de depuración, tráfico claro, dominios y cadenas con aspecto de clave/token.
3. Buscad la cadena ficticia `DEMO_API_KEY` y comprobad si parece un secreto real o un marcador de laboratorio. No copiéis valores sensibles en la entrega.
4. Elegid un hallazgo y explicad cómo lo confirmaríais sin reutilizar ni publicar la credencial.

Recordad: ofuscar una app no convierte una clave distribuida en secreto. Los secretos de servidor deben quedarse en el servidor; las claves públicas de cliente han de restringirse y no dar autoridad por sí solas.

**Evidencia:** captura recortada o nombre de la sección de MobSF, identificador de hallazgo, riesgo y corrección propuesta.

### Misión 3 — La compra sospechosa (40 min)

1. Configurad el proxy de ZAP según la ficha del docente y activad la captura. Usad solo el certificado y configuración de laboratorio.
2. Iniciad sesión con una cuenta ficticia y completad una compra simulada.
3. En el historial de ZAP, localizad la petición de creación/confirmación de compra. Anotad método, ruta, campos relevantes y código de respuesta. Ocultad tokens y datos personales en capturas.
4. Si la ficha autoriza pruebas de manipulación, repetid una compra de sandbox cambiando **solo** el precio enviado por el cliente. Comprobad si el servidor calcula el total desde un catálogo propio o acepta el precio del móvil. No ejecutéis cargos reales.
5. Revisad si la API usa HTTPS fuera del emulador local, si el token viaja en cabecera y si respuestas/errores filtran información.

**Diseño seguro esperado:** el servidor valida identidad y propiedad, calcula precio y descuentos con datos propios, verifica estado e idempotencia de la compra, valida recibos/tokens con la tienda correspondiente y no confía en importes ni resultados de pago reportados por el cliente. No se implementa una integración real de Apple/Google en esta práctica.

### Misión 4 — Clasificar con MASVS y proponer arreglo (25 min)

Relacionad cada hallazgo con el grupo MASVS más cercano. Las etiquetas orientativas de MASVS 2.x son:

- **MASVS-STORAGE:** almacenamiento local y protección de datos.
- **MASVS-NETWORK:** comunicaciones de red.
- **MASVS-PLATFORM:** permisos e interacción con el sistema operativo.
- **MASVS-CODE:** calidad del código y configuración de compilación.
- **MASVS-RESILIENCE:** resistencia a manipulación y análisis; no sustituye los controles de seguridad.
- **MASVS-PRIVACY:** tratamiento y minimización de datos personales.

La autorización de compras en backend también se puede relacionar con **MASVS-NETWORK** y requisitos de autenticación/autorización del API. Indicad además una referencia MASWE/hallazgo cuando la ficha o el informe de herramienta la proporcione. MASVS es un estándar de verificación, no una puntuación automática: un semáforo de MobSF no demuestra por sí solo que la app sea segura.

### Misión 5 — Informe relámpago (20 min)

Preparad una mini-presentación de 2 minutos con un hallazgo prioritario. Debe incluir:

- título y componente afectado;
- evidencia reproducible (sin secretos);
- impacto para una persona usuaria;
- categoría MASVS/MASWE orientativa;
- corrección y una prueba para comprobarla.

## Plantilla de hallazgos

| ID | Hallazgo y evidencia | Impacto | MASVS/MASWE | Corrección | Prueba de verificación |
|---|---|---|---|---|---|
| H-01 |  |  |  |  |  |
| H-02 |  |  |  |  |  |

## Entrega

Un PDF o Markdown por equipo con:

1. tabla de permisos;
2. dos hallazgos como mínimo (uno del APK y uno de API/red, o dos de APK si no se dispone de API);
3. clasificación MASVS/MASWE, impacto, corrección y prueba;
4. una captura censurada de MobSF y otra de ZAP, si se usaron.

No adjuntéis tokens, claves, certificados privados, datos personales ni capturas sin censurar.

## Rúbrica (100 puntos)

| Criterio | Puntos |
|---|---:|
| Permisos: mínimo privilegio y comportamiento al denegar | 20 |
| Análisis del APK y explicación de exposición | 20 |
| API/compra: identifica qué debe validar el servidor | 25 |
| Clasificación MASVS y mitigaciones concretas | 20 |
| Evidencias claras, reproducibles y censuradas | 15 |

## Recorrido alternativo para iOS (20–30 min)

Con una build iOS de laboratorio disponible, repetid la revisión del flujo y comparad el resultado estático de Info.plist: `NS*UsageDescription`, capacidades y dominios. Revisad el comportamiento con permisos denegados en el simulador. Para proxy, configurad el simulador/dispositivo de laboratorio y el certificado de ZAP/Burp bajo instrucciones del centro. No intentéis eludir protecciones de una app ajena. Sin macOS/Xcode, completad esta parte leyendo el Info.plist exportado por el docente y comparándolo con el Android Manifest.

## Ampliaciones opcionales

- **Wireshark (15 min):** capturar tráfico en una red aislada y reconocer DNS, IP, puertos y metadatos. No descifrar tráfico de terceros ni buscar credenciales ajenas.
- **Frida (20–30 min):** observar una función de la app propia de laboratorio para entender instrumentación y por qué la lógica crítica no debe residir solo en el cliente. No usar scripts de bypass contra aplicaciones comerciales.
- **Burp Suite Community:** sustituye a ZAP si ya está instalado y el docente conoce su configuración. La actividad es la misma.

## Notas de seguridad y accesibilidad

- La interceptación TLS puede requerir instalar una CA de laboratorio y configuración específica de Android/iOS. Preparad una imagen de emulador; no cambiéis la confianza del dispositivo personal del alumnado.
- En producción, una app puede aplicar certificate pinning, lo que impide el proxy. No se pide desactivarlo; el docente debe facilitar una build didáctica instrumentable o una captura/API preparada.
- MobSF puede dar falsos positivos y no ve todos los flujos. Contrastad hallazgos manualmente.
- Para reducir barreras de instalación, el profesor puede proporcionar capturas de MobSF/ZAP y una ficha de evidencias si el aula no tiene hardware o permisos de instalación.
