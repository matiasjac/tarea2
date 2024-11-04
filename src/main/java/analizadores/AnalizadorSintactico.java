package analizadores;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class AnalizadorSintactico {

    /*
        Gramatica inicial
        json => element eof
        element => object | array
        array => [element-list] | []
        element-list => element-list , element | element
        object => {attributes-list} | {}
        attributes-list => attribute-list , attribute | attribute
        attribute => attribute-name : attribute-value
        attribute-name => string
        attribute-value => element | string | number | true | false | null

        Reescribiendo la gramatica para resolver recursion por izquierda
        element-list => element e'
        e' => , element e' | ε

        attributes-list => attribute a'
        a' => , attribute a' | ε

        Nueva gramatica
        json => element eof
        element => object | array
        array => [element-list] | []
        element-list => element e'
        e' => , element e' | ε
        object => {attributes-list} | {}
        attributes-list => attribute a'
        a' => , attribute a' | ε
        attribute => attribute-name : attribute-value
        attribute-name => string
        attribute-value => element | string | number | true | false | null
     */

    private static List<String> tokens;
    private static int indiceActual = 0;
    private static String tokenActual = null;


    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\Matias\\Desktop\\fuente2.txt";
        try {
            String input = Utils.fileContentToString(inputFilePath);
            tokens = AnalizadorLexico.getTokens(input);
            json();
            System.out.println("El fuente ingresado es un json valido");
        } catch ( IOException e){
            System.out.println("Ocurrio un error al cargar el archivo " + inputFilePath);
        } catch (Exception e) {
            System.out.println("El fuente ingresado no es un json valido");
        }
    }

    private static void json() throws Exception{
        if(tokens == null || tokens.isEmpty()) return;
        // cargar el primer token
        getToken();
        element();
    }

    private static void match(String expToken) throws Exception{
        if(tokenActual.equals(expToken)) {
            indiceActual++;
            getToken();
        } else throw new Exception("Elemento no esperado");
    }

    private static void getToken(){
        if(indiceActual < tokens.size()) tokenActual = tokens.get(indiceActual);
        else tokenActual = "";
    }


    private static void element() throws Exception {
        if (tokenActual.equals("L_LLAVE")) {
            object();
        } else if(tokenActual.equals("L_CORCHETE")) {
            array();
        } else {
            throw new Exception("Elemento no esperado");
        }
    }

    private static void object() throws Exception {
        match("L_LLAVE");
        if(!tokenActual.equals("R_LLAVE")) attributesList();
        match("R_LLAVE");
    }

    private static void array() throws Exception {
        match("L_CORCHETE");
        if(!tokenActual.equals("R_CORCHETE")) elementList();
        match("R_CORCHETE");
    }

    private static void elementList() throws Exception{
        element();
        elementListPrima();
    }

    private static void elementListPrima() throws Exception{
        if (tokenActual.equals("COMA")) {
            match("COMA");
            element();
            elementListPrima();
        }
    }

    private static void attributesList() throws Exception{
        attribute();
        attributesListPrima();
    }

    private static void attributesListPrima() throws Exception{
        if (tokenActual.equals("COMA")) {
            match("COMA");
            attribute();
            attributesListPrima();
        }
    }

    private static void attribute() throws Exception {
        attributeName();
        match("DOS_PUNTOS");
        attributeValue();
    }

    private static void attributeName() throws Exception{
        match("STRING");
    }

    private static void attributeValue() throws Exception {
        switch (tokenActual){
            case "L_CORCHETE", "L_LLAVE":
                element();
                break;
            case "STRING":
                match("STRING");
                break;
            case "LITERAL_NUM":
                match("LITERAL_NUM");
                break;
            case "PR_TRUE":
                match("PR_TRUE");
                break;
            case "PR_FALSE":
                match("PR_FALSE");
                break;
            case "PR_NULL":
                match("PR_NULL");
                break;
            default:
                throw new Exception("Elemento no esperado");
        }
    }
}
