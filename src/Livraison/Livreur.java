package Livraison;

import Moteur.Nom;
import Moteur.ResultatComparaison;

import java.util.List;

public interface Livreur {
    void livrer(List<ResultatComparaison> resultats);
    void livrerAvecDetails(Nom nom, List<ResultatComparaison> resultats);
}
