package application;

import java.util.Objects;

public class Esercizio 
{
    String titolo;
    String livello;
    String codice;
    String domanda;
    String[] risposte;
    int indiceCorretta;
    public boolean isAnswered = false; // Nuovo campo per tracciare se la domanda è già stata risolta

    public Esercizio(String titolo, String livello, String codice, String domanda, String[] risposte, int indiceCorretta) 
    {
        this.titolo = titolo;
        this.livello = livello;
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