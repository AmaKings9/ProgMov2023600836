## Aplicación con menú desplegable
Cada item del menú cuenta con su propia pantalla independiente, por lo que esta carpeta contiene en la carpeta "Screens" los archivos correspondientes al resto de las vistas.

## Estructura del proyecto
MainActivity.kt: Utiliza navController para registrar el desplacamiento entre las diferentes pantallas durante la sesión activa y permite redirigir al resto de vistas

Screens/
- MainScreen.kt: Contiene elementos de UI y funcionalidad de la pantalla principal donde se encuentra el menú desplegable.
- SumaScreen.kt: Contiene elementos de UI y proceso para la pantalla de suma de 3 números y el despliegue del resultado de dicha operación.
- NombreScreen.kt: Contiene elementos de UI y proceso para ingresar apellidos y nombre, para después mostrar en pantalla el nombre completo.
- FecNacScreen.kt: Contiene elementos de UI y proceso para seleccionar la fecha de nacimiento y calcular semanas, días, meses, etc. transcurridos hasta la fecha de consulta.
