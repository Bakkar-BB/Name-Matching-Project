package selectionneur;

import model.ResultatComparaison;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TopKSelection implements Selectionneur {
    private final int k;

    public TopKSelection(int k) {
        this.k = k;
    }

    @Override
    public List<ResultatComparaison> filtrer(List<ResultatComparaison> resultat) {
        if (resultat == null) return List.of();
        return resultat.stream()
                .sorted(Comparator.comparingDouble(ResultatComparaison::getScore).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }
}
