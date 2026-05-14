package selectionneur;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import model.ResultatComparaison;

public class SelectionAvancee implements Selectionneur {
    public final double seuil;
    public final String sourcePrioritaire;

    public SelectionAvancee(double seuil, String sourcePrioritaire) {
        this.seuil = seuil;
        this.sourcePrioritaire = sourcePrioritaire;
    }

    @Override
    public List<ResultatComparaison> filtrer(List<ResultatComparaison> resultat) {
        if (resultat == null) return List.of();
        return resultat.stream()
                .filter(r -> r.estAuDessus(seuil))
                .sorted(Comparator.comparing((ResultatComparaison r) -> !r.source.equals(sourcePrioritaire))
                        .thenComparing(Comparator.comparingDouble(ResultatComparaison::getScore).reversed()))
                .collect(Collectors.toList());
    }
}
