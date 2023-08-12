package mx.sugus.javapoet;

import java.util.Objects;

public final class CatchClauseSpec implements SyntaxNode {
    private final SyntaxNode catchParameter;
    private final BlockStatementSpec body;

    private CatchClauseSpec(Builder builder) {
        this.catchParameter = Objects.requireNonNull(builder.catchParameter);
        this.body = builder.toBlockStatement();
    }

    public static Builder builder(SyntaxNode catchParameter) {
        return new Builder(catchParameter);
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("catch (");
        catchParameter.emit(writer);
        writer.emit(") ");
        body.emit(writer);
    }

    @Override
    public void emitInline(CodeWriter writer) {
        writer.emit("catch (");
        catchParameter.emit(writer);
        writer.emit(") ");
        body.emitInline(writer);
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, CatchClauseSpec> {
        private SyntaxNode catchParameter;

        public Builder(SyntaxNode catchParameter) {
            this.catchParameter = catchParameter;
        }

        @Override
        public CatchClauseSpec build() {
            return new CatchClauseSpec(this);
        }
    }
}
