package application;

import java.util.Objects;

public class Esercizio 
{
    String titolo;
    Enum grado;
    String codice;
    String domanda;
    String[] risposte;
    int indiceCorretta;

    public boolean isAnswered = false;  // Indica se l'utente ha risposto correttamente

    public Esercizio(String titolo, Enum grado, String codice, String domanda, String[] risposte, int indiceCorretta) 
    {
        this.titolo = titolo;
        this.grado = grado;
        this.codice = codice;
        this.domanda = domanda;
        this.risposte = risposte;
        this.indiceCorretta = indiceCorretta;
    }

    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof Esercizio)) 
            return false;
        Esercizio that = (Esercizio) o;
        return Objects.equals(codice, that.codice);
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(codice);
    }
}