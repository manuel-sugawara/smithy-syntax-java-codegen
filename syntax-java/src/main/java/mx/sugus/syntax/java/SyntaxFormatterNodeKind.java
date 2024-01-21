package mx.sugus.syntax.java;

public enum SyntaxFormatterNodeKind {
    LITERAL("LITERAL"),

    STRING("STRING"),

    TYPE_NAME("TYPE_NAME"),

    NAME("NAME"),

    UNKNOWN_TO_VERSION(null);

    private final String value;

    private SyntaxFormatterNodeKind(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Use this in place of valueOf to convert the raw string returned by the service into the enum value.
     * @param value The string literal to convert to SyntaxFormatterNodeKind
     */
    public static SyntaxFormatterNodeKind fromValue(String value) {
        switch(value) {
            case "LITERAL":
                return LITERAL;
            case "STRING":
                return STRING;
            case "TYPE_NAME":
                return TYPE_NAME;
            case "NAME":
                return NAME;
            default:
                return UNKNOWN_TO_VERSION;
        }
    }
}
