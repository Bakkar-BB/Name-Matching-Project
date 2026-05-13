package livraison;

import model.Nom;
import model.ResultatComparaison;
import java.util.List;

public class LivreurConsole implements Livreur {

    @Override
    public void livrer(List<ResultatComparaison> resultats) {
        if (resultats == null || resultats.isEmpty()) {
            System.out.println("=== Aucun résultat trouvé ===");
            return;
        }
        System.out.println("=== Résultats ===");
        for (ResultatComparaison r : resultats) {
            System.out.println(r); // uses ResultatComparaison.toString()
        }
    }

    @Override
    public void livrerAvecDetails(Nom nom, List<ResultatComparaison> resultats) {
        if (resultats == null || resultats.isEmpty()) {
            System.out.println("=== Aucun résultat pour : " + nom.getNomBrut() + " ===");
            return;
        }
        System.out.println("=== Résultats pour : " + nom.getNomBrut() + " ===");
        for (ResultatComparaison r : resultats) {
            System.out.println("  Nom    : " + r.nomTrouve);
            System.out.println("  Score  : " + String.format("%.2f", r.score));
            System.out.println("  Source : " + r.source);
            System.out.println("  --------");
        }
    }
}
