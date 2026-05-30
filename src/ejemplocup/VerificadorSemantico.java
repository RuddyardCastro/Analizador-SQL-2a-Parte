/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejemplocup;

public class VerificadorSemantico { 
    // Estas variables guardan el estado de lo que va descubriendo el analizador
    private static String baseDatosCreada = null;
    private static String baseDatosEnUso = null;

    // ) Guardar que se creó la Base de Datos
    public static void registrarCreateDatabase(String id) {
        baseDatosCreada = id;
        System.out.println("Semantico OK: Base de datos creada: " + id);
    }

    // ) Verificar si ya se creó antes de usarla
    public static void registrarUseDatabase(String id) {
        if (baseDatosCreada == null) {
            System.err.println("ERROR Semantico: No puedes usar '" + id + "' porque no se ha creado ninguna Base de Datos primero.");
            return;
        }
        if (!baseDatosCreada.equals(id)) {
            System.err.println("ERROR Semantico Intentas usar '" + id + "', pero la que creaste se llama '" + baseDatosCreada + "'.");
            return;
        }
        baseDatosEnUso = id;
        System.out.println("Semantico OK Cambialo a la base de datos: " + id);
    }

    // Esta cosa  verificar que todo esté listo para crear la tabla
    public static void verificarCreateTable(String nombreTabla) {
        if (baseDatosCreada == null) {
            System.err.println("ERROR Semantico No puedes crear la tabla '" + nombreTabla + "' porque no has creado una Base de Datos.");
            return;
        }
        if (baseDatosEnUso == null) {
            System.err.println("ERROR Semantico No puedes crear la tabla '" + nombreTabla + "' porque no has seleccionado la base de datos con 'USE'.");
            return;
        }
        System.out.println("Semantico OK Tabla '" + nombreTabla + "' creada con exito dentro de '" + baseDatosEnUso + "'.");
    }

    
}