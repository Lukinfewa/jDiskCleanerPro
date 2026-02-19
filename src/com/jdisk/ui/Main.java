package com.jdisk.ui;

import com.jdisk.model.FileStats;
import com.jdisk.service.DiskService;
import com.jdisk.util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase principal que actúa como interfaz de usuario por consola.
 * Gestiona el menú interactivo y la comunicación con el servicio de limpieza.
 */
public class Main {
  // Variables estáticas para que sean accesibles desde los métodos del Main
  private static Path selectedPath = null;
  private static final DiskService service = new DiskService();
  private static final Scanner sc = new Scanner(System.in);

  public static void main(String[] args) {
    int opcion = -1;

    // Bucle principal del programa
    while (opcion != 0) {
      printMenu();
      try {
        // Leemos la opción como String y convertimos a Int para evitar errores de Scanner
        opcion = Integer.parseInt(sc.nextLine());
        processOption(opcion);
      } catch (NumberFormatException e) {
        System.out.println("❌ Error: Por favor, introduce un número válido del 0 al 5.");
      } catch (Exception e) {
        System.out.println("❌ Error inesperado: " + e.getMessage());
      }
    }
  }

  /**
   * Imprime el menú visual en la consola.
   */
  private static void printMenu() {
    System.out.println("\n==========================================");
    System.out.println("       💾 JDiskCleaner Pro v1.0");
    System.out.println("==========================================");
    System.out.println(" Ruta actual: " + (selectedPath == null ? "NO CONFIGURADA" : selectedPath));
    System.out.println("------------------------------------------");
    System.out.println(" 1. Configurar ruta de trabajo");
    System.out.println(" 2. Analizar: Top 10 archivos más grandes");
    System.out.println(" 3. Limpiar: Archivos temporales (.tmp, .log)");
    System.out.println(" 4. Organizar: Clasificar por extensión");
    System.out.println(" 5. Jefe Final: Buscar duplicados (Hash MD5)");
    System.out.println(" 0. Salir");
    System.out.print("\n👉 Seleccione una opción: ");
  }

  /**
   * Lógica de ejecución según la opción elegida por el usuario.
   * @param opcion Número de la opción seleccionada.
   */
  private static void processOption(int opcion) throws Exception {
    switch (opcion) {
      case 1 -> {
        // CONFIGURAR RUTA
        System.out.print("Introduce la ruta completa (ej: C:\\Pruebas): ");
        String input = sc.nextLine();
        // Paths.get convierte el texto en un objeto Path de Java NIO
        Path tempPath = Paths.get(input);

        if (tempPath.toFile().exists() && tempPath.toFile().isDirectory()) {
          selectedPath = tempPath;
          System.out.println("✅ Ruta configurada correctamente.");
        } else {
          System.out.println("❌ Error: La ruta no existe o no es una carpeta.");
        }
      }

      case 2 -> {
        // TOP 10 ARCHIVOS
        checkPathSelected();
        System.out.println("🔍 Analizando archivos más pesados...");
        List<FileStats> topFiles = service.getTopFiles(selectedPath, 10);

        System.out.println("\n--- RANKING DE ARCHIVOS (TOP 10) ---");
        // Usamos programación funcional para imprimir la lista
        topFiles.forEach(System.out::println);
      }

      case 3 -> {
        // LIMPIAR TEMPORALES
        checkPathSelected();
        System.out.print("⚠️ ¿Seguro que quieres borrar archivos temporales en " + selectedPath + "? (S/N): ");
        if (sc.nextLine().equalsIgnoreCase("S")) {
          long sizeDeleted = service.cleanTemporaryFiles(selectedPath);
          System.out.println("✅ Limpieza terminada.");
          System.out.println("📊 Espacio recuperado: " + FileUtil.formatSize(sizeDeleted));
        }
      }

      case 4 -> {
        // ORGANIZAR POR EXTENSIÓN
        checkPathSelected();
        System.out.println("📁 Organizando archivos por tipo...");
        service.organizeFiles(selectedPath);
        System.out.println("✅ Operación completada. Revisa las nuevas subcarpetas.");
      }

      case 5 -> {
        // BUSCAR DUPLICADOS
        checkPathSelected();
        System.out.println("🔬 Escaneando contenido binario (MD5)... esto puede tardar.");
        Map<String, List<Path>> dups = service.findDuplicates(selectedPath);

        if (dups.isEmpty()) {
          System.out.println("✅ No se encontraron archivos idénticos.");
        } else {
          System.out.println("\n🚩 ARCHIVOS DUPLICADOS ENCONTRADOS:");
          dups.forEach((hash, paths) -> {
            System.out.println("\nID de contenido (Hash): " + hash);
            paths.forEach(p -> System.out.println("  > " + p.getFileName()));
          });
        }
      }

      case 0 -> System.out.println("👋 Saliendo de JDiskCleaner Pro. ¡Hasta pronto!");

      default -> System.out.println("⚠️ Opción no reconocida.");
    }
  }

  /**
   * Método auxiliar para validar que el usuario ha configurado una ruta antes de operar.
   */
  private static void checkPathSelected() throws Exception {
    if (selectedPath == null) {
      // Lanzamos excepción si no hay ruta, el 'catch' del main la capturará
      throw new Exception("Debes configurar una ruta de trabajo primero (Opción 1).");
    }
  }
}