

# Memoria Técnica: JDiskCleaner Pro Lib
**Librería de Análisis y Optimización de Sistemas de Ficheros**

**Autor:** Lucas Blanco Álvarez
**Fecha:** Febrero 2024  
**Proyecto:** Programación en Java (Manejo de I/O y Streams)

---

## 1. Descripción General y Diseño

### 1.1 Motivación
En el ecosistema digital actual, la acumulación de datos redundantes, archivos temporales y la desorganización de directorios es un problema común que degrada la productividad y agota los recursos de almacenamiento. El objetivo de **JDiskCleaner Pro** es proporcionar una API robusta y una herramienta de consola capaz de automatizar el mantenimiento de discos (HD, USB) mediante técnicas avanzadas de procesamiento de datos.

### 1.2 Objetivos de Diseño
El diseño de la librería se fundamenta en tres pilares:
1.  **Seguridad (Analyze-First):** Antes de cualquier operación destructiva, la librería permite analizar el impacto (tamaño recuperable).
2.  **Modularidad:** Separación estricta entre la lógica de negocio (`Service`), el modelo de datos (`Record`), las utilidades (`Util`) y la gestión de errores (`Exception`).
3.  **Modernidad:** Uso intensivo de **Java NIO** y **Programación Funcional (Streams API)** para garantizar un código eficiente y legible.

---

## 2. Operaciones Principales Implementadas

La librería expone un conjunto de operaciones críticas para la gestión del espacio:

| Operación | Método Técnico | Característica |
| :--- | :--- | :--- |
| **Análisis de Ocupación** | `getTopFiles` | Identifica los "N" archivos más pesados mediante un filtrado recursivo con `Files.walk`. |
| **Limpieza de Temporales** | `cleanTemporaryFiles` | Localiza patrones `.tmp` y `.log`. Calcula el espacio liberado antes de proceder al borrado físico. |
| **Organizador Inteligente**| `organizeFiles` | Escanea un directorio y clasifica archivos en subcarpetas basadas en su extensión (Ej: `/PDF_FILES`). |
| **Detector de Duplicados**| `findDuplicates` | **Operación Estrella:** No se basa en el nombre, sino en el contenido binario mediante el cálculo de **Hash MD5**. |
| **Reporte Formateado** | `FileUtil.formatSize` | Transforma valores en bytes a unidades legibles (MB, GB) para una mejor UX. |

---

## 3. Puntos Fuertes y Limitaciones

### Puntos Fuertes
*   **Eficiencia con Streams:** El uso de flujos de datos (`Streams`) permite procesar grandes volúmenes de archivos con un consumo de memoria optimizado, evitando cargar listas pesadas en RAM.
*   **Uso de Records:** La implementación de `FileStats` como un `record` de Java garantiza la inmutabilidad de los datos durante el análisis, siguiendo los estándares modernos de programación.
*   **Hasing MD5:** La capacidad de detectar duplicados reales (incluso con nombres distintos) eleva la herramienta de un simple script a una utilidad profesional.
*   **Gestión de Errores:** Se ha implementado `DiskCleanerException` para manejar de forma segura archivos bloqueados por el sistema o falta de permisos.

### Limitaciones
*   **Permisos de Sistema:** Al ser una herramienta a nivel de usuario, no puede modificar archivos protegidos por el Sistema Operativo (`System32`, `/root`).
*   **Coste Computacional del Hashing:** El análisis de duplicados en discos mecánicos muy grandes puede ser lento, ya que el cálculo del Hash requiere leer el archivo completo.

---

## 4. Validación y Pruebas (Menú Main)

La funcionalidad se valida mediante una interfaz de texto en la clase `Main`. El flujo de pruebas diseñado sigue esta secuencia:

1.  **Configuración de Ruta:** El usuario define la ruta de trabajo (ej. una memoria USB). El sistema valida su existencia.
2.  **Fase de Diagnóstico:** Se ejecuta el **Top 10**, permitiendo al usuario ver el estado actual del disco.
3.  **Fase de Acción:**
    *   Se prueban los filtros de limpieza (Temporales).
    *   Se ejecuta el organizador para verificar la creación dinámica de carpetas.
4.  **Fase de Verificación:** Se ejecuta el buscador de duplicados para confirmar que la lógica de Hashing agrupa correctamente los archivos idénticos.

---

## 5. Uso de Modelos de Lenguaje (LLM)

Para el desarrollo de esta práctica se ha utilizado **ChatGPT (OpenAI)** de forma estratégica y moderada:

*   **Refactorización de Código:** Para optimizar la transición de bucles `for-each` tradicionales a tuberías de **Streams API**, mejorando la elegancia del código.
*   **Implementación Criptográfica:** Como apoyo para el manejo de la clase `MessageDigest` al generar el Hash MD5, asegurando el correcto manejo de los buffers de lectura.
*   **Estructura Documental:** Para organizar la jerarquía de paquetes siguiendo las convenciones de la industria (Maven/Standard layout).
