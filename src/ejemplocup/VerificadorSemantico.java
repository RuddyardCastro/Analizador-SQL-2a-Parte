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

    /* Modulo 1: BASE DE DATOS (GENERAL) */
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
            if (id.equals(baseDatosEnUso)) baseDatosEnUso = null;
            System.out.println("Semantico OK: Base de datos eliminada: " + id);
        }
    }

    /* Modulo 2: BASE DE DATOS EN USO */
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

    /* ModulO 3: TABLAS */
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

    /* Modulo 4: CAMPOS DE LA TABLA Y VERIFICACION DE TIPOS */
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
    
    public static void verificarLlaveForanea(String tablaActual, String campoFk, String tablaRef, String campoRef) {
        if (!verificarTablaExiste(tablaRef, "crear llave foranea")) return;
        String claveRef = baseDatosEnUso + "." + tablaRef;
        if (!tablasCampos.get(claveRef).contains(campoRef)) {
            System.err.println("ERROR Semantico: Modulo 4 - La tabla '" + tablaRef + "' referenciada por FOREIGN KEY no contiene el campo '" + campoRef + "'.");
        }
    }

     public static void verificarInsert(String nombreTabla, List<String> campos, List<Object> valores) {
        if (!verificarTablaExiste(nombreTabla, "insertar")) return;
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        List<String> columnasTabla = tablasCampos.get(claveTabla);
        
        if (campos.size() != valores.size()) {
            System.err.println("ERROR Semantico: Modulo 4 - El número de valores no coincide con el número de campos especificados.");
            return;
        }

        List<String> camposVistos = new ArrayList<>();
        for (int i = 0; i < campos.size(); i++) {
            String campo = campos.get(i);
            Object valor = valores.get(i);

            if (!columnasTabla.contains(campo)) {
                System.err.println("ERROR Semantico: Modulo 4 - El campo '" + campo + "' no existe en la tabla.");
            } else {
                verificarTipoDato(nombreTabla, campo, valor);
            }
            if (camposVistos.contains(campo)) {
                System.err.println("ERROR Semantico: Modulo 4 - No se puede repetir un campo dentro del mismo INSERT: " + campo);
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
    
    public static boolean verificarTipoDato(String nombreTabla, String campo, Object valor) {
        String claveCampo = baseDatosEnUso + "." + nombreTabla + "." + campo;
        if (!tiposCampos.containsKey(claveCampo)) return false; 

        String tipoEsperado = tiposCampos.get(claveCampo); 
        String tipoValor = "";
        int longitudString = 0;

        if (valor instanceof Integer) {
            tipoValor = "INT";
        } else if (valor instanceof Float) {
            tipoValor = "FLOAT";
        } else if (valor instanceof String) {
            String strVal = (String) valor;
            if (strVal.startsWith("'") && strVal.endsWith("'")) {
                tipoValor = "VARCHAR";
                longitudString = strVal.length() - 2; 
            } else {
                tipoValor = "ID";
            }
        }

        // 1. Validar INT
        if (tipoEsperado.equals("INT") && !tipoValor.equals("INT")) {
            System.err.println("ERROR Semantico: Modulo 4 - El campo '" + campo + "' espera un INT pero recibio " + tipoValor);
            return false;
        }
        // 2. Validar FLOAT y DECIMAL
        if ((tipoEsperado.startsWith("FLOAT") || tipoEsperado.startsWith("DECIMAL")) && (!tipoValor.equals("FLOAT") && !tipoValor.equals("INT"))) {
            System.err.println("ERROR Semantico: Modulo 4 - El campo '" + campo + "' espera un valor numérico real pero recibio " + tipoValor);
            return false;
        }
        // 3. Validar VARCHAR y su longitud
        if (tipoEsperado.startsWith("VARCHAR")) {
            if (!tipoValor.equals("VARCHAR")) {
                System.err.println("ERROR Semantico: Modulo 4 - El campo '" + campo + "' espera un VARCHAR pero se le dio " + tipoValor);
                return false;
            }
            
            String[] partes = tipoEsperado.split(":");
            if (partes.length == 2) {
                int maxLen = Integer.parseInt(partes[1]);
                if (longitudString > maxLen) {
                    System.err.println("ERROR Semantico: Modulo 4 - El texto excede la longitud para '" + campo + "' (Max: " + maxLen + ", Recibido: " + longitudString + ").");
                    return false;
                }
            }
        }
        return true;
    }

    public static void verificarUpdate(String nombreTabla, String campoSet, Object valor) {
        if (!verificarTablaExiste(nombreTabla, "actualizar")) return;
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        
        if (!tablasCampos.get(claveTabla).contains(campoSet)) {
            System.err.println("ERROR Semantico: Modulo 4 - No se puede actualizar un campo que no existe: " + campoSet);
        } else {
            verificarTipoDato(nombreTabla, campoSet, valor);
        }
    }

    public static void verificarWhere(String nombreTabla, String campoWhere, Object valor) {
        String claveTabla = baseDatosEnUso + "." + nombreTabla;
        if (!tablasCampos.get(claveTabla).contains(campoWhere)) {
            System.err.println("ERROR Semantico: Modulo 4 - El campo '" + campoWhere + "' del WHERE no existe en la tabla.");
        } else {
            verificarTipoDato(nombreTabla, campoWhere, valor);
        }
    }
}