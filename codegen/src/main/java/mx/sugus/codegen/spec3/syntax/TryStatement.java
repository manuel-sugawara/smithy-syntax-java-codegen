package mx.sugus.codegen.spec3.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public class TryStatement implements SyntaxNode {

    private final BlockStatement tryBody;
    private final SyntaxNode resources;
    private final List<CatchClause> catchClauses;
    private final FinallyClause finallyClause;

    public TryStatement(Builder builder) {
        this.tryBody = Objects.requireNonNull(builder.tryBody);
        this.resources = builder.resources;
        this.catchClauses = List.copyOf(builder.catchClauses);
        this.finallyClause = builder.finallyClause;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting("try ");
        tryBody.emitInline(writer);
        for (var catchClause : catchClauses) {
            catchClause.emitInline(writer);
        }
        if (finallyClause != null) {
            finallyClause.emit(writer);
        } else {
            writer.writeWithNoFormatting("");
        }
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitTryStatement(this);
    }

    @Override
    public String toString() {
        var writer = new CodegenWriter("<none>");
        emit(writer);
        return writer.toString();
    }

    public static class Builder {
        private SyntaxNode resources;
        private BlockStatement tryBody;

        private List<CatchClause> catchClauses = new ArrayList<>();
        private FinallyClause finallyClause;

        Builder() {
        }

        public Builder tryBody(BlockStatement body) {
            this.tryBody = body;
            return this;
        }

        public Builder resources(SyntaxNode resources) {
            this.resources = resources;
            return this;
        }

        public Builder finallyClause(FinallyClause finallyClause) {
            this.finallyClause = finallyClause;
            return this;
        }

        public Builder catchClause(CatchClause catchClause) {
            this.catchClauses.add(catchClause);
            return this;
        }

        public TryStatement build() {
            return new TryStatement(this);
        }

    }
}
