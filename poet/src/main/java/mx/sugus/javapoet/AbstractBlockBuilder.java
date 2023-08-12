package mx.sugus.javapoet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
abstract class AbstractBlockBuilder<B extends AbstractBlockBuilder<B, T>, T extends SyntaxNode> {
    private final Deque<AbstractBlockBuilder<?, ?>> state = new ArrayDeque<>();
    protected List<SyntaxNode> contents = new ArrayList<>();

    AbstractBlockBuilder() {
        state.push(this);
    }

    static final String reportMissingClosing(AbstractBlockBuilder<?, ?> last) {
        if (last instanceof IfStatementSpec.Builder ||
            last instanceof IfStatementSpec.ElseIfBuilder ||
            last instanceof ElseStatementSpec.Builder
        ) {
            return "missing endIfStatement()";
        }
        if (last instanceof TryStatementSpec.Builder ||
            last instanceof CatchClauseSpec.Builder ||
            last instanceof FinallyClauseSpec.Builder
        ) {
            return "missing endTryStatement()";
        }
        if (last instanceof ForStatementSpec.Builder) {
            return "missing endForStatement()";
        }

        return "missing closing element of: " + last.getClass().getName();
    }

    public B addStatement(String statement) {
        return addStatement(Statement.of(statement));
    }

    public B addStatement(String format, Object... args) {
        return addStatement(Statement.of(format, args));
    }

    public B addStatement(SyntaxNode node) {
        assert state.peekFirst() != null;
        assert node != null;
        state.peekFirst().contents.add(node);
        return (B) this;
    }

    // --- If Statements --
    public B beginIfStatement(SyntaxNode condition) {
        state.push(IfStatementSpec.builder(condition));
        return (B) this;
    }

    public B beginIfStatement(String condition) {
        return beginIfStatement(Expression.of(condition));
    }

    public B beginIfStatement(String condition, Object... args) {
        return beginIfStatement(Expression.of(condition, args));
    }

    public B elseIfStatement(SyntaxNode condition) {
        var stmt = peekExpecting(IfStatementSpec.Builder.class, IfStatementSpec.ElseIfBuilder.class);
        if (stmt instanceof IfStatementSpec.Builder ifStatement) {
            state.push(ifStatement.addElseIf(condition));
        } else {
            state.pop();
            var ifStatement = peekExpecting(IfStatementSpec.Builder.class);
            state.push(ifStatement.addElseIf(condition));
        }
        return (B) this;
    }

    public B elseIfStatement(String condition) {
        return elseIfStatement(Expression.of(condition));
    }

    public B elseIfStatement(String condition, Object... args) {
        return elseIfStatement(Expression.of(condition, args));
    }

    public B elseStatement() {
        var stmt = peekExpecting(IfStatementSpec.Builder.class, IfStatementSpec.ElseIfBuilder.class);
        if (stmt instanceof IfStatementSpec.ElseIfBuilder) {
            state.pop();
        }
        var ifStatement = peekExpecting(IfStatementSpec.Builder.class);
        state.push(ifStatement.addElse());
        return (B) this;
    }

    public B endIfStatement() {
        var last = popExpecting(IfStatementSpec.Builder.class,
                                IfStatementSpec.ElseIfBuilder.class,
                                ElseStatementSpec.Builder.class);
        if (!(last instanceof IfStatementSpec.Builder)) {
            last = popExpecting(IfStatementSpec.Builder.class);
        }
        return addStatement(last.build());
    }

    public B ifStatement(String condition, Consumer<AbstractBlockBuilder<B, T>> ifBody) {
        beginIfStatement(condition);
        ifBody.accept(this);
        return endIfStatement();
    }

    public B ifStatement(String condition, Consumer<AbstractBlockBuilder<B, T>> ifBody,
                         Consumer<AbstractBlockBuilder<B, T>> elseBody) {
        beginIfStatement(condition);
        ifBody.accept(this);
        elseStatement();
        elseBody.accept(this);
        return endIfStatement();
    }

