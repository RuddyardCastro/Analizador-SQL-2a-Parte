
/* --------------------------Codigo de Usuario----------------------- */
package ejemplocup;

import java_cup.runtime.*;
import java.io.Reader;
      
%% //inicio de opciones
   
/* ------ Seccion de opciones y declaraciones de JFlex -------------- */  
   
/* 
    Cambiamos el nombre de la clase del analizador a Lexer
*/
%class AnalizadorLexico

/*
    Activar el contador de lineas, variable yyline
    Activar el contador de columna, variable yycolumn
*/
%line
%column
    
/* 
   Activamos la compatibilidad con Java CUP para analizadores
   sintacticos(parser)
*/
%cup
   
/*
    Declaraciones

    El codigo entre %{  y %} sera copiado integramente en el 
    analizador generado.
*/
%{
    /*  Generamos un java_cup.Symbol para guardar el tipo de token 
        encontrado */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Generamos un Symbol para el tipo de token encontrado 
       junto con su valor */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
   

/*
    Macro declaraciones
  
    Declaramos expresiones regulares que despues usaremos en las
    reglas lexicas.
*/
   
/*  Un salto de linea es un \n, \r o \r\n dependiendo del SO   */
Salto = \r|\n|\r\n
   
/* Espacio es un espacio en blanco, tabulador \t, salto de linea 
    o avance de pagina \f, normalmente son ignorados */
Espacio     = {Salto} | [ \t\f]
   
/* EXPRESIONES REGULARES */
Entero = 0 | [1-9][0-9]*
Identificador =[a-zA-Z][A-Za-z0-9]* 
NumReal=({Entero}+) "." ({Entero}*)





%% //fin de opciones
/* -------------------- Seccion de reglas lexicas ------------------ */
   
/*
   Esta seccion contiene expresiones regulares y acciones. 
   Las acciones son código en Java que se ejecutara cuando se
   encuentre una entrada valida para la expresion regular correspondiente */
   
   /* YYINITIAL es el estado inicial del analizador lexico al escanear.
      Las expresiones regulares solo serán comparadas si se encuentra
      en ese estado inicial. Es decir, cada vez que se encuentra una 
      coincidencia el scanner vuelve al estado inicial. Por lo cual se ignoran
      estados intermedios.*/
   
<YYINITIAL> {
   
    /* 							DECLARANDO LOS OPERADORES            */
    "+"                {  System.out.print(" + ");
                          return symbol(sym.OP_SUMA); }
    "-"                {  System.out.print(" - ");
                          return symbol(sym.OP_RESTA); }

    "*"                {  System.out.print(" * ");
                          return symbol(sym.OP_MULTI); }

    "/"                {  System.out.print(" / ");
                          return symbol(sym.OP_DIVIDE); }

    "="                {  System.out.print(" = ");
                          return symbol(sym.OP_IGUAL); }

    /* 							DECLARANDO SIMBOLOS           */

    ";"                {  System.out.print(" ; ");
                          return symbol(sym.PUNTOYCOMA); }

    ":"                {  System.out.print(":");
                          return symbol(sym.DOSPUNTOS); }

    ","                {  System.out.print(" , ");
                          return symbol(sym.SCOMA);}

	/*PARENTESIS CON COMILLA SENCILLA*/
    "('"                {  System.out.print(" (' ");
                          return symbol(sym.PARABRE); }

     "')"                {  System.out.print(" ') ");
                          return symbol(sym.PARCIERRA); }
    /*PARENTESISI NORMALES*/
     "("                {  System.out.print(" ( ");
                          return symbol(sym.PARIZQ); }

     ")"                {  System.out.print(" ) ");
                          return symbol(sym.PARDER); }

    /* 					PALABRAS RESERVADAS 						*/
    "PROGRAM" |"program"  {  System.out.print(" PROGRAM ");
                          return symbol(sym.NPROGRAM); }
    /*PALBRA RESERVADA */
    "USES" | "uses"       {  System.out.print(" USES");
                          return symbol(sym.RUSE); }

    "CRT" | "crt"        { System.out.print(" CRT");
                         return symbol(sym.DCRT);}

    "DOS" | "dos"        { System.out.print(" DOS");
                         return symbol(sym.DDOS);}

    "MATH" | "math"      { System.out.print(" MATH");
                         return symbol(sym.DMATH);}

    "GRAPH" | "graph"   { System.out.print(" GRAPH");
                         return symbol(sym.DGRAPH);}

    "CONST" | "const"   { System.out.print(" CONST ");
                         return symbol(sym.DCONST);}

    "VAR" | "var"       { System.out.print(" VAR ");
                         return symbol(sym.DVAR);}
   
    "STRING" | "string"  { System.out.print(" STRING ");
                          return symbol(sym.DSTRING);}

    "REAL" | "real"      { System.out.print(" REAL ");
                          return symbol(sym.DREAL);}

    "ENTERO" | "entero"  { System.out.print(" ENTERO ");
                          return symbol(sym.DENTERO);}

    "INTEGER" | "integer"   { System.out.print(" INTEGER ");
                            return symbol(sym.DINTEGER);}

    "BEGIN" | "begin"        { System.out.print(" BEGIN ");
                       return symbol(sym.DBEGIN);}

    "END" | "end"        { System.out.print(" END");
                       return symbol(sym.DEND);}

    "CLRSCR" | "clrscr"  { System.out.print(" clrscr ");
                       return symbol(sym.DCLRSCR);}

   "WRITE" | "write"     { System.out.print(" write ");
                       return symbol(sym.DWRITE);}

    "WRITELN" | "writeln"  { System.out.print(" writeln ");
                       return symbol(sym.DWRITELN);}

   "READLN" | "readln"     { System.out.print(" READLN ");
                       return symbol(sym.DREADLN);}

    "READKEY" | "readkey"  { System.out.print(" readkey");
                       return symbol(sym.DREADKEY);}


    /* Si se encuentra un entero, se imprime, se regresa un token numero
        que representa un entero y el valor que se obtuvo de la cadena yytext
        al convertirla a entero. yytext es el token encontrado. */
    {Entero}      {   System.out.print(yytext( )); 
                      return symbol(sym.ENTERO, new Integer(yytext())); }

    {Identificador}      {   System.out.print(yytext( )); 
                      return symbol(sym.ID, new String(yytext())); }

    {NumReal}      {   System.out.print(yytext()); 
                      return symbol(sym.NUREAL, new Float(yytext())); }

    /* No hace nada si encuentra el espacio en blanco */
    {Espacio}       { /* ignora el espacio */ } 
}


/* Si el token contenido en la entrada no coincide con ninguna regla
    entonces se marca un token ilegal */
[^]                    { throw new Error("Caracter ilegal <"+yytext()+">"); }
