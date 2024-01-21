package mx.sugus.javapoet;

import java.util.ArrayDeque;
import java.util.Deque;

public final class IfStatementSpec implements SyntaxNode {
    private final SyntaxNode condition;
    private final SyntaxNode body;
    private final ElseStatementSpec elseStatement;

    private IfStatementSpec(Builder builder) {
        this.condition = builder.condition;
        this.body = builder.toBlockStatement();
        this.elseStatement = builder.elseStatement;
    }

    private IfStatementSpec(SyntaxNode condition, SyntaxNode body, ElseStatementSpec elseStatement) {
        this.condition = condition;
        this.body = body;
        this.elseStatement = elseStatement;
    }

    public static Builder builder(SyntaxNode condition) {
        return new Builder(condition);
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit("if (");
        condition.emit(writer);
        writer.emit(") ");
        if (elseStatement == null) {
            body.emit(writer);
        } else {
            body.emitInline(writer);
            elseStatement.emit(writer);
        }
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        var writer = new CodeWriter(out);
        this.emit(writer);
        return out.toString();
    }

    public static final class Builder extends AbstractBlockBuilder<Builder, IfStatementSpec> {
        private SyntaxNode condition;
        private ElseStatementSpec elseStatement;

        private Deque<ElseIfBuilder> elseIfBuilders = new ArrayDeque<>();
        private ElseStatementSpec.Builder elseStatementBuilder;


        Builder(SyntaxNode condition) {
            this.condition = condition;
        }

        public ElseIfBuilder addElseIf(SyntaxNode condition) {
            var elseIf = new ElseIfBuilder(condition);
            elseIfBuilders.push(elseIf);
            return elseIf;
        }

        public ElseStatementSpec.Builder addElse() {
            elseStatementBuilder = ElseStatementSpec.builder();
            return elseStatementBuilder;
        }

        @Override
        public IfStatementSpec build() {
            var lastElse = elseStatementBuilder != null ? elseStatementBuilder.build() : null;
            while (!elseIfBuilders.isEmpty()) {
                var elseIf = elseIfBuilders.pop();
                lastElse = new ElseStatementSpec(elseIf.addElse(lastElse).build());
            }
            return new IfStatementSpec(condition, this.toBlockStatement(), lastElse);
        }
    }

    public static final class ElseIfBuilder extends AbstractBlockBuilder<Builder, IfStatementSpec> {
        private SyntaxNode condition;
        private ElseStatementSpec elseStatement;

        private Deque<Builder> elseIfBuilders = new ArrayDeque<>();

        ElseIfBuilder(SyntaxNode condition) {
            this.condition = condition;
        }

        public ElseIfBuilder addElse(ElseStatementSpec elseStatement) {
            this.elseStatement = elseStatement;
            return this;
        }

        @Override
        public IfStatementSpec build() {
            return new IfStatementSpec(condition, this.toBlockStatement(), elseStatement);
        }
    }
}
