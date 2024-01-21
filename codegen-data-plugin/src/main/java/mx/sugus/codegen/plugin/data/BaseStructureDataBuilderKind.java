package mx.sugus.codegen.plugin.data;

public enum BaseStructureDataBuilderKind {

    PLAIN_USE_ONCE("plain-use-once"),
    USE_REFERENCE("use-reference");

    private final String value;
    BaseStructureDataBuilderKind(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
