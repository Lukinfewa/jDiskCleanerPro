package com.jdisk.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase de utilidades para el manejo de archivos.
 * Contiene métodos estáticos para formatear tamaños y calcular huellas digitales (Hashes).
 */
public class FileUtil {

    /**
     * Convierte un tamaño en bytes a un formato legible (KB, MB, GB).
     *
     * @param bytes Longitud del archivo en bytes.
     * @return String formateado (ej: "15.40 MB").
     */
    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        // Calculamos el exponente para saber si es KB, MB, GB...
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Genera una huella digital única (Hash MD5) basada en el contenido del archivo.
     * Se utiliza para identificar archivos idénticos aunque tengan nombres distintos.
     *
     * @param path Ruta del archivo a procesar.
     * @return Representación hexadecimal del hash MD5.
     */
    public static String getFileHash(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            // MessageDigest es la herramienta de Java para criptografía y hashes
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192]; // Leemos el archivo en trozos de 8KB
            int read;
            while ((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }

            // Convertimos el resultado binario a una cadena de texto (Hexadecimal)
            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest()) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException | IOException e) {
            // Si hay error (archivo bloqueado, etc.), devolvemos una marca de error
            return "error_hash_" + path.getFileName();
        }
    }
}