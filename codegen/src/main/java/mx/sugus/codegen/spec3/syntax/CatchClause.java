package mx.sugus.codegen.spec3.syntax;

import java.util.Objects;
import mx.sugus.codegen.writer.CodegenWriter;

public final class CatchClause implements SyntaxNode {
    private final SyntaxNode catchParameter;
    private final BlockStatement catchBody;

    CatchClause(Builder builder) {
        this.catchParameter = Objects.requireNonNull(builder.catchParameter);
        this.catchBody = Objects.requireNonNull(builder.catchBody);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting("catch (");
        catchParameter.emit(writer);
        writer.writeInlineWithNoFormatting(") ");
        catchBody.emit(writer);
    }

    @Override
    public void emitInline(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting("catch (");
        catchParameter.emit(writer);
        writer.writeInlineWithNoFormatting(") ");
        catchBody.emitInline(writer);
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitCatchClause(this);
    }

    @Override
    public String toString() {
        var writer = new CodegenWriter("<none>");
        emit(writer);
        return writer.toString();
    }

    public static class Builder {
        private SyntaxNode catchParameter;
        private BlockStatement catchBody;

        public Builder() {
        }

        public Builder parameter(SyntaxNode catchParameter) {
            this.catchParameter = catchParameter;
            return this;
        }

        public Builder body(BlockStatement catchBody) {
            this.catchBody = catchBody;
            return this;
        }

        public CatchClause build() {
            return new CatchClause(this);
        }
    }
}
