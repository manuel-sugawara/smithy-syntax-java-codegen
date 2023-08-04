package mx.sugus.codegen.jv.spec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import mx.sugus.codegen.jv.spec3.syntax.BlockStatement;
import mx.sugus.codegen.jv.spec3.syntax.FormatStatement;
import mx.sugus.codegen.jv.spec3.syntax.LiteralExpression;
import mx.sugus.codegen.jv.spec3.syntax.LiteralStatement;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;

@SuppressWarnings("unchecked")
public abstract class AbstractBlock<B extends AbstractBlock<B, T>, T extends SyntaxNode> {
    private final Deque<AbstractBlock<?, ?>> state = new ArrayDeque<>();
    protected List<SyntaxNode> contents = new ArrayList<>();

    AbstractBlock() {
        state.push(this);
    }

    public B addStatement(String statement) {
        return addStatement(LiteralStatement.create(statement));
    }

    public B addStatement(String format, Object... args) {
        return addStatement(FormatStatement.create(format, args));
    }

    public B addStatement(SyntaxNode node) {
        assert state.peekFirst() != null;
        assert node != null;
        state.peekFirst().contents.add(node);
        return (B) this;
    }

    // --- If Statements --
    public B beginIfStatement(SyntaxNode condition) {
        state.push(new IfStatementBuilder(condition));
        return (B) this;
    }

    public B beginIfStatement(String condition) {
        return beginIfStatement(LiteralExpression.create(condition));
    }

    public B elseStatement() {
        var ifStatement = popExpecting(IfStatementBuilder.class);
        this.state.push(new ElseStatementBuilder(ifStatement));
        return (B) this;
    }

    public B endIfStatement() {
        var last = popExpecting(IfStatementBuilder.class, ElseStatementBuilder.class);
        return addStatement(last.build());
    }

    public B ifStatement(String condition, Consumer<AbstractBlock<B, T>> ifBody) {
        beginIfStatement(condition);
        ifBody.accept(this);
        return endIfStatement();
    }

    public B ifStatement(String condition, Consumer<AbstractBlock<B, T>> ifBody, Consumer<AbstractBlock<B, T>> elseBody) {
        beginIfStatement(condition);
        ifBody.accept(this);
        elseStatement();
        elseBody.accept(this);
        return endIfStatement();
    }

    // --- For statement
    public B beginForStatement(SyntaxNode initializer) {
        state.push(new ForStatementBuilder(initializer));
        return (B) this;
    }

    public B beginForStatement(String initializer) {
        return beginForStatement(LiteralExpression.create(initializer));
    }

    public B endForStatement() {
        var last = popExpecting(ForStatementBuilder.class);
        return addStatement(last.build());
    }

    public B forStatement(String initializer, Consumer<AbstractBlock<B, T>> forBody) {
        beginForStatement(initializer);
        forBody.accept(this);
        return endForStatement();
    }

    // -- try-catch
    public B beginTryStatement(SyntaxNode resources) {
        state.push(new TryStatementBuilder(resources));
        return (B) this;
    }

    public B beginTryStatement() {
        state.push(new TryStatementBuilder());
        return (B) this;
    }

    public B beginCatchStatement(SyntaxNode catchParameter) {
        var last = peekExpecting(TryStatementBuilder.class, CatchClauseBuilder.class);
        if (last instanceof TryStatementBuilder t) {
            state.push(t.addCatch(catchParameter));
        } else {
            state.pop();
            var tryStatement = peekExpecting(TryStatementBuilder.class);
            state.push(tryStatement.addCatch(catchParameter));
        }
        return (B) this;
    }

    public B beginFinallyStatement() {
        var last = peekExpecting(TryStatementBuilder.class, CatchClauseBuilder.class);
        TryStatementBuilder tryStatement;
        if (last instanceof TryStatementBuilder t) {
            tryStatement = t;
        } else {
            state.pop();
            tryStatement = peekExpecting(TryStatementBuilder.class);
        }
        state.push(tryStatement.addFinally());
        return (B) this;
    }

    public B endTryStatement() {
        var last = popExpecting(TryStatementBuilder.class, CatchClauseBuilder.class, FinallyClauseBuilder.class);
        if (!(last instanceof TryStatementBuilder)) {
            last = popExpecting(TryStatementBuilder.class);
        }
        return addStatement(last.build());
    }

    public B tryStatement(
        Consumer<AbstractBlock<B, T>> tryBody,
        SyntaxNode catchParameter,
        Consumer<AbstractBlock<B, T>> catchBody
    ) {
        beginTryStatement();
        tryBody.accept(this);
        beginCatchStatement(catchParameter);
        catchBody.accept(this);
        return endTryStatement();
    }

    public B tryStatement(
        Consumer<AbstractBlock<B, T>> tryBody,
        String catchParameter,
        Consumer<AbstractBlock<B, T>> catchBody
    ) {
        return tryStatement(tryBody, LiteralExpression.create(catchParameter), catchBody);
    }

    // -- Utils
    <E> E popExpecting(Class<E> clazz) {
        var last = state.pop();
        if (!clazz.isInstance(last)) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz.getSimpleName() + ", but got "
                                            + "instead: " +
                                            last.getClass().getSimpleName());
        }
        return (E) last;
    }

    AbstractBlock<?, ?> popExpecting(
        Class<? extends AbstractBlock<?, ?>> clazz0,
        Class<? extends AbstractBlock<?, ?>> clazz1
    ) {
        var last = state.pop();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getSimpleName() +
                                            " or " + clazz1.getSimpleName());
        }
        return last;
    }

    AbstractBlock<?, ?> popExpecting(
        Class<? extends AbstractBlock<?, ?>> clazz0,
        Class<? extends AbstractBlock<?, ?>> clazz1,
        Class<? extends AbstractBlock<?, ?>> clazz2
    ) {
        var last = state.pop();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last) || clazz2.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getSimpleName() +
                                            ", but got instead: " + last.getClass().getSimpleName());
        }
        return last;
    }

    <E> E peekExpecting(Class<E> clazz) {
        var last = state.peekFirst();
        if (!clazz.isInstance(last)) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz.getSimpleName());
        }
        return (E) last;
    }

    AbstractBlock<?, ?> peekExpecting(
        Class<? extends AbstractBlock<?, ?>> clazz0,
        Class<? extends AbstractBlock<?, ?>> clazz1
    ) {
        var last = state.peekFirst();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getSimpleName() +
                                            " or " + clazz1.getSimpleName() + ", but got instead: " +
                                            last.getClass().getSimpleName());
        }
        return last;
    }

    BlockStatement toBlockStatement() {
        return BlockStatement.builder()
                             .addStatements(contents)
                             .build();
    }

    public abstract T build();
}
