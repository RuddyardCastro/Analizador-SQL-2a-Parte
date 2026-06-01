package ejemplocup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EjemploCUP {

    public final static int GENERAR = 1;
    public final static int EJECUTAR = 2;
    public final static int SALIR = 3;
    public final static int SEMANTICO = 4; 

    public static void main(String[] args) {
        java.util.Scanner in = new Scanner(System.in);
        int valor = 0;
        
        do {
            System.out.println("Elija una opcion: ");
            System.out.println("1) Generar");
            System.out.println("2) Ejecutar sintactico");
            System.out.println("3) Salir");
            System.out.println("4) Ejecutar semantico"); // Agregamos la opción al menú visual
            System.out.print("Opcion: ");
            valor = in.nextInt();
            
            switch (valor) {
                case GENERAR: {
                    System.out.println("\n*** Generando ***\n");
                    String archLexico = "";
                    String archSintactico = "";
                    String archSemantico = ""; 
                    
                    if (args.length > 0) {
                        System.out.println("\n*** Procesando archivos custom ***\n");
                        archLexico = args[0];
                        archSintactico = args[1];
                        archSemantico = args[2];
                        // Si pasan argumentos por consola, archSemantico se queda como "semantico.cup"
                    } else {
                        System.out.println("\n*** Procesando archivo default ***\n");
                        archLexico = "alexico.flex";
                        archSintactico = "asintactico.cup";
                        archSemantico = "semantico.cup";
                    }
                    
                    // Configuramos los comandos para Java CUP
                    String[] alexico = {archLexico};
                    String[] asintactico = {"-parser", "AnalizadorSintactico", archSintactico};
                    
                     /*El analizador semantico */
                    String[] asemantico = {"-parser", "AnalizadorSemantico", archSemantico};                    
                    jflex.Main.main(alexico);
                    try {
                        java_cup.Main.main(asintactico); 
                        /*Genera el semántico*/
                        java_cup.Main.main(asemantico);  
                    } catch (Exception ex) {
                        Logger.getLogger(EjemploCUP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    // Movemos TODOS los archivos generados a la carpeta correcta
                    boolean mvAL = moverArch("AnalizadorLexico.java");
                    boolean mvAS = moverArch("AnalizadorSintactico.java");
                    boolean mvSym = moverArch("sym.java");
                    /*Movemos el semántico*/
                    boolean mvASEM = moverArch("AnalizadorSemantico.java"); 
                    
                    // Comprobamos que todos se movieron bien
                    if(mvAL && mvAS && mvSym && mvASEM){
                        System.exit(0);
                    }
                    System.out.println("Generado!");
                    break;
                }
                case EJECUTAR: {
                    String[] archivoPrueba = {"programa.txt"};
                    AnalizadorSintactico.main(archivoPrueba);
                 //   System.out.println("Ejecutado Sintáctico!");
                    break;
                }
                case SALIR: {
                    System.out.println("Adios!");
                    break;
                }
                case SEMANTICO: { // El nuevo bloque para ejecutar el semántico
                    System.out.println("\n*** Ejecutando Analisis semantico ***\n");
                    String[] archivoPrueba = {"programa.txt"};

                    try {
                        // CORRECCIÓN: Llamamos a la clase generada por CUP (AnalizadorSemantico)
                        /*Aqui me confudi y puse el  verificador semantico*/
                        AnalizadorSemantico.main(archivoPrueba);
                    } catch (Exception ex) {
                        System.out.println("Error al ejecutar el semántico");
                        /*esta funcio me la recomendo la ia para ver errores de mejor forma */
                        /*cuando incorpore otras reglas me ayudo a identificar error en semntico.cup por lago que borre*/
                        ex.printStackTrace(); 
                        break;
                    }

                    System.out.println("\nAnalisis Semantico Terminado!");
                    break;
                }
                default: {
                    System.out.println("Opcion no valida!");
                    break;
                }
            }
        } while (valor != 3);
    }

    public static boolean moverArch(String archNombre) {
        boolean efectuado = false;
        File arch = new File(archNombre);
        if (arch.exists()) {
            System.out.println("\n*** Moviendo " + arch + " \n***");
            Path currentRelativePath = Paths.get("");
            String nuevoDir = currentRelativePath.toAbsolutePath().toString()
                    + File.separator + "src" + File.separator
                    + "ejemplocup" + File.separator + arch.getName();
            File archViejo = new File(nuevoDir);
            archViejo.delete();
            if (arch.renameTo(new File(nuevoDir))) {
                System.out.println("\n*** Generado " + archNombre + "***\n");
                efectuado = true;
            } else {
                System.out.println("\n*** No movido " + archNombre + " ***\n");
            }
        } else {
            System.out.println("\n*** Codigo no existente ***\n");
        }
        return efectuado;
    }
}