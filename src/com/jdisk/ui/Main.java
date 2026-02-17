import java.util.Scanner;

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int opcion = -1;

    System.out.println("=== JDiskCleaner Pro v1.0 ===");

    while (opcion != 0) {
      System.out.println("\n1. Configurar ruta de trabajo");
      System.out.println("2. Analizar espacio (Top 10 archivos)");
      System.out.println("3. Limpiar archivos temporales");
      System.out.println("4. Organizar archivos por extensión");
      System.out.println("0. Salir");
      System.out.print("\nSeleccione una opción: ");

      try {
        opcion = Integer.parseInt(sc.nextLine());
        processOption(opcion);
      } catch (NumberFormatException e) {
        System.out.println("Error: Por favor, introduzca un número válido.");
      }
    }
  }

  private static void processOption(int opcion) {
    switch (opcion) {
      case 1 -> System.out.println("Configurando ruta...");
      case 2 -> System.out.println("Analizando...");
      case 3 -> System.out.println("Limpiando...");
      case 0 -> System.out.println("Saliendo del programa...");
      default -> System.out.println("Opción no válida.");
    }
  }
