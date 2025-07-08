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

import javafx.css.CssMetaData;

public class ProgressManager 
{
    //#region PROGRESSI.CSV

   public static void saveProgress(String exercise, Map<String, List<String>> progressState) 
   {
        String user = Session.getCurrentUser();
        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;

        String pState = convertToRG(progressState.getOrDefault("Principiante", Collections.emptyList()));
        String iState = convertToRG(progressState.getOrDefault("Intermedio", Collections.emptyList()));
        String aState = convertToRG(progressState.getOrDefault("Avanzato", Collections.emptyList()));

        String newBlock = String.format("{%s [Principiante(%s)] [Intermedio(%s)] [Avanzato(%s)]},", exercise, pState, iState, aState);

        try (BufferedReader reader = new BufferedReader(new FileReader(Costanti.PATH_FILE_PROGRESSI))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                if (!line.startsWith(user + ",")) 
                {
                    updatedLines.add(line);
                } 
                else 
                {
                    String regex = "\\{" + Pattern.quote(exercise) + "[^\\}]*\\},?";
                    String withoutOld = line.replaceAll(regex, "");

                    // tolgo eventuali virgole o spazi finali
                    withoutOld = withoutOld.replaceAll("[,\\s]+$", "");

                    String prefix = user + ",";
                    String rest = "";
                    if (withoutOld.length() > prefix.length()) 
                    {
                        rest = withoutOld.substring(prefix.length()).trim();
                    }

                    String rebuilt = prefix + " " + rest;

                    if (!rest.isEmpty()) 
                    {
                        rebuilt += ", ";
                    }
                    
                    // aggiungo il nuovo blocco
                    updatedLines.add(rebuilt + newBlock);
                    userFound = true;
                }
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Errore durante la lettura del file progressi: " + e.getMessage());
        }

        // Aggiunta utente se non esiste
        if (!userFound) 
        {
            updatedLines.add(String.format("%s, %s", user, newBlock));
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(Costanti.PATH_FILE_PROGRESSI, false))) 
        {
            for (String l : updatedLines) 
            {
                writer.println(l);
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
        try (BufferedReader reader = new BufferedReader(new FileReader(Costanti.PATH_FILE_PROGRESSI)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                // Estrai username e contenuto progressi
                int commaIndex = line.indexOf(',');
                if (commaIndex == -1) 
                    continue;

                String fileUser = line.substring(0, commaIndex).trim();
                if (!fileUser.equals(user)) 
                    continue;

                String rest = line.substring(commaIndex + 1).trim();

                // Trova l'indice dell'esercizio richiesto
                int exIdx = rest.indexOf(exercise);
                if (exIdx == -1) 
                    continue;

                int startIdx = rest.lastIndexOf('{', exIdx);
                int endIdx = findMatchingBrace(rest, startIdx);
                if (startIdx != -1 && endIdx != -1) 
                {
                    // Estraggo il contenuto interno del blocco rimuovendo le graffe
                    String block = rest.substring(startIdx + 1, endIdx).trim();

                    // Trova la parte a partendo da --> [Principiante]
                    int progIdx = block.indexOf("[Principiante");
                    if (progIdx != -1) 
                    {
                        String progressData = block.substring(progIdx);
                        try 
                        {
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

        progressState.put(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put(Costanti.LIVELLO_AVANZATO, new ArrayList<>(Collections.nCopies(5, "")));
        return progressState;
    }

    /**
     * Trova l'indice della graffa di chiusura corrispondente alla startIndex
     */
    private static int findMatchingBrace(String text, int startIndex) 
    {
        int depth = 0;
        for (int i = startIndex; i < text.length(); i++) 
        {
            char c = text.charAt(i);
            if (c == '{') 
                depth++;
            else if (c == '}') 
            {
                depth--;
                if (depth == 0) 
                    return i;
            }
        }
        return -1;
    }

    public static Map<String, List<String>> parseProgressi(String progressi) 
    {
        // Inizializza con 5 'tacchette' vuote per ciascun livello
        Map<String, List<String>> progressState = new LinkedHashMap<>();
        progressState.put(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>(Collections.nCopies(5, "")));
        progressState.put(Costanti.LIVELLO_AVANZATO, new ArrayList<>(Collections.nCopies(5, "")));

        // Pattern per catturare [Livello(...)], con o senza spazio
        Pattern livelloPattern = Pattern.compile("\\[\\s*([^\\(]+?)\\s*\\(([^\\)]*?)\\)\\]"
                                               + "");
        Matcher livelloMatcher = livelloPattern.matcher(progressi);

        while (livelloMatcher.find()) 
        {
            String livello = livelloMatcher.group(1).trim();
            String[] risposte = livelloMatcher.group(2).split(";");

            // Rimuozione spazi e mantiene esattamente 5 elementi
            List<String> cleaned = new ArrayList<>();
            for (int i = 0; i < risposte.length && i < 5; i++) 
            {
                cleaned.add(risposte[i].trim());
            }

            while (cleaned.size() < 5) 
            {
                cleaned.add("");
            }
            if (progressState.containsKey(livello)) 
            {
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
            if (tacca.contains(Costanti.VERDE)) 
            {
                result.append("G;");
            } 
            else if (tacca.contains(Costanti.ROSSO)) 
            {
                result.append("R;");
            } 
            else 
            {
                result.append(";");
            }
        }

        return result.toString().replaceAll(";$", "");
    }

    //#endregion PROGRESSI.CSV

    //#region RISULTATI.CSV

    public static void salvaRisultati(String esercizio, String livelloCorrente, String timestamp_inizio) 
    {
        String utente = Session.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String timestamp_fine = now.format(formatter);

        String durataFormattata = "00:00:00";
        try 
        {
            // Estrazione data
            String tsPulito = timestamp_inizio.trim();
            if (tsPulito.contains(";")) 
            {
                String[] parti = tsPulito.split(";");
                tsPulito = parti[parti.length - 1].trim();
            }
            LocalDateTime inizio = LocalDateTime.parse(tsPulito, formatter);
            LocalDateTime fine = LocalDateTime.parse(timestamp_fine, formatter);

            long durataSecondi = java.time.Duration.between(inizio, fine).getSeconds();
            long ore = durataSecondi / 3600;
            long minuti = (durataSecondi % 3600) / 60;
            long secondi = durataSecondi % 60;
            durataFormattata = String.format("%02d:%02d:%02d", ore, minuti, secondi);
        } 
        catch (Exception e) 
        {
            System.err.println("Errore nel parsing del timestamp");
            durataFormattata = "00:00:00";
        }

        // Calcola delle tacchette verdi o G dai progressi.csv:
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
                countPrincipiante = (int) prog.get(Costanti.LIVELLO_PRINCIPIANTE).stream().filter("G"::equals).count();
                countIntermedio = (int) prog.get(Costanti.LIVELLO_INTERMEDIO).stream().filter("G"::equals).count();
                countAvanzato = (int) prog.get(Costanti.LIVELLO_AVANZATO).stream().filter("G"::equals).count();
                break;
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return;
        }

        // Prepara la nuova tupla solo per il livello corrente, aggiungendo ora e dataFine
        String nuovaTupla;
        switch (livelloCorrente) 
        {
            case Costanti.LIVELLO_PRINCIPIANTE:
                nuovaTupla = String.format("(" + Costanti.LIVELLO_PRINCIPIANTE + ";%d;%s;%s)", countPrincipiante, durataFormattata, timestamp_fine);
                break;
            case Costanti.LIVELLO_INTERMEDIO:
                nuovaTupla = String.format("(" + Costanti.LIVELLO_INTERMEDIO + ";%d;%s;%s)", countIntermedio, durataFormattata, timestamp_fine);
                break;
            case Costanti.LIVELLO_AVANZATO:
                nuovaTupla = String.format("(" + Costanti.LIVELLO_AVANZATO + ";%d;%s;%s)", countAvanzato, durataFormattata, timestamp_fine);
                break;
            default:
                throw new IllegalArgumentException("Livello non valido: " + livelloCorrente);
        }

        // Legge e modifica risultati.csv in memoria
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
                String tuples = m.group(2);
                String p4 = m.group(4); // ]

                // Rimuovi eventuale tupla già presente per questo livello
                StringBuilder nuoveTuple = new StringBuilder();
                boolean trovato = false;
                if (tuples != null) 
                {
                    String[] tupleArr = tuples.trim().split("\\s+");
                    for (String t : tupleArr) 
                    {
                        if (!t.isBlank() && !t.startsWith("(" + livelloCorrente + ";")) 
                        {
                            nuoveTuple.append(t).append(" ");
                        } 
                        else if (t.startsWith("(" + livelloCorrente + ";")) 
                        {
                            trovato = true;
                        }
                    }
                }

                // Aggiunta la nuova tupla per il livello completato
                nuoveTuple.append(nuovaTupla).append(" ");

                String nuovoBlock = p1 + nuoveTuple.toString().trim() + p4;
                String nuovaRiga = m.replaceFirst(Matcher.quoteReplacement(nuovoBlock));
                lines.set(i, nuovaRiga);
            } 
            else 
            {
                // Se non c'è ancora il blocco per questo esercizio lo aggiungo solo con la tupla del livello completato
                String fallback = String.format(",[%s %s]", esercizio, nuovaTupla);
                lines.set(i, row + fallback);
            }
            break;
        }

        if (!userFound) 
        {
            // Crea riga da zero solo con la tupla del livello completato
            lines.add(utente + String.format(",[%s %s]", esercizio, nuovaTupla));
        }

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

        Path path = Paths.get(Costanti.PATH_FILE_STATO);
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
            // Nuovo utente nuova riga
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

    public static List<String> translateTacche(List<String> tacche, int expectedSize) 
    {
        List<String> translatedTacche = new ArrayList<>();
        for (String tacca : tacche) 
        {
            if ("G".equals(tacca)) 
            {
                translatedTacche.add("-fx-background-color: " + Costanti.VERDE + ";");
            } 
            else if ("R".equals(tacca)) 
            {
                translatedTacche.add("-fx-background-color: " + Costanti.ROSSO + ";");
            } 
            else 
            {
                translatedTacche.add("");
            }
        }
        while (translatedTacche.size() < expectedSize) 
        {
            translatedTacche.add("");
        }
        return translatedTacche.subList(0, expectedSize);
    }

    public boolean isProgressoValido(String[] parts, String utente, String titolo) 
    {
        return parts.length >= 9 && parts[0].equals(utente) && parts[1].equals(titolo);
    }

    public List<String> normalizeTacche(String taccheString, int expectedSize) 
    {
        List<String> tacche = new ArrayList<>(Arrays.asList(taccheString.split(";")));
        while (tacche.size() < expectedSize) 
        {
            tacche.add(""); // Aggiungi tacche vuote se mancano
        }
        return tacche.subList(0, expectedSize); // Troncamento se ci sono più tacche del previsto
    }
}