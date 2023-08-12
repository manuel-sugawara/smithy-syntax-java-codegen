package mx.sugus.javapoet;

public final class ElseStatementSpec implements SyntaxNode {

    private final SyntaxNode body;

    ElseStatementSpec(SyntaxNode body) {
        this.body = body;
    }

    private ElseStatementSpec(Builder builder) {
        this.body = builder.toBlockStatement();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("else ");
        body.emit(writer);
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        var writer = new CodeWriter(out);
        this.emit(writer);
        return out.toString();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, ElseStatementSpec> {

        Builder() {
        }

        @Override
        public ElseStatementSpec build() {
            return new ElseStatementSpec(this);
        }
    }
}
