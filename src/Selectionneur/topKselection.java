package kyc.selection;

import kyc.model.ResultatComparaison;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TopKSelection implements Selectionneur {
    private final int k;
    public TopKSelection(int k) { this.k = k; }

    @Override
    public List<ResultatComparaison> filtrer(List<ResultatComparaison> resultats) {
        return resultats.stream()
                .sorted(Comparator.comparingDouble(ResultatComparaison::score).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }
}