    // --- For statement
    public B beginForStatement(SyntaxNode initializer) {
        state.push(ForStatementSpec.builder(initializer));
        return (B) this;
    }

    public B beginForStatement(String initializer) {
        return beginForStatement(Expression.of(initializer));
    }

    public B endForStatement() {
        var last = popExpecting(ForStatementSpec.Builder.class);
        return addStatement(last.build());
    }

    public B forStatement(String initializer, Consumer<AbstractBlockBuilder<B, T>> forBody) {
        beginForStatement(initializer);
        forBody.accept(this);
        return endForStatement();
    }

    public B forStatement(SyntaxNode initializer, Consumer<AbstractBlockBuilder<B, T>> forBody) {
        beginForStatement(initializer);
        forBody.accept(this);
        return endForStatement();
    }

    // -- try-catch
    public B beginTryStatement(SyntaxNode resources) {
        state.push(TryStatementSpec.builder(resources));
        return (B) this;
    }

    public B beginTryStatement() {
        state.push(TryStatementSpec.builder());
        return (B) this;
    }

    public B beginCatchStatement(SyntaxNode catchParameter) {
        var last = peekExpecting(TryStatementSpec.Builder.class, CatchClauseSpec.Builder.class);
        if (last instanceof TryStatementSpec.Builder t) {
            state.push(t.addCatch(catchParameter));
        } else {
            state.pop();
            var tryStatement = peekExpecting(TryStatementSpec.Builder.class);
            state.push(tryStatement.addCatch(catchParameter));
        }
        return (B) this;
    }

    public B beginCatchStatement(String format, Object... args) {
        return beginCatchStatement(Expression.of(format, args));
    }

    public B beginFinallyStatement() {
        var last = peekExpecting(TryStatementSpec.Builder.class, CatchClauseSpec.Builder.class);
        TryStatementSpec.Builder tryStatement;
        if (last instanceof TryStatementSpec.Builder t) {
            tryStatement = t;
        } else {
            state.pop();
            tryStatement = peekExpecting(TryStatementSpec.Builder.class);
        }
        state.push(tryStatement.addFinally());
        return (B) this;
    }

    public B endTryStatement() {
        var last = popExpecting(TryStatementSpec.Builder.class, CatchClauseSpec.Builder.class, FinallyClauseSpec.Builder.class);
        if (!(last instanceof TryStatementSpec.Builder)) {
            last = popExpecting(TryStatementSpec.Builder.class);
        }
        return addStatement(last.build());
    }

    public B tryStatement(
        Consumer<AbstractBlockBuilder<B, T>> tryBody,
        SyntaxNode catchParameter,
        Consumer<AbstractBlockBuilder<B, T>> catchBody,
        Consumer<AbstractBlockBuilder<B, T>> finallyBody
    ) {
        beginTryStatement();
        tryBody.accept(this);
        beginCatchStatement(catchParameter);
        catchBody.accept(this);
        beginFinallyStatement();
        finallyBody.accept(this);
        return endTryStatement();
    }

    public B tryStatement(
        Consumer<AbstractBlockBuilder<B, T>> tryBody,
        SyntaxNode catchParameter,
        Consumer<AbstractBlockBuilder<B, T>> catchBody
    ) {
        beginTryStatement();
        tryBody.accept(this);
        beginCatchStatement(catchParameter);
        catchBody.accept(this);
        return endTryStatement();
    }

    public B tryStatement(
        Consumer<AbstractBlockBuilder<B, T>> tryBody,
        Consumer<AbstractBlockBuilder<B, T>> finallyBody
    ) {
        beginTryStatement();
        tryBody.accept(this);
        beginFinallyStatement();
        finallyBody.accept(this);
        return endTryStatement();
    }

