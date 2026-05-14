package model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ListeControle {
    private final String chemin;
    private final List<EntreeList> entrees;

    public ListeControle(String chemin, List<EntreeList> entrees) {
        this.chemin = chemin;
        this.entrees = List.copyOf(entrees);
    }

    public static ListeControle chargerDepuisCSV(String chemin) {
        List<EntreeList> entrees = new ArrayList<>();
        try {
            List<String> lignes = Files.readAllLines(Paths.get(chemin));
            for (int i = 1; i < lignes.size(); i++) {
                String[] cols = lignes.get(i).split(",", 3);
                if (cols.length >= 3) {
                    entrees.add(new EntreeList(cols[0].trim(), cols[1].trim(), cols[2].trim()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lecture du fichier de contrôle : " + e.getMessage());
        }
        return new ListeControle(chemin, entrees);
    }

    public List<EntreeList> getEntrees() {
        return entrees;
    }
}
