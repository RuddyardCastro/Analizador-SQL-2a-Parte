/* --------------------------Codigo de Usuario----------------------- */
package ejemplocup;

import java_cup.runtime.*;
import java.io.Reader;
      
%% //inicio de opciones
   
/* ------ Seccion de opciones y declaraciones de JFlex -------------- */  
   
/* Cambiamos el nombre de la clase del analizador a Lexer */
%class AnalizadorLexico

/*
    Activar el contador de lineas, variable yyline
    Activar el contador de columna, variable yycolumn
*/
%line
%column
    
/* Activamos la compatibilidad con Java CUP para analizadores sintacticos(parser) */
%cup
   
/*
    Declaraciones
    El codigo entre %{  y %} sera copiado integramente en el analizador generado.
*/
%{
    /* Generamos un java_cup.Symbol para guardar el tipo de token encontrado */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Generamos un Symbol para el tipo de token encontrado junto con su valor */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
   

/*
    Macro declaraciones
    Declaramos expresiones regulares que despues usaremos en las reglas lexicas.
*/
   
/* Un salto de linea es un \n, \r o \r\n dependiendo del SO */
Salto = \r|\n|\r\n
   
/* Espacio es un espacio en blanco, tabulador \t, salto de linea o avance de pagina \f */
Espacio = {Salto} | [ \t\f]
   
/* EXPRESIONES REGULARES */
Entero = 0 | [1-9][0-9]*
Identificador = [a-zA-Z][A-Za-z0-9_]*
NumReal = {Entero} "." [0-9]*

%% // --- SEGUNDO SEPARADOR: Inicio de Reglas Léxicas (Solo debe haber uno aquí) ---

<YYINITIAL> {
    
    /* 1. ESPACIOS */
    {Espacio}           { /* ignora el espacio */ }

    /* 2. PALABRAS RESERVADAS SQL (DML) */
    
    "SELECT" | "select"    { System.out.print("SELECT "); 
                            return symbol(sym.SELECT); }
    "FROM" | "from"        { System.out.print("FROM "); 
                            return symbol(sym.FROM); }
    "WHERE" | "where"      { System.out.print("WHERE "); 
                            return symbol(sym.WHERE); }
    "UPDATE" | "update"    { System.out.print("UPDATE "); 
                                return symbol(sym.UPDATE); }
    "SET" | "set"          { System.out.print("SET "); 
                            return symbol(sym.SET); }
    "INNER" | "inner"      { System.out.print("INNER "); 
                            return symbol(sym.INNER); }
    "JOIN"  | "join"       { System.out.print("JOIN "); 
                            return symbol(sym.JOIN); }
    "ON"    | "on"         { System.out.print("ON "); return symbol(sym.ON); }

    /* palabras para el proy final */
    "INSERT" | "insert"    { System.out.print("INSERT "); return symbol(sym.INSERT); }
    "INTO"   | "into"      { System.out.print("INTO "); return symbol(sym.INTO); }
    "VALUES" | "values"    { System.out.print("VALUES "); return symbol(sym.VALUES); }

    /* Nuevo tipo de dato FLOAT */
    "FLOAT"  | "float"     { System.out.print("TIPO_FLOAT "); return symbol(sym.FLOAT); }

    /* DML - Ordenación */
    "ORDER" | "order"      { System.out.print("ORDER "); return symbol(sym.ORDER); }
    "BY" | "by"            { System.out.print("BY "); return symbol(sym.BY); }
    "ASC" | "asc"          { System.out.print("ASC "); return symbol(sym.ASC); }
    "DESC" | "desc"        { System.out.print("DESC "); return symbol(sym.DESC); }

    /* DDL - Definición de Datos */
    "CREATE" | "create"    { System.out.print("CREATE "); return symbol(sym.CREATE); }
    "DATABASE" | "database" { System.out.print("DATABASE "); return symbol(sym.DATABASE); }
    "TABLE" | "table"      { System.out.print("TABLE "); return symbol(sym.TABLE); }
    "USE" | "use"          { System.out.print("USE "); return symbol(sym.USE); }
        
    /* Palabras reservadas para tipos de datos y restricciones */
    "INT" | "int"          { System.out.print("TIPO_INT "); return symbol(sym.INT); } 
    "VARCHAR" | "varchar"  { System.out.print("TIPO_VARCHAR "); return symbol(sym.VARCHAR); }
    "PRIMARY" | "primary"  { System.out.print("PRIMARY "); return symbol(sym.PRIMARY); }
    "KEY" | "key"          { System.out.print("KEY "); return symbol(sym.KEY); }

    /* AGREGADOS PARA CUBRIR PARTE OBLIGATORIA DEL PROYECTO FINAL */
    "AUTO_INCREMENT" | "auto_increment" { System.out.print("AUTO_INCREMENT "); return symbol(sym.AUTO_INCREMENT); }
    "NOT" | "not"          { System.out.print("NOT "); return symbol(sym.NOT); }
    "NULL" | "null"        { System.out.print("NULL "); return symbol(sym.NULL); }
    "DECIMAL" | "decimal"  { System.out.print("DECIMAL "); return symbol(sym.DECIMAL); }
    "FOREIGN" | "foreign"  { System.out.print("FOREIGN "); return symbol(sym.FOREIGN); }
    "REFERENCES" | "references" { System.out.print("REFERENCES "); return symbol(sym.REFERENCES); }
    "AS" | "as"            { System.out.print("AS "); return symbol(sym.AS); }
    "GROUP" | "group"      { System.out.print("GROUP "); return symbol(sym.GROUP); }
    "COUNT" | "count"      { System.out.print("COUNT "); 
                            return symbol(sym.COUNT); }
    "AND" | "and"      { System.out.print("AND "); 
                            return symbol(sym.AND); }
    "OR" | "or"      { System.out.print("OR "); 
                            return symbol(sym.OR); }

    /* 3. OPERADORES Y SÍMBOLOS SQL */
    "*"                    { System.out.print("* "); return symbol(sym.ASTERISCO); }
    "="                    { System.out.print("= "); return symbol(sym.IGUAL); }
    ","                    { System.out.print(", "); return symbol(sym.COMA); }
    ";"                    { System.out.print(";\n"); return symbol(sym.PUNTOYCOMA); }
    "("                    { System.out.print("( "); return symbol(sym.PAR_A); }
    ")"                    { System.out.print(") "); return symbol(sym.PAR_C); }
    "."                    { System.out.print(". "); return symbol(sym.PUNTO); }

    /* Operadores de comparación  */
    ">"                    { System.out.print("> "); return symbol(sym.MAYOR); }
    "<"                    { System.out.print("< "); return symbol(sym.MENOR); }

    /* Cadenas de texto con comilla simple */
       "'" [^]* "'"          { System.out.print("LITERAL_STR ");
                            return symbol(sym.LITERAL_STR, yytext()); }

    /* Identificadores y Números */
    {Identificador}        { System.out.print("ID "); return symbol(sym.ID, yytext()); }
    {Entero}               { System.out.print("ENTERO "); return symbol(sym.ENTERO, Integer.valueOf(yytext())); }
    {NumReal}              { System.out.print("NUREAL "); return symbol(sym.NUREAL, Float.valueOf(yytext())); }
}

/* Error de token ilegal */
[^]                    { throw new Error("Caracter ilegal <"+yytext()+">"); }