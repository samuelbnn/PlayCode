package application;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProgressManager 
{
    private static final String FILE_PATH = Costanti.PATH_FILE_PROGRESSI;

   //public static void saveProgress(String user, String exercise, String level, Map<String, List<String>> progressState) 
   //{
   //    List<String> updatedLines = new ArrayList<>();

   //    // Read existing lines and exclude the current user's progress for this exercise
   //    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) 
   //    {
   //        String line;
   //        while ((line = reader.readLine()) != null) 
   //        {
   //            if (!line.startsWith(user + "," + exercise)) 
   //            {
   //                updatedLines.add(line);
   //            }
   //        }
   //    } 
   //    catch (IOException e) 
   //    {
   //        System.out.println("File non trovato, verrà creato un nuovo file.");
   //    }

   //    // Convert progress state to R/G/empty
   //    String principianteState = convertToRG(progressState.getOrDefault("Principiante", Collections.emptyList()));
   //    String intermedioState = convertToRG(progressState.getOrDefault("Intermedio", Collections.emptyList()));
   //    String avanzatoState = convertToRG(progressState.getOrDefault("Avanzato", Collections.emptyList()));

   //    // Write the progress line without quotes around level names and remove unnecessary spaces
   //    updatedLines.add(String.format("%s,%s,{%s[Principiante(%s)][Intermedio(%s)][Avanzato(%s)]}", 
   //        user, exercise, exercise, principianteState, intermedioState, avanzatoState));

   //    // Write back to the file
   //    try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) 
   //    {
   //        for (String line : updatedLines) 
   //        {
   //            writer.println(line);
   //        }
   //    } 
   //    catch (IOException e) 
   //    {
   //        System.err.println("Errore nel salvataggio del progresso: " + e.getMessage());
   //    }
   //}

    public void saveProgress(String titolo, Map<String, List<String>> statoTacche) 
    {
        String utente = Session.getCurrentUser();
        StringBuilder progressData = new StringBuilder();

        // Append progress for Trova l'errore
        progressData.append(" {" + titolo + "");
        progressData.append(" [Principiante (").append(String.join(";", convertToRG(statoTacche.getOrDefault("Principiante", new ArrayList<>())))).append(")]");
        progressData.append(" [Intermedio (").append(String.join(";", convertToRG(statoTacche.getOrDefault("Intermedio", new ArrayList<>())))).append(")]");
        progressData.append(" [Avanzato (").append(String.join(";", convertToRG(statoTacche.getOrDefault("Avanzato", new ArrayList<>())))).append(")]},");

        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(Costanti.PATH_FILE_PROGRESSI))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                if (line.startsWith(utente + ",")) 
                {
                    int closingBraceIndex = line.lastIndexOf("}");
                    if (closingBraceIndex != -1) 
                    {
                        // Insert new progress before the closing brace
                        String updatedLine = line.substring(0, closingBraceIndex) + line.substring(closingBraceIndex) + progressData;
                        updatedLines.add(updatedLine);
                    } 
                    else 
                    {
                        updatedLines.add(line); // Fallback in case of malformed line
                    }
                    userFound = true;
                } 
                else 
                {
                    updatedLines.add(line);
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("File non trovato, verrà creato un nuovo file.");
        }

        if (!userFound) 
        {
            updatedLines.add(utente + "," + progressData);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(Costanti.PATH_FILE_PROGRESSI, false))) 
        {
            for (String line : updatedLines) 
            {
                writer.println(line);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static Map<String, List<String>> loadProgress(String user, String exercise) {
        Map<String, List<String>> progressState = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int commaIndex = line.indexOf(',');
                if (commaIndex == -1) continue;
    
                String fileUser = line.substring(0, commaIndex).trim();
                String progressContent = line.substring(commaIndex + 1).trim();
    
                if (!fileUser.equals(user)) continue;
    
                // Cerca il nome dell'esercizio nella stringa
                int exerciseIndex = progressContent.indexOf(exercise);
                if (exerciseIndex == -1) continue;
    
                // Trova l'inizio del blocco {
                int startIndex = progressContent.lastIndexOf('{', exerciseIndex);
                // Trova la fine del blocco }
                int endIndex = findMatchingBrace(progressContent, startIndex);
    
                if (startIndex != -1 && endIndex != -1) {
                    String exerciseBlock = progressContent.substring(startIndex + 1, endIndex).trim();
                    try {
                        // Cerca la prima occorrenza di [Principiante ...] dentro il blocco
                        String progressData = exerciseBlock.substring(exerciseBlock.indexOf("[Principiante"));
                        progressState = parseProgressi(progressData);
                    } catch (Exception e) {
                        System.err.println("Formato dei dati di progresso non valido: " + e.getMessage());
                    }
                    return progressState;
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante il caricamento del progresso: " + e.getMessage());
        }
    
        // Initialize empty progress if no data is found or format is invalid
        progressState.put("Principiante", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Intermedio", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Avanzato", new ArrayList<>(Collections.nCopies(5, "")));
        return progressState;
    }
    private static int findMatchingBrace(String text, int startIndex) {
        int depth = 0;
        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1; // non trovato
    }

    public static Map<String, List<String>> parseProgressi(String progressi) 
    {
        Map<String, List<String>> progressState = new HashMap<>();
        Pattern livelloPattern = Pattern.compile("\\[([^\\]]+?) \\(([^\\)]*?)\\)\\]");
        Matcher livelloMatcher = livelloPattern.matcher(progressi);

        while (livelloMatcher.find()) 
        {
            String livello = livelloMatcher.group(1); // "Principiante", "Intermedio", "Avanzato"
            String[] risposte = livelloMatcher.group(2).split(";");
            progressState.put(livello, Arrays.asList(risposte));
        }

        return progressState;
    }

    private static String convertToRG(List<String> tacche) 
    {
        StringBuilder result = new StringBuilder();
        for (String tacca : tacche) 
        {
            if (tacca.contains("green")) 
            {
                result.append("G;");
            } 
            else if (tacca.contains("red")) 
            {
                result.append("R;");
            } 
            else 
            {
                result.append(";");
            }
        }
        return result.toString().replaceAll(";$", ""); // Remove trailing semicolon
    }
}
