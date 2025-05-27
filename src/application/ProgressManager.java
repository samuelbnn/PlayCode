package application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProgressManager 
{
    //#region PROGRESSI.CSV

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
    try (BufferedReader reader = new BufferedReader(new FileReader(Costanti.PATH_FILE_PROGRESSI))) {
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
    try (PrintWriter writer = new PrintWriter(new FileWriter(Costanti.PATH_FILE_PROGRESSI, false))) {
        for (String l : updatedLines) {
            writer.println(l);
        }
    } catch (IOException e) {
        System.err.println("Errore nel salvataggio del progresso: " + e.getMessage());
    }
}



    public static Map<String, List<String>> loadProgress(String user, String exercise) {
        Map<String, List<String>> progressState = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Costanti.PATH_FILE_PROGRESSI))) {
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
            if (tacca.contains("#2ECC71")) 
            {
                result.append("G;");
            } 
            else if (tacca.contains("#E74C3C")) 
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

    //#endregion PROGRESSI.CSV


    //#region RISULTATI.CSV
    public static void salvaRisultatoCSV(String esercizio, String livelloCorrente) 
    {
        String utente = Session.getCurrentUser();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 1) Calcola i count di G dai progressi:
        int countPrincipiante = 0, countIntermedio = 0, countAvanzato = 0;
        try (Scanner sc = new Scanner(new File(Costanti.PATH_FILE_PROGRESSI))) 
        {
            while (sc.hasNextLine()) 
            {
                String line = sc.nextLine();
                if (!line.startsWith(utente + ","))
                    continue;

                int start = line.indexOf("{" + esercizio);
                if (start < 0) 
                    break;

                int end = line.indexOf("}", start) + 1;
                String block = line.substring(start, end);

                Map<String, List<String>> prog = ProgressManager.parseProgressi(block);
                countPrincipiante = (int) prog.get("Principiante").stream().filter("G"::equals).count();
                countIntermedio = (int) prog.get("Intermedio").stream().filter("G"::equals).count();
                countAvanzato = (int) prog.get("Avanzato").stream().filter("G"::equals).count();
                break;
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return;
        }

        // 2) Prepara la nuova tupla solo per il livello corrente:
        String nuovaTupla;
        switch (livelloCorrente) 
        {
            case "Principiante":
                nuovaTupla = String.format("(Principiante;%d;%s)", countPrincipiante, timestamp);
                break;
            case "Intermedio":
                nuovaTupla = String.format("(Intermedio;%d;%s)",   countIntermedio,   timestamp);
                break;
            case "Avanzato":
                nuovaTupla = String.format("(Avanzato;%d;%s)",     countAvanzato,     timestamp);
                break;
            default:
                throw new IllegalArgumentException("Livello non valido: " + livelloCorrente);
        }

        // 3) Leggi e modifica risultati.csv in memoria
        Path path = Paths.get(Costanti.PATH_FILE_RISULTATI);
        List<String> lines;
        try 
        {
            lines = Files.exists(path)
                  ? Files.readAllLines(path, StandardCharsets.UTF_8)
                  : new ArrayList<>();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return;
        }

        boolean userFound = false;
        String esercizioEsc = Pattern.quote(esercizio);
        // regex per trovare il blocco esatto di questo esercizio, incluse le tuple già presenti
        Pattern blockPattern = Pattern.compile("(\\[" + esercizioEsc + "\\s*)((\\([^\\)]+\\)\\s*)*)(\\])");

        for (int i = 0; i < lines.size(); i++) 
        {
            String row = lines.get(i);
            if (!row.startsWith(utente + ",")) 
                continue;

            userFound = true;
            Matcher m = blockPattern.matcher(row);
            if (m.find()) 
            {
                String p1 = m.group(1); // [esercizio
                String tuples = m.group(2); // tutte le tuple già presenti
                String p4 = m.group(4); // ]

                // Rimuovi eventuale tupla già presente per questo livello
                StringBuilder nuoveTuple = new StringBuilder();
                boolean trovato = false;
                if (tuples != null) {
                    String[] tupleArr = tuples.trim().split("\\s+");
                    for (String t : tupleArr) {
                        if (!t.isBlank() && !t.startsWith("(" + livelloCorrente + ";")) {
                            nuoveTuple.append(t).append(" ");
                        } else if (t.startsWith("(" + livelloCorrente + ";")) {
                            trovato = true;
                        }
                    }
                }
                // Aggiungi la nuova tupla per il livello completato
                nuoveTuple.append(nuovaTupla).append(" ");

                String nuovoBlock = p1 + nuoveTuple.toString().trim() + p4;
                String nuovaRiga = m.replaceFirst(Matcher.quoteReplacement(nuovoBlock));
                lines.set(i, nuovaRiga);
            } 
            else 
            {
                // Se non c'è ancora il blocco per questo esercizio, aggiungilo solo con la tupla del livello completato
                String fallback = String.format(",[%s %s]", esercizio, nuovaTupla);
                lines.set(i, row + fallback);
            }
            break;
        }

        if (!userFound) 
        {
            // Nuovo utente: crea riga da zero solo con la tupla del livello completato
            lines.add(utente + String.format(",[%s %s]", esercizio, nuovaTupla));
        }

        // 4) Riscrivi il file
        try 
        {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    //#endregion RISULTATI.CSV

    //#region STATO.CSV

    public static void updateProgressBar(String titoloEsercizio, String livelloCorrente) 
    {
        String utente = Session.getCurrentUser();
        String statoDaInserire = titoloEsercizio + " - " + livelloCorrente;

        Path path = Paths.get(Costanti.PATH_FILE_STATO); // ad esempio "stati_livelli.csv"
        List<String> righe;
        try 
        {
            righe = Files.exists(path)
                  ? Files.readAllLines(path, StandardCharsets.UTF_8)
                  : new ArrayList<>();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return;
        }

        boolean trovatoUtente = false;

        for (int i = 0; i < righe.size(); i++) 
        {
            String riga = righe.get(i);
            if (!riga.startsWith(utente + ",")) 
                continue;

            trovatoUtente = true;

            // Ottieni tutte le coppie già presenti
            String[] parti = riga.split(",", 2);
            String[] coppie = (parti.length > 1) ? parti[1].split(",") : new String[0];

            List<String> nuovaLista = new ArrayList<>();
            boolean aggiornato = false;

            for (String coppia : coppie) 
            {
                String trim = coppia.trim();
                if (trim.startsWith(titoloEsercizio + " - ")) 
                {
                    // sostituisco con il nuovo livello
                    nuovaLista.add(statoDaInserire);
                    aggiornato = true;
                } 
                else 
                {
                    nuovaLista.add(trim);
                }
            }

            if (!aggiornato) 
            {
                nuovaLista.add(statoDaInserire);
            }

            // Ricostruisco la riga utente
            righe.set(i, utente + "," + String.join(",", nuovaLista));
            break;
        }

        if (!trovatoUtente) 
        {
            // Nuovo utente, nuova riga
            righe.add(utente + "," + statoDaInserire);
        }

        try 
        {
            Files.write(path, righe, StandardCharsets.UTF_8);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    //#endregion STATO.CSV
}