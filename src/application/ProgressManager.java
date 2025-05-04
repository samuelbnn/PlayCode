package application;

import java.io.*;
import java.util.*;

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

        updatedLines.add(String.format("%s,%s,%s,%s,%s,%s", 
            user, exercise, level, principianteState, intermedioState, avanzatoState));

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
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) 
        {
            while (scanner.hasNextLine()) 
            {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 6 && parts[0].equals(user) && parts[1].equals(exercise)) 
                {
                    progressState.put("Principiante", normalizeTacche(parts[3], 5));
                    progressState.put("Intermedio", normalizeTacche(parts[4], 5));
                    progressState.put("Avanzato", normalizeTacche(parts[5], 5));
                    return progressState;
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Nessun progresso precedente trovato per l'utente " + user);
        }

        // Initialize empty progress if no data is found
        progressState.put("Principiante", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Intermedio", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Avanzato", new ArrayList<>(Collections.nCopies(5, "")));
        return progressState;
    }

    private static List<String> normalizeTacche(String taccheString, int expectedSize) 
    {
        List<String> tacche = new ArrayList<>(Arrays.asList(taccheString.split(";")));
        while (tacche.size() < expectedSize) 
        {
            tacche.add(""); // Add empty entries if missing
        }
        return tacche.subList(0, expectedSize); // Trim to the expected size
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
