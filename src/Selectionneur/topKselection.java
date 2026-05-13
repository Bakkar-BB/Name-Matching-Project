package Selectionneur;

import Model.ResultatComparaison;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TopKSelection implements Selectionneur {
    private final int k;
    public TopKSelection(int k) { this.k = k; }

    @Override
    public List<ResultatComparaison> filtrer(List<ResultatComparaison> resultats) {
        return resultats.stream()
               .sorted(Comparator.comparingDouble(r -> r.getScore()).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }
}
