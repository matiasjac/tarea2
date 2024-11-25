package traductor;

import analizadores.AnalizadorLexico;
import analizadores.Token;
import analizadores.Utils;

import java.io.IOException;
import java.util.List;

public class Traductor {


    /*
    Gramatica
    json => element eof
    element => object | array
    array => [element-list]  | []

    element-list => element-list , element | element

    element-list => element element-list'
    element-list'=>  , element element-list' | ε

    object => {attributes-list} | {}

    attributes-list => attribute-list , attribute | attribute
    attributes-list => attribute attribute-list'
    attributes-list' => , attribute-list' |  ε


    attribute => attribute-name : attribute-value
    attribute-name => string
    attribute-value => element | string | number | true | false | null

    Reglas semanticas de traduccion a XML
    json.trad => element.trad
    element.trad => object.trad | array.trad
    array.trad => "<array>" element-list.trad "</array>" | "<array></array>"

    element-list.trad => element.trad element-list'.trad
    element-list'.trad => element.trad element-list'.trad | ε

    object.trad => "<object>" attributes-list.trad "</object>" | "<object></object>"

    attributes-list.trad => attribute.trad attributes-list'.trad
    attributes-list'.trad => attribute.trad attributes-list'.trad | ε

    attribute.trad => "<" attribute-name.trad ">" attribute-value.trad "</" attribute-name.trad ">"
    attribute-name.trad => string.trad
    attribute-value.trad => element.trad | string | number | true | false | null.trad
    null.trad => ε

     */


    private static List<Token> tokens;
    private static int indiceActual = 0;
    private static String tokenActual = null;

    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\Matias\\Desktop\\fuente.txt";
        try {
            String input = Utils.fileContentToString(inputFilePath); // Carga el contenido del archivo JSON
            tokens = AnalizadorLexico.getTokensWithLexema(input);   // Genera la lista de tokens
            json();
        } catch (Exception e) {
            e.printStackTrace();
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
        if(indiceActual < tokens.size()) tokenActual = tokens.get(indiceActual).getTipo();
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
        System.out.print("<item>");
        element();
        System.out.print("</item>");
        elementListPrima();
    }

    private static void elementListPrima() throws Exception{
        if (tokenActual.equals("COMA")) {
            match("COMA");
            System.out.print("<item>");
            element();
            System.out.print("</item>");
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
        int indexNode = indiceActual;
        System.out.print("<");
        attributeName();
        System.out.print(">");
        match("DOS_PUNTOS");
        attributeValue();
        System.out.print("</" + tokens.get(indexNode).getValor().replace("\"", "") + ">");
    }

    private static void attributeName() throws Exception{
        match("STRING");
        System.out.print(tokens.get(indiceActual - 1).getValor().replace("\"",""));
    }

    private static void attributeValue() throws Exception {
        switch (tokenActual){
            case "L_CORCHETE", "L_LLAVE":
                element();
                break;
            case "STRING":
                match("STRING");
                System.out.print(tokens.get(indiceActual-1).getValor());
                break;
            case "LITERAL_NUM":
                match("LITERAL_NUM");
                System.out.print(tokens.get(indiceActual-1).getValor());
                break;
            case "PR_TRUE":
                match("PR_TRUE");
                System.out.print(tokens.get(indiceActual-1).getValor());
                break;
            case "PR_FALSE":
                match("PR_FALSE");
                System.out.print(tokens.get(indiceActual-1).getValor());
                break;
            case "PR_NULL":
                match("PR_NULL");
                break;
            default:
                throw new Exception("Elemento no esperado");
        }
    }

}