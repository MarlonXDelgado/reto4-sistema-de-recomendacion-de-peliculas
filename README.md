# Sistema de Recomendación de Películas

## Descripción del proyecto

Este proyecto consiste en el desarrollo de un sistema de recomendación de películas implementado en Java.  
El sistema permite a los usuarios explorar un catálogo de películas, consultar información por género, registrar las películas que han visto, calificarlas y recibir recomendaciones basadas en ciertos criterios.

El objetivo principal es aplicar conceptos fundamentales de programación en Java como estructuras de datos, programación orientada a objetos, manejo de excepciones, uso de Streams y procesamiento concurrente.

El sistema funciona completamente en consola (CLI) y simula el funcionamiento básico de una plataforma de recomendación de contenido.

---

## Objetivos

### Objetivo general

Desarrollar un sistema de recomendación de películas que permita gestionar usuarios, consultar información del catálogo y generar recomendaciones basadas en filtros específicos.

### Objetivos específicos

- Implementar un catálogo de películas organizado por género.
- Permitir la creación y selección de perfiles de usuario.
- Registrar películas vistas por cada usuario.
- Permitir que los usuarios califiquen películas.
- Generar recomendaciones personalizadas evitando películas ya vistas.
- Calcular estadísticas del catálogo utilizando procesamiento paralelo.
- Aplicar buenas prácticas de programación orientada a objetos.

---

## Características del sistema

El sistema ofrece las siguientes funcionalidades:

- Visualizar todas las películas organizadas por género.
- Calcular el total de votos por género utilizando `parallelStream`.
- Generar recomendaciones filtradas por género.
- Crear y seleccionar perfiles de usuario.
- Registrar películas vistas.
- Puntuar películas.
- Consultar el historial de películas vistas.
- Evitar recomendaciones de películas que el usuario ya haya visto.

Las recomendaciones se generan aplicando los siguientes criterios:

- Rating mayor a 4.0
- Al menos 100 votos
- Películas no vistas por el usuario activo

---

## Estructura del proyecto

El proyecto sigue la estructura estándar de Maven:
```RETO4-SISTEMA-DE-RECOMENDACION
│
├── logs
│
├── src
│ └── main
│ ├── java
│ │ └── com
│ │ └── dev
│ │ └── mxdelgado
│ │ ├── DuplicateOperationException.java
│ │ ├── InvalidOptionException.java
│ │ ├── InvalidSearchException.java
│ │ ├── Main.java
│ │ ├── Movie.java
│ │ ├── MovieNotFoundException.java
│ │ ├── RecommendationSystem.java
│ │ ├── User.java
│ │ └── UserService.java
│
│ └── resources
│ └── log4j2.xml
│
├── target
│
├── .gitignore
└── pom.xml
```
---

## Descripción de las clases

### Main

Clase principal del sistema.  
Se encarga de iniciar la aplicación, mostrar el menú principal, gestionar la interacción con el usuario y coordinar el uso del sistema de recomendación.

---

### RecommendationSystem

Contiene la lógica principal del sistema de recomendación.

Responsabilidades:

- Cargar el catálogo de películas.
- Obtener géneros disponibles.
- Filtrar películas por género.
- Generar recomendaciones.
- Calcular estadísticas del catálogo.
- Buscar películas por nombre.

---

### Movie

Representa una película dentro del sistema.

Cada película contiene:

- Título
- Género
- Rating promedio
- Número de votos

Incluye un método para actualizar el rating promedio cuando un usuario agrega una nueva puntuación.

---

### User

Representa un perfil de usuario.

Cada usuario mantiene:

- Nombre de perfil
- Historial de películas vistas
- Puntuaciones personales realizadas

---

### UserService

Clase encargada de gestionar los perfiles de usuario.

Permite:

- Crear usuarios
- Buscar usuarios
- Listar perfiles existentes

---

### Excepciones personalizadas

El sistema incluye varias excepciones para mejorar el control de errores:

- `InvalidOptionException`
- `DuplicateOperationException`
- `InvalidSearchException`
- `MovieNotFoundException`

Estas excepciones permiten manejar errores de entrada del usuario de forma controlada.

---

## Tecnologías utilizadas

- Java
- Maven
- Programación Orientada a Objetos (OOP)
- Java Streams
- Parallel Streams
- Colecciones de Java (`List`, `Set`, `Map`)
- Manejo de excepciones
- Logger SLF4J
- Log4j2

---

## Sistema de recomendaciones

El sistema recomienda películas aplicando los siguientes criterios:

- Rating mayor a **4.0**
- Al menos **100 votos**
- Películas del género seleccionado
- Películas que **no hayan sido vistas por el usuario**

Las recomendaciones se ordenan por:

1. Rating (de mayor a menor)
2. Título de la película

---

## Ejecución del proyecto

El proyecto utiliza **Maven**.
---

## Ejemplo de uso

Al iniciar el sistema se solicitará crear o seleccionar un perfil de usuario.

Luego se mostrará el menú principal:

1.Ver todas las películas por género

2.Calcular el total de votos por género

3.Recomendar películas

4.Gestionar mi perfil de usuario

5.Cambiar perfil

0.Salir


El usuario puede navegar por las diferentes opciones para consultar el catálogo, registrar películas vistas o recibir recomendaciones.

---

## Conceptos de Java aplicados

Este proyecto aplica varios conceptos importantes del lenguaje Java:

- Programación orientada a objetos
- Encapsulamiento
- Uso de colecciones (`HashSet`, `HashMap`, `ArrayList`)
- Streams y procesamiento funcional
- Parallel Streams
- Manejo de excepciones personalizadas
- Inmutabilidad en colecciones
- Normalización de datos
- Sincronización con `synchronized`
- Registro de eventos mediante logging

---

## Autores

Kevin Esteban Sánchez Méndez  
Marlon Xavier Delgado Ruiz

---

## Organización

Dev Senior Code

---

## Año

2026







