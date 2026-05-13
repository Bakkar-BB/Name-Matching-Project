package kyc.Pretraitement;

import kyc.model.Nom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Normalizes phonetic and transliteration variants of names.
 *
 * Problem it solves:
 *   Arabic, Hebrew, and other names transliterated to latin script
 *   have no single standard — the same name can appear many ways:
 *     MOHAMMED / MOHAMED / MUHAMMED / MOHAMAD → all collapse to MOHAMMAD
 *     YOUSSEF  / YOUSEF  / YUSUF             → all collapse to YUSUF
 *
 *   Without this step, Levenshtein and JaroWinkler catch some variants
 *   but not all — especially when the edit distance is large.
 *
 * Runs after Normalisation so tokens are already uppercase.
 *
 * Strategy: rule-based normalization using a canonical form map.
 * Each key is a known variant, the value is the canonical form.
 *
 * Pipeline position: after Normalisation, before Decomposeur
 *   Nettoyeur → SuppAcc → Normalisation → NormalisateurPhonetique → Decomposeur
 */
public class NormalisateurPhonetique implements Pretraitement {

    /**
     * Maps known transliteration variants to a single canonical form.
     * All keys and values are uppercase.
     *
     * Covers common Arabic, Hebrew, and cross-language variants
     * frequently found in international sanctions lists.
     */
    private static final Map<String, String> VARIANTES = Map.ofEntries(
            // Mohammed variants
            Map.entry("MOHAMMED",  "MOHAMMAD"),
            Map.entry("MOHAMED",   "MOHAMMAD"),
            Map.entry("MUHAMMED",  "MOHAMMAD"),
            Map.entry("MUHAMAD",   "MOHAMMAD"),
            Map.entry("MOHAMAD",   "MOHAMMAD"),
            Map.entry("MEHMED",    "MOHAMMAD"),

            // Youssef variants
            Map.entry("YOUSSEF",   "YUSUF"),
            Map.entry("YOUSEF",    "YUSUF"),
            Map.entry("YOUSUF",    "YUSUF"),
            Map.entry("YUSEF",     "YUSUF"),

            // Mustafa variants
            Map.entry("MUSTAFA",   "MOSTAFA"),
            Map.entry("MOUSTAFA",  "MOSTAFA"),
            Map.entry("MUSTAPHA",  "MOSTAFA"),

            // Omar variants
            Map.entry("OMER",      "OMAR"),
            Map.entry("UMAR",      "OMAR"),

            // Osama variants
            Map.entry("OSSAMA",    "OSAMA"),
            Map.entry("OUSAMA",    "OSAMA"),
            Map.entry("USAMA",     "OSAMA"),

            // Abdallah variants
            Map.entry("ABDALLAH",  "ABDULLAH"),
            Map.entry("ABDALLA",   "ABDULLAH"),
            Map.entry("ABDELAH",   "ABDULLAH"),

            // Ibrahim variants
            Map.entry("IBRAHIM",   "IBRAHIM"),
            Map.entry("EBRAHIM",   "IBRAHIM"),
            Map.entry("BRAHIM",    "IBRAHIM"),

            // Hasan variants
            Map.entry("HASSAN",    "HASAN"),
            Map.entry("HASSEN",    "HASAN"),

            // Hussain variants
            Map.entry("HUSSEIN",   "HUSSAIN"),
            Map.entry("HOSSEIN",   "HUSSAIN"),
            Map.entry("HUSAIN",    "HUSSAIN"),

            // Khadija variants
            Map.entry("KHADIJA",   "KHADIJA"),
            Map.entry("KHADIDJA",  "KHADIJA"),
            Map.entry("KHEDIJA",   "KHADIJA")
    );

    @Override
    public List<String> traiter(Nom nom) {
        List<String> tokens = nom.getNomPretraite().isEmpty()
                ? List.of(nom.getNomBrut().trim().toUpperCase().split("\\s+"))
                : nom.getNomPretraite();

        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            // Replace with canonical form if a variant is known, otherwise keep as is
            result.add(VARIANTES.getOrDefault(token, token));
        }
        return result;
    }
}