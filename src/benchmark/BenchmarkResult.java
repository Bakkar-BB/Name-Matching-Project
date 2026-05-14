package benchmark;

public class BenchmarkResult {
    public final String generatorName;
    public final String comparateur1;
    public final String comparateur2;
    public final String selectionneur;
    public final long runtimeMs;
    public final long totalCandidates;
    public final long totalResults;
    public final int sanctionsSize;
    public final int querySize;
    public final String datasetName;

    public BenchmarkResult(String generatorName,
                           String comparateur1,
                           String comparateur2,
                           String selectionneur,
                           long runtimeMs,
                           long totalCandidates,
                           long totalResults,
                           int sanctionsSize,
                           int querySize,
                           String datasetName) {
        this.generatorName = generatorName;
        this.comparateur1 = comparateur1;
        this.comparateur2 = comparateur2;
        this.selectionneur = selectionneur;
        this.runtimeMs = runtimeMs;
        this.totalCandidates = totalCandidates;
        this.totalResults = totalResults;
        this.sanctionsSize = sanctionsSize;
        this.querySize = querySize;
        this.datasetName = datasetName;
    }

    public String toCsvLine() {
        return String.join(",",
                escape(generatorName),
                escape(comparateur1),
                escape(comparateur2),
                escape(selectionneur),
                Long.toString(runtimeMs),
                Long.toString(totalCandidates),
                Long.toString(totalResults),
                Integer.toString(sanctionsSize),
                Integer.toString(querySize),
                escape(datasetName)
        );
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        String escaped = text.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
