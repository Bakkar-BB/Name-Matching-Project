package comparateur;

import javax.swing.plaf.PanelUI;

public class Levenshtein implements  ComparateurChaine{
    @Override
    public double comparer(String s1, String s2){
        if (s1 == null || s2 == null) return 0.0;
        if (s1.isEmpty() && s2.isEmpty()) {
            return 1.0;
        }

    }
}
