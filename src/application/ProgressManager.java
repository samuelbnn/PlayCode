package application;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProgressManager 
{
    private static final String FILE_PATH = Costanti.PATH_FILE_PROGRESSI;

   public static void saveProgress(String exercise, Map<String, List<String>> progressState) 
   {
    String user = Session.getCurrentUser();
    List<String> updatedLines = new ArrayList<>();
    boolean userFound = false;

    // --- 1) Prepara il nuovo blocco formattato correttamente
    String pState = convertToRG(progressState.getOrDefault("Principiante", Collections.emptyList()));
    String iState = convertToRG(progressState.getOrDefault("Intermedio",  Collections.emptyList()));
    String aState = convertToRG(progressState.getOrDefault("Avanzato",     Collections.emptyList()));
    // include sempre { … } e la virgola alla fine
    String newBlock = String.format("{%s [Principiante(%s)] [Intermedio(%s)] [Avanzato(%s)]},",
                                    exercise, pState, iState, aState);

    // --- 2) Leggi riga per riga e ricostruisci
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith(user + ",")) {
                // riga di un altro utente: la tengo com’è
                updatedLines.add(line);
            } else {
                // riga dell'utente corrente: rimuovo blocco vecchio di questo exercise
                String regex = "\\{" + Pattern.quote(exercise) + "[^\\}]*\\},?";
                String withoutOld = line.replaceAll(regex, "");
                // tolgo eventuali virgole o spazi finali
                withoutOld = withoutOld.replaceAll("[,\\s]+$", "");
                // spezzetto utente vs resto
                String prefix = user + ",";
                String rest = "";
                if (withoutOld.length() > prefix.length()) {
                    rest = withoutOld.substring(prefix.length()).trim();
                }
                // ricostruisco con un singolo spazio dopo la virgola
                String rebuilt = prefix + " " + rest;
                // se non c'era alcun blocco, rest era vuoto => evito doppie virgole
                if (!rest.isEmpty()) {
                    rebuilt += ", ";
                }
                // aggiungo il nuovo blocco
                updatedLines.add(rebuilt + newBlock);
                userFound = true;
            }
        }
    } catch (IOException e) {
        // file non esiste o errore: creeremo una nuova riga
    }

    // se l'utente non c'era, lo aggiungo da zero
    if (!userFound) {
        updatedLines.add(String.format("%s, %s", user, newBlock));
    }

    // --- 3) Riscrivo completamente il file
    try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, false))) {
        for (String l : updatedLines) {
            writer.println(l);
        }
    } catch (IOException e) {
        System.err.println("Errore nel salvataggio del progresso: " + e.getMessage());
    }
}



    public static Map<String, List<String>> loadProgress(String user, String exercise) {
        Map<String, List<String>> progressState = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Estrai username e contenuto progressi
                int commaIndex = line.indexOf(',');
                if (commaIndex == -1) continue;

                String fileUser = line.substring(0, commaIndex).trim();
                if (!fileUser.equals(user)) continue;

                String rest = line.substring(commaIndex + 1).trim();
                // Trova l'indice dell'esercizio richiesto
                int exIdx = rest.indexOf(exercise);
                if (exIdx == -1) continue;

                // Individua il blocco { ... } che incapsula l'esercizio
                int startIdx = rest.lastIndexOf('{', exIdx);
                int endIdx = findMatchingBrace(rest, startIdx);
                if (startIdx != -1 && endIdx != -1) {
                    // Estraggo il contenuto interno del blocco (senza le graffe)
                    String block = rest.substring(startIdx + 1, endIdx).trim();
                    // Trova la parte a partire da [Principiante]
                    int progIdx = block.indexOf("[Principiante");
                    if (progIdx != -1) {
                        String progressData = block.substring(progIdx);
                        try {
                            progressState = parseProgressi(progressData);
                        } catch (Exception e) {
                            System.err.println("Formato dei dati di progresso non valido: " + e.getMessage());
                        }
                        return progressState;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante il caricamento del progresso: " + e.getMessage());
        }

        // Se non trovato, inizializza vuoto
        progressState.put("Principiante", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Intermedio",  new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Avanzato",    new ArrayList<>(Collections.nCopies(5, "")));
        return progressState;
    }

    /**
     * Trova l'indice della graffa di chiusura corrispondente alla startIndex
     */
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
        return -1;
    }

    public static Map<String, List<String>> parseProgressi(String progressi) {
        // Inizializza con 5 valori vuoti per ciascun livello
        Map<String, List<String>> progressState = new LinkedHashMap<>();
        progressState.put("Principiante", new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Intermedio",  new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put("Avanzato",    new ArrayList<>(Collections.nCopies(5, "")));

        // Pattern per catturare [Livello(...)], con o senza spazio
        Pattern livelloPattern = Pattern.compile("\\[\\s*([^\\(]+?)\\s*\\(([^\\)]*?)\\)\\]"
                                               + "");
        Matcher livelloMatcher = livelloPattern.matcher(progressi);

        while (livelloMatcher.find()) {
            String livello = livelloMatcher.group(1).trim();
            String[] risposte = livelloMatcher.group(2).split(";");
            // Rimuove spazi e mantiene esattamente 5 elementi
            List<String> cleaned = new ArrayList<>();
            for (int i = 0; i < risposte.length && i < 5; i++) {
                cleaned.add(risposte[i].trim());
            }
            // Se meno di 5, riempi con stringhe vuote
            while (cleaned.size() < 5) {
                cleaned.add("");
            }
            if (progressState.containsKey(livello)) {
                progressState.put(livello, cleaned);
            }
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
