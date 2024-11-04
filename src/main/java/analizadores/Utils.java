package analizadores;

import java.io.*;

public class Utils {

    public static String fileContentToString(String filePath) throws Exception {
        StringBuilder inputContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            inputContent.append(line).append("\n");
        }
        return inputContent.toString();
    }

    public static void escribirArchivo(String outputFilePath, String outputContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write(outputContent);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
