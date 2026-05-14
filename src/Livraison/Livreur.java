package livreur;

import java.util.List;
import model.Nom;
import model.ResultatComparaison;

public interface Livreur {
    void livrer(List<ResultatComparaison> resultats);
    void livrerAvecDetails(Nom nom, List<ResultatComparaison> resultats);
}
