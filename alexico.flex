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

%% //fin de opciones

/* -------------------- Seccion de reglas lexicas ------------------ */
   
<YYINITIAL> {
    
    /* 1. ESPACIOS */
    {Espacio}           { /* ignora el espacio */ } 

    /* 2. PALABRAS a MySQL */
    
    "SELECT" | "select"    { return symbol(sym.SELECT); }


    "FROM" | "from"        { return symbol(sym.FROM); }


    "WHERE" | "where"      { return symbol(sym.WHERE); }


    


    "UPDATE" | "update"    { return symbol(sym.UPDATE); }


    "SET" | "set"          { return symbol(sym.SET); }

    


    "INNER" | "inner" { return symbol(sym.INNER); }

        "JOIN"  | "join"  { return symbol(sym.JOIN); }

    "ON"    | "on"    { return symbol(sym.ON); }


    /* 3. OPERADORES Y SÍMBOLOS SQL */

    "*"                    { return symbol(sym.ASTERISCO); }
    "="                    { return symbol(sym.IGUAL); }
    ","                    { return symbol(sym.COMA); }
    ";"                    { return symbol(sym.PUNTOYCOMA); }
    "("                    { return symbol(sym.PAR_A); }
    ")"                    { return symbol(sym.PAR_C); }
    "."                    { return symbol(sym.PUNYO); }

    /* 4. LITERALES: Cadenas de texto con comilla simple */

    "'" [^']* "'"          { return symbol(sym.LITERAL_STR, yytext()); }

    /* 5. EXPRESIONES REGULARES: Identificadores y Números */
    
    {Identificador}        { return symbol(sym.ID, yytext()); }
    {Entero}               { return symbol(sym.ENTERO, Integer.valueOf(yytext())); }
    {NumReal}              { return symbol(sym.NUREAL, Float.valueOf(yytext())); }
}

/* Error de token ilegal */
[^]                    { throw new Error("Caracter ilegal <"+yytext()+">"); }