/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejemplocup;

public class VerificadorSemantico { 
    // Estas variables guardan el estado de lo que va descubriendo el analizador
    private static String baseDatosCreada = null;
    private static String baseDatosEnUso = null;

    // 5.1) Guardar que se creó la Base de Datos
    public static void registrarCreateDatabase(String id) {
        baseDatosCreada = id;
        System.out.println("Semántico OK: Base de datos creada: " + id);
    }

    // 5.2) Verificar si ya se creó antes de usarla
    public static void registrarUseDatabase(String id) {
        if (baseDatosCreada == null) {
            System.err.println("ERROR Semántico: No puedes usar '" + id + "' porque no se ha creado ninguna Base de Datos primero.");
            return;
        }
        if (!baseDatosCreada.equals(id)) {
            System.err.println("ERROR Semántico Intentas usar '" + id + "', pero la que creaste se llama '" + baseDatosCreada + "'.");
            return;
        }
        baseDatosEnUso = id;
        System.out.println("Semántico OK Cambiado a la base de datos: " + id);
    }

    // 5.3) Verificar que todo esté listo para crear la tabla
    public static void verificarCreateTable(String nombreTabla) {
        if (baseDatosCreada == null) {
            System.err.println("ERROR Semántico No puedes crear la tabla '" + nombreTabla + "' porque no has creado una Base de Datos.");
            return;
        }
        if (baseDatosEnUso == null) {
            System.err.println("ERROR Semántico No puedes crear la tabla '" + nombreTabla + "' porque no has seleccionado la base de datos con 'USE'.");
            return;
        }
        System.out.println("Semántico OK Tabla '" + nombreTabla + "' creada con éxito dentro de '" + baseDatosEnUso + "'.");
    }

    static void main(String[] archivoPrueba) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}