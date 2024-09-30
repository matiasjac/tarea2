/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tarea1;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author Matias
 */
public class Tarea1 {
      
       // Mapa para almacenar los tipos de token junto con sus patrones
    private static final Map<String, String> tokenPatterns = new HashMap<>();

    static {
        tokenPatterns.put("\\[", "L_CORCHETE");
        tokenPatterns.put("\\]", "R_CORCHETE");
        tokenPatterns.put("\\{", "L_LLAVE");
        tokenPatterns.put("\\}", "R_LLAVE");
        tokenPatterns.put(",", "COMA");
        tokenPatterns.put(":", "DOS_PUNTOS");
        tokenPatterns.put("\".*?\"", "STRING"); // Para cadenas
        tokenPatterns.put("[0-9]+(\\.[0-9]+)?([eE][+\\-]?[0-9]+)?", "LITERAL_NUM"); // Para números
        tokenPatterns.put("true|TRUE", "PR_TRUE");
        tokenPatterns.put("false|FALSE", "PR_FALSE");
        tokenPatterns.put("null|NULL", "PR_NULL");
    }

    public static void main(String[] args) {

        String inputFilePath = "C:\\Users\\Matias\\Desktop\\fuente.txt";
        String outputFilePath = "C:\\Users\\Matias\\Desktop\\output.txt";

         try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            StringBuilder inputContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                inputContent.append(line).append("\n");
            }

            String outputContent = replaceTokens(inputContent.toString());
            escribirArchivo(outputFilePath, outputContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String replaceTokens(String input) throws Exception {
        StringBuilder replacedContent = new StringBuilder();

        // Usar una expresión regular para encontrar todos los tokens válidos en el input
        String regex = String.join("|", tokenPatterns.keySet());
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        int lastEnd = 0;

        while (matcher.find()) {
            
            // para agregar los espacios o saltos de lineas 
            // que estan entre la ultima verificacion de token 
            // y el nuevo token
            replacedContent.append(input, lastEnd, matcher.start());

            // obtenemos lo que debemos buscar para obtener el token correspondiente
            String busqueda = matcher.group();

            String token = obtenerToken(busqueda);
            
            // agregamos un espacio por si haya mas de un token en la linea
            replacedContent.append(token);
            
            lastEnd = matcher.end();
        }

        // Añadir cualquier texto que quede después del último token
        replacedContent.append(input.substring(lastEnd));

        return replacedContent.toString().trim();
    }

    private static String obtenerToken(String token) throws Exception {
        for (Map.Entry<String, String> entry : tokenPatterns.entrySet()) {
            String pattern = entry.getKey();
            // Verificar si el token coincide con el patrón
            if (token.matches(pattern)) {
                return entry.getValue();
            }
        }
        throw new Exception("Error léxico. Token " + token + "invalido");
    }

    private static void escribirArchivo(String outputFilePath, String outputContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write(outputContent);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
