package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Objects;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class FinallyClause implements SyntaxNode {
    private final BlockStatement finallyBody;

    FinallyClause(Builder builder) {
        this.finallyBody = Objects.requireNonNull(builder.finallyBody);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting("finally ");
        finallyBody.emit(writer);
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitFinallyClause(this);
    }

    @Override
    public String toString() {
        var writer = new CodegenWriter("<none>");
        emit(writer);
        return writer.toString();
    }

    public static class Builder {
        private BlockStatement finallyBody;

        public Builder() {
        }


        public Builder body(BlockStatement catchBody) {
            this.finallyBody = catchBody;
            return this;
        }

        public FinallyClause build() {
            return new FinallyClause(this);
        }
    }

}
