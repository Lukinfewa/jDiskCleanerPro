package com.jdisk.exception;

/**
 * Excepción personalizada para la librería JDiskCleaner.
 * Se utiliza para centralizar y capturar errores específicos relacionados
 * con el sistema de ficheros (permisos, rutas inválidas, archivos bloqueados).
 *
 * Al heredar de 'Exception', es una "Checked Exception", lo que obliga al
 * desarrollador a gestionarla mediante bloques try-catch.
 */
public class DiskCleanerException extends Exception {

    /**
     * Constructor que acepta un mensaje descriptivo del error.
     * @param message Mensaje que explica qué ha fallado.
     */
    public DiskCleanerException(String message) {
        // Llamamos al constructor de la clase padre (Exception)
        super(message);
    }

    /**
     * Constructor avanzado para el "Encadenamiento de Excepciones".
     * Permite guardar la causa original del error (por ejemplo, una IOException del sistema).
     *
     * @param message Mensaje descriptivo.
     * @param cause El error original que provocó este fallo (Throwable).
     */
    public DiskCleanerException(String message, Throwable cause) {
        // Al pasarle la 'cause' a super, no perdemos la traza original del error
        super(message, cause);
    }
}