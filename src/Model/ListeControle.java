package Model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class ListeControle {
    public final String nomFichier;
    public final List<EntreeList> entrees;

    public ListeControle(String nomFichier, List<EntreeList> entrees) {
        this.nomFichier = nomFichier;
        this.entrees = List.copyOf(entrees);
    }

    public static ListeControle chargerDepuisCSV(String chemin) {
        List<EntreeList> entrees = new ArrayList<>();

        if (!Files.exists(Paths.get(chemin))) {
            System.out.println("Fichier introuvable !");
            return new ListeControle(chemin, entrees);
        }

        List<String> lignes = Files.readAllLines(Paths.get(chemin));

        if (lignes.size() <= 1) {
            return new ListeControle(chemin, entrees);
        }

        for (int i = 1; i < lignes.size(); i++) { // skip header
            String ligne = lignes.get(i).trim();
            String[] col = ligne.split(",", -1);

            if (col.length >= 3) {
                entrees.add(new EntreeList(
                        col[0].trim(),
                        col[1].trim(),
                        col[2].trim()
                ));
            }
        }

        return new ListeControle(chemin, entrees);
    }

}
