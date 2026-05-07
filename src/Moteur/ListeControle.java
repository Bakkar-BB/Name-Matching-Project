package Moteur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListeControle {
    public final String nomFichier;
    public final List<EntreeList> entrees;

    public ListeControle(String nomFichier, List<EntreeList> entrees) {
        this.nomFichier = nomFichier;
        this.entrees = List.copyOf(entrees);
    }

    public static ListeControle chargerDepuisCSV(String chemin) throws IOException {
        List<EntreeList> entrees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            br.readLine(); // skip header
            String ligne;
            while ((ligne = br.readLine()) != null) {
                String[] col = ligne.trim().split(",", -1);
                if (col.length >= 3)
                    entrees.add(new EntreeList(col[0].trim(), col[1].trim(), col[2].trim()));
            }
        }
        return new ListeControle(chemin, entrees);
    }
}