    public B tryStatement(
        Consumer<AbstractBlockBuilder<B, T>> tryBody,
        String catchParameter,
        Consumer<AbstractBlockBuilder<B, T>> catchBody
    ) {
        return tryStatement(tryBody, Expression.of(catchParameter), catchBody);
    }

    // --- Legacy B/C control flow
    public B beginControlFlow(SyntaxNode prefix) {
        state.push(AbstractControlFlow.builder(prefix));
        return (B) this;
    }

    public B beginControlFlow(String prefix) {
        return beginControlFlow(Expression.of(prefix));
    }

    public B beginControlFlow(String prefix, Object... args) {
        return beginControlFlow(Expression.of(prefix, args));
    }

    public B nextControlFlow(SyntaxNode prefix) {
        var stmt = peekExpecting(AbstractControlFlow.Builder.class, AbstractControlFlow.NextControlFlowBuilder.class);
        if (stmt instanceof AbstractControlFlow.Builder b) {
            state.push(b.addNext(prefix));
        } else {
            state.pop();
            var controlFlow = peekExpecting(AbstractControlFlow.Builder.class);
            state.push(controlFlow.addNext(prefix));
        }
        return (B) this;
    }

    public B nextControlFlow(String prefix) {
        return nextControlFlow(Expression.of(prefix));
    }

    public B nextControlFlow(String prefix, Object... args) {
        return nextControlFlow(Expression.of(prefix, args));
    }

    public B endControlFlow() {
        var last = popExpecting(AbstractControlFlow.Builder.class, AbstractControlFlow.NextControlFlowBuilder.class);
        if (!(last instanceof AbstractControlFlow.Builder)) {
            last = popExpecting(AbstractControlFlow.Builder.class);
        }
        return addStatement(last.build());
    }

    // -- Utils
    // TODO, Improve the wording, as "missing beginIfStatement()", instead of expecting class ...
    <E> E popExpecting(Class<E> clazz) {
        var last = state.pop();
        if (!clazz.isInstance(last)) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz.getName() + ", but got "
                                            + "instead: " +
                                            last.getClass().getName());
        }
        return (E) last;
    }

    AbstractBlockBuilder<?, ?> popExpecting(
        Class<? extends AbstractBlockBuilder<?, ?>> clazz0,
        Class<? extends AbstractBlockBuilder<?, ?>> clazz1
    ) {
        var last = state.pop();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getName() +
                                            " or " + clazz1.getName());
        }
        return last;
    }

    AbstractBlockBuilder<?, ?> popExpecting(
        Class<? extends AbstractBlockBuilder<?, ?>> clazz0,
        Class<? extends AbstractBlockBuilder<?, ?>> clazz1,
        Class<? extends AbstractBlockBuilder<?, ?>> clazz2
    ) {
        var last = state.pop();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last) || clazz2.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getName() +
                                            ", but got instead: " + last.getClass().getName());
        }
        return last;
    }

    <E> E peekExpecting(Class<E> clazz) {
        var last = state.peekFirst();
        if (!clazz.isInstance(last)) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz.getName());
        }
        return (E) last;
    }

    AbstractBlockBuilder<?, ?> peekExpecting(
        Class<? extends AbstractBlockBuilder<?, ?>> clazz0,
        Class<? extends AbstractBlockBuilder<?, ?>> clazz1
    ) {
        var last = state.peekFirst();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getName() +
                                            " or " + clazz1.getName() + ", but got instead: " +
                                            last.getClass().getName());
        }
        return last;
    }

    BlockStatementSpec toBlockStatement() {
        var last = state.peekFirst();
        if (last != this) {
            var errors = new ArrayList<String>();
            do {
                errors.add(reportMissingClosing(state.pop()));
            } while (state.peekFirst() != this);
            throw new IllegalStateException("Unterminated state: " + String.join(",", errors));
        }
        return BlockStatementSpec.builder()
                                 .addStatements(contents)
                                 .build();
    }

    public abstract T build();
}
