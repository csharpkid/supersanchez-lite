# SuperSanchezLite

SuperSanchezLite es una aplicación Android ligera que utiliza Jetpack Compose y un WebView para mostrar el contenido del sitio web [supersanchez.com](https://supersanchez.com). 

La aplicación está diseñada para proporcionar una experiencia de usuario fluida con un indicador de carga inicial, soporte para "pull to refresh", manejo de errores de red y navegación en el historial del WebView con el botón "Back".

## Características

- **Carga inicial optimizada**: Muestra un indicador de carga con el logo de la aplicación y un `CircularProgressIndicator` durante la primera carga, que desaparece cuando el contenido esencial (HTML básico con skeletons) está visible.
- **Pull to refresh**: Permite recargar la página con un gesto de deslizamiento hacia abajo, con un indicador de refresco nativo que se oculta rápidamente para una experiencia no intrusiva.
- **Manejo de errores**: Detecta problemas de conexión a internet o errores de carga y muestra un mensaje con un botón "Retry" para intentar de nuevo.
- **Navegación con botón "Back"**: El botón "Back" del dispositivo navega en el historial del WebView en lugar de cerrar la aplicación, si hay páginas previas disponibles.
- **Caché inteligente**: Utiliza `SharedPreferences` para recargar la página desde la red cada 24 horas, optimizando el uso de datos.
- **Soporte para contenido dinámico**: Configura el WebView con JavaScript, almacenamiento DOM y modo de contenido mixto habilitados para soportar sitios web modernos.

## Requisitos previos

- **Android Studio**: Versión reciente (recomendado: Android Studio Iguana o superior).
- **SDK de Android**: API 21 (Lollipop) o superior.
- **Kotlin**: Versión compatible con Jetpack Compose (1.8.0 o superior).
- **Dependencias**:
  - Jetpack Compose
  - Material 3
  - AndroidX Activity
  - SwipeRefreshLayout

Asegúrate de tener configurado un emulador o dispositivo físico con conexión a internet para probar la aplicación.

Creado por Cristian Villegas
## info@pudu.mx
