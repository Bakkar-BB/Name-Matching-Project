package selectionneur;

import model.ResultatComparaison;
import java.util.List;

public interface Selectionneur {
    List<ResultatComparaison> filtrer(List<ResultatComparaison> resultat);
}
