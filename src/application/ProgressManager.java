package application;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProgressManager 
{
    private static final String FILE_PATH = Costanti.PATH_FILE_PROGRESSI;

    public static void saveProgress(String user, String exercise, String level, Map<String, List<String>> progressState) 
    {
        List<String> updatedLines = new ArrayList<>();

        // Read existing lines and exclude the current user's progress for this exercise
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                if (!line.startsWith(user + "," + exercise)) 
                {
                    updatedLines.add(line);
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("File non trovato, verrà creato un nuovo file.");
        }

        // Convert progress state to R/G/empty
        String principianteState = convertToRG(progressState.getOrDefault("Principiante", Collections.emptyList()));
        String intermedioState = convertToRG(progressState.getOrDefault("Intermedio", Collections.emptyList()));
        String avanzatoState = convertToRG(progressState.getOrDefault("Avanzato", Collections.emptyList()));

        // Write the progress line without quotes around level names and remove unnecessary spaces
        updatedLines.add(String.format("%s,%s,{%s[Principiante(%s)][Intermedio(%s)][Avanzato(%s)]}", 
            user, exercise, exercise, principianteState, intermedioState, avanzatoState));

        // Write back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) 
        {
            for (String line : updatedLines) 
            {
                writer.println(line);
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Errore nel salvataggio del progresso: " + e.getMessage());
        }
    }

    public static Map<String, List<String>> loadProgress(String user, String exercise) 
    {
        Map<String, List<String>> progressState = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                // Adjust regex to correctly extract user, exercise, and progress
                Pattern pattern = Pattern.compile("^([^,]+),\\s*\\{([^\\[]+?)\\s*\\[.*\\]\\}\\s*$");
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) 
                {
                    String fileUser = matcher.group(1).trim();
                    String fileExercise = matcher.group(2).trim(); // Extract only the exercise name
                    if (fileUser.equals(user) && fileExercise.equals(exercise)) 
                    {
                        try 
                        {
                            // Extract progress data and parse it
                            String progressData = line.substring(line.indexOf("[Principiante"), line.lastIndexOf("]") + 1);
                            progressState = parseProgressi(progressData);
                        } 
                        catch (Exception e) 
                        {
                            System.err.println("Formato dei dati di progresso non valido: " + e.getMessage());
                        }
                        return progressState;
                    }
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Errore durante il caricamento del progresso: " + e.getMessage());
        }

        // Initialize empty progress if no data is found or format is invalid
        progressState.put("Principiante", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Intermedio", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Avanzato", new ArrayList<>(Collections.nCopies(5, "")));
        return progressState;
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
