package com.jdisk.model;

/**
 * Representa un objeto de datos que almacena las estadísticas de un archivo.
 * Al ser un 'record', Java genera automáticamente los campos, el constructor,
 * y los métodos de acceso (getters).
 *
 * @param name Nombre del archivo.
 * @param size Tamaño en bytes.
 * @param path Ruta completa en el sistema.
 */
public record FileStats(String name, long size, String path) {

    /**
     * Personalizamos el método toString para que, al imprimir el objeto,
     * la información se vea como una tabla limpia en la consola.
     */
    @Override
    public String toString() {
        // String.format permite alinear el texto:
        // %-30s  -> El nombre ocupa 30 caracteres alineado a la izquierda.
        // |      -> Separador visual.
        // %10.2f -> El tamaño ocupa 10 caracteres, con 2 decimales.
        // MB     -> Etiqueta de unidad.

        return String.format("%-30s | %10.2f MB",
                name,
                size / (1024.0 * 1024.0)); // Convertimos bytes a Megabytes
    }
}
