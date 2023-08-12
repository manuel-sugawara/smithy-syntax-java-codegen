package mx.sugus.javapoet;

import java.util.ArrayDeque;
import java.util.Deque;

public final class AbstractControlFlow implements SyntaxNode {
    private final SyntaxNode prefix;
    private final BlockStatementSpec body;
    private final AbstractControlFlow next;

    private AbstractControlFlow(SyntaxNode prefix, BlockStatementSpec body, AbstractControlFlow next) {
        this.prefix = prefix;
        this.body = body;
        this.next = next;
    }

    public static Builder builder(SyntaxNode prefix) {
        return new Builder(prefix);
    }

    @Override
    public void emit(CodeWriter writer) {
        prefix.emit(writer);
        writer.emit(" ");
        if (next == null) {
            body.emit(writer);
        } else {
            body.emitInline(writer);
            next.emit(writer);
        }
    }

    public static class Builder extends AbstractBlockBuilder<Builder, AbstractControlFlow> {
        private final SyntaxNode prefix;
        private final Deque<NextControlFlowBuilder> nextFlows = new ArrayDeque<>();

        public Builder(SyntaxNode prefix) {
            this.prefix = prefix;
        }

        public NextControlFlowBuilder addNext(SyntaxNode prefix) {
            var next = new NextControlFlowBuilder(prefix);
            nextFlows.push(next);
            return next;
        }

        @Override
        public AbstractControlFlow build() {
            AbstractControlFlow next = null;
            while (!nextFlows.isEmpty()) {
                var nextFlow = nextFlows.pop();
                next = nextFlow.addNext(next).build();
            }
            return new AbstractControlFlow(prefix, toBlockStatement(), next);
        }
    }

    public static class NextControlFlowBuilder extends AbstractBlockBuilder<Builder, AbstractControlFlow> {
        private final SyntaxNode prefix;
        private AbstractControlFlow next;

        public NextControlFlowBuilder(SyntaxNode prefix) {
            this.prefix = prefix;
        }

        public NextControlFlowBuilder addNext(AbstractControlFlow next) {
            this.next = next;
            return this;
        }

        @Override
        public AbstractControlFlow build() {
            AbstractControlFlow next = null;
            return new AbstractControlFlow(prefix, toBlockStatement(), next);
        }
    }

}
