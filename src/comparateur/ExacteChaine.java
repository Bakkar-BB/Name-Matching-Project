package comparateur;

public class ExacteChaine implements ComparateurChaine{
    @Override
    public double comparer(String s1, String s2){
        if (s1 == null || s2==null) {
            return 0.0;
        }
        return s1.equalsIgnoreCase(s2) ? 1.0 : 0.0;
    }

}
