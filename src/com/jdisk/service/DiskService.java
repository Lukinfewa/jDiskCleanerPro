package com.jdisk.service;

import com.jdisk.model.FileStats;
import com.jdisk.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Clase de servicio que gestiona las operaciones principales de la librería.
 * Implementa lógica de análisis, limpieza y organización mediante la API Java NIO y Streams.
 */
public class DiskService {

    /**
     * Analiza una ruta y devuelve los N archivos más pesados.
     * Utiliza programación funcional para filtrar, transformar y ordenar.
     *
     * @param root Ruta base de búsqueda.
     * @param limit Cantidad de archivos a listar (Top N).
     * @return Lista de objetos FileStats ordenados por tamaño.
     * @throws IOException Si ocurre un error de acceso al disco.
     */
    public List<FileStats> getTopFiles(Path root, int limit) throws IOException {
        // Files.walk abre un flujo (Stream) que recorre subcarpetas recursivamente.
        // El bloque try-with-resources asegura que el Stream se cierre al terminar.
        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile) // Descartamos carpetas, solo queremos archivos.
                    .map(path -> {
                        try {
                            // Transformamos cada 'Path' en nuestro record 'FileStats'
                            return new FileStats(path.getFileName().toString(), Files.size(path), path.toString());
                        } catch (IOException e) {
                            // En caso de error de acceso, devolvemos un objeto de error marcado.
                            return new FileStats("Error de acceso", 0, "");
                        }
                    })
                    // Comparamos los tamaños de forma descendente (f2 vs f1).
                    .sorted((f1, f2) -> Long.compare(f2.size(), f1.size()))
                    .limit(limit) // Solo tomamos los primeros 'N'.
                    .collect(Collectors.toList()); // Convertimos el flujo final en una Lista.
        }
    }

    /**
     * Busca y elimina archivos temporales con extensiones específicas (.tmp, .log).
     *
     * @param root Ruta donde realizar la limpieza.
     * @return Cantidad total de bytes liberados.
     * @throws IOException Si hay problemas al borrar o acceder a los archivos.
     */
    public long cleanTemporaryFiles(Path root) throws IOException {
        try (var stream = Files.walk(root)) {
            // Filtramos archivos que terminen en .tmp o .log (sin importar mayúsculas/minúsculas).
            var targets = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".tmp") || p.toString().toLowerCase().endsWith(".log"))
                    .toList(); // Los guardamos en una lista antes de borrar.

            long deletedSize = 0;
            for (Path p : targets) {
                deletedSize += Files.size(p); // Acumulamos el tamaño para el reporte final.
                Files.delete(p); // Borrado físico del archivo.
            }
            return deletedSize;
        }
    }

    /**
     * Organiza los archivos de la carpeta raíz moviéndolos a subcarpetas según su extensión.
     * Ejemplo: "vacaciones.jpg" se moverá a la carpeta "/JPG_Files/".
     *
     * @param root Ruta de la carpeta a organizar (no afecta a subcarpetas).
     * @throws IOException Si ocurre un error al crear carpetas o mover ficheros.
     */
    public void organizeFiles(Path root) throws IOException {
        // Files.list() solo mira el primer nivel (no es recursivo como walk).
        try (var stream = Files.list(root)) {
            stream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    String fileName = file.getFileName().toString();
                    String extension = "";
                    int i = fileName.lastIndexOf('.');
                    // Extraemos la extensión del nombre del archivo.
                    if (i > 0) extension = fileName.substring(i + 1).toLowerCase();

                    if (!extension.isEmpty()) {
                        // Definimos el nombre de la subcarpeta (ej. PNG_Files).
                        Path targetDir = root.resolve(extension.toUpperCase() + "_Files");
                        // Si la carpeta no existe, la creamos dinámicamente.
                        if (!Files.exists(targetDir)) Files.createDirectory(targetDir);
                        // Movemos el archivo a la subcarpeta manteniendo su nombre original.
                        Files.move(file, targetDir.resolve(file.getFileName()));
                    }
                } catch (IOException e) {
                    System.err.println("No se pudo mover: " + file.getFileName());
                }
            });
        }
    }

    /**
     * Detecta archivos duplicados en un directorio comparando su contenido real (Hash MD5).
     *
     * @param root Ruta donde buscar duplicados.
     * @return Un Mapa donde la clave es el Hash y el valor es la lista de archivos que coinciden.
     * @throws IOException Si hay errores de lectura.
     */
    public Map<String, List<Path>> findDuplicates(Path root) throws IOException {
        // Mapa para agrupar archivos por su "huella digital" (Hash).
        Map<String, List<Path>> hashGroups = new HashMap<>();

        try (var stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile).forEach(path -> {
                try {
                    // Evitamos procesar archivos vacíos.
                    if (Files.size(path) > 0) {
                        String hash = FileUtil.getFileHash(path);
                        // computeIfAbsent: si el hash no está en el mapa, crea una lista nueva;
                        // si ya está, añade el archivo a la lista existente.
                        hashGroups.computeIfAbsent(hash, k -> new ArrayList<>()).add(path);
                    }
                } catch (IOException ignored) {} // Ignoramos archivos bloqueados por el sistema.
            });
        }

        // Filtramos el mapa final: solo nos quedamos con los grupos que tienen más de 1 archivo.
        Map<String, List<Path>> duplicates = new HashMap<>();
        hashGroups.forEach((hash, paths) -> {
            if (paths.size() > 1) {
                duplicates.put(hash, paths);
            }
        });
        return duplicates;
    }
}