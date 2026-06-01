package ejemplocup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerificadorSemantico { 
    // Estructuras para simular el catálogo del motor de base de datos
    private static Map<String, Boolean> basesDeDatos = new HashMap<>();
    private static String baseDatosEnUso = null;
    private static Map<String, List<String>> tablasCampos = new HashMap<>();
    private static Map<String, String> tiposCampos = new HashMap<>();
    private static Map<String, Boolean> llavesPrimarias = new HashMap<>();

    /* MÓDULO 1: BASE DE DATOS (GENERAL) */
    public static void registrarCreateDatabase(String id) {
        if (basesDeDatos.containsKey(id)) {
            System.err.println("ERROR Semantico: M1 - No se puede crear dos veces la misma base de datos: " + id);
        } else {
            basesDeDatos.put(id, true);
            System.out.println("Semantico OK: Base de datos creada: " + id);
        }
    }

    public static void registrarDropDatabase(String id) {
        if (!basesDeDatos.containsKey(id)) {
            System.err.println("ERROR Semantico: M1 - No se puede eliminar una base de datos que no existe: " + id);
        } else {
            basesDeDatos.remove(id);
            if (id.equals(baseDatosEnUso)) baseDatosEnUso = null; // M1: Ya no se puede usar despues
            System.out.println("Semantico OK: Base de datos eliminada: " + id);
        }
    }

    /* MÓDULO 2: BASE DE DATOS EN USO */
    public static void registrarUseDatabase(String id) {
        if (!basesDeDatos.containsKey(id)) {
            System.err.println("ERROR Semantico: M1/M2 - No puedes usar '" + id + "' porque no se ha creado.");
            return;
        }
        baseDatosEnUso = id;
        System.out.println("Semantico OK: Cambiando a la base de datos: " + id);
    }

    public static boolean verificarBaseDatosEnUso() {
        if (baseDatosEnUso == null) {
            System.err.println("ERROR Semantico: M2 - No hay una base de datos en uso. Selecciona una con USE primero.");
            return false;
        }
        return true;
    }

    /* MÓDULO 3: TABLAS */
    public static boolean verificarCreateTable(String nombreTabla) {
        if (!verificarBaseDatosEnUso()) return false;
        
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        if (tablasCampos.containsKey(claveTabla)) {
            System.err.println("ERROR Semantico: M3 - No se puede crear dos veces la misma tabla: " + nombreTabla);
            return false;
        }
        tablasCampos.put(claveTabla, new ArrayList<>());
        System.out.println("Semantico OK: Tabla '" + nombreTabla + "' creada en '" + baseDatosEnUso + "'.");
        return true;
    }

    public static boolean verificarTablaExiste(String nombreTabla, String contexto) {
        if (!verificarBaseDatosEnUso()) return false;
        
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        if (!tablasCampos.containsKey(claveTabla)) {
            System.err.println("ERROR Semantico: M3 - No se puede " + contexto + " porque la tabla '" + nombreTabla + "' no existe.");
            return false;
        }
        return true;
    }

    /* MÓDULO 4: CAMPOS DE LA TABLA Y VERIFICACION DE TIPOS */
    public static void registrarColumna(String nombreTabla, String columna, String tipo, boolean isPrimaryKey) {
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        if (!tablasCampos.containsKey(claveTabla)) return;

        List<String> columnas = tablasCampos.get(claveTabla);
        if (columnas.contains(columna)) {
            System.err.println("ERROR Semantico: M4 - No se pueden crear campos duplicados en la tabla: " + columna);
            return;
        }

        if (isPrimaryKey) {
            if (llavesPrimarias.getOrDefault(claveTabla, false)) {
                System.err.println("ERROR Semantico: M4 - No se puede definir más de un campo como PRIMARY KEY en la tabla " + nombreTabla);
            } else {
                llavesPrimarias.put(claveTabla, true);
            }
        }

        columnas.add(columna);
        tiposCampos.put(claveTabla + "." + columna, tipo);
    }

    public static void verificarInsert(String nombreTabla, List<String> campos, List<Object> valores) {
        if (!verificarTablaExiste(nombreTabla, "insertar")) return;
        
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        List<String> columnasTabla = tablasCampos.get(claveTabla);

        if (campos.size() != valores.size()) {
            System.err.println("ERROR Semantico: M4 - El número de valores no coincide con el número de campos especificados.");
        }

        List<String> camposVistos = new ArrayList<>();
        for (String campo : campos) {
            if (!columnasTabla.contains(campo)) {
                System.err.println("ERROR Semantico: M4 - El campo '" + campo + "' no existe en la tabla.");
            }
            if (camposVistos.contains(campo)) {
                System.err.println("ERROR Semantico: M4 - No se puede repetir un campo dentro del mismo INSERT: " + campo);
            }
            camposVistos.add(campo);
        }
    }
    
    public static void verificarUpdate(String nombreTabla, String campoSet) {
        if (!verificarTablaExiste(nombreTabla, "actualizar")) return;
        
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        if (!tablasCampos.get(claveTabla).contains(campoSet)) {
            System.err.println("ERROR Semantico: M4 - No se puede actualizar un campo que no existe: " + campoSet);
        }
    }
}