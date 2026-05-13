package Selectionneur;
import java.util.List;
import Moteur.ResultatComparaison;
public  class SelectionAvancee implements Selectionneur{

    public final double seuil;
    public final String sourcePrioritaire;

    public SelectionAvancee(double seuil , String sourcePrioritaire){
        this.seuil=seuil;
        this.sourcePrioritaire=sourcePrioritaire;
    }



    @Override
    public List<ResultatComparaison> filtrer(List<ResultatComparaison> resultats) {
        if (resultats == null) return List.of();
        return resultats.stream()
                .filter(r -> r.estAuDessus(seuil))
                .sorted((a, b) -> {
                    if (a.source.equals(sourcePrioritaire) != b.source.equals(sourcePrioritaire))
                        return a.source.equals(sourcePrioritaire) ? -1 : 1;
                    return Double.compare(b.score, a.score);
                })
                .toList();
    }

}
