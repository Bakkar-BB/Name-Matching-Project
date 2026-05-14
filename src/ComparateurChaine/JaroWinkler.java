package comparateur;

public class JaroWinkler implements ComparateurChaine {
    private static final double P = 0.1;

    @Override
    public double comparer(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        double jaroScore = jaro(s1, s2);
        int prefix = commonPrefix(s1, s2, 4);
        return jaroScore + prefix * P * (1.0 - jaroScore);
    }

    private double jaro(String s1, String s2) {
        if (s1.isEmpty() && s2.isEmpty()) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        int matchWindow = Math.max(s1.length(), s2.length()) / 2 - 1;
        if (matchWindow < 0) matchWindow = 0;

        boolean[] matchedS1 = new boolean[s1.length()];
        boolean[] matchedS2 = new boolean[s2.length()];
        int matches = 0;

        for (int i = 0; i < s1.length(); i++) {
            int start = Math.max(0, i - matchWindow);
            int end = Math.min(i + matchWindow + 1, s2.length());
            for (int j = start; j < end; j++) {
                if (!matchedS2[j] && s1.charAt(i) == s2.charAt(j)) {
                    matchedS1[i] = true;
                    matchedS2[j] = true;
                    matches++;
                    break;
                }
            }
        }
        if (matches == 0) return 0.0;

        int transpositions = 0;
        int k = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (matchedS1[i]) {
                while (!matchedS2[k]) k++;
                if (s1.charAt(i) != s2.charAt(k)) transpositions++;
                k++;
            }
        }
        return (matches / (double) s1.length()
                + matches / (double) s2.length()
                + (matches - transpositions / 2.0) / matches) / 3.0;
    }

    private int commonPrefix(String s1, String s2, int max) {
        int limit = Math.min(max, Math.min(s1.length(), s2.length()));
        for (int i = 0; i < limit; i++) {
            if (s1.charAt(i) != s2.charAt(i)) return i;
        }
        return limit;
    }
}
