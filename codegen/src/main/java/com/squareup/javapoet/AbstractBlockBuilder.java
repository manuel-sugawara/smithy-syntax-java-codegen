package com.squareup.javapoet;

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

    public B addStatement(String statement) {
        return addStatement(CodeSnippet.of(statement));
    }

    public B addStatement(String format, Object... args) {
        return addStatement(CodeSnippet.of(format, args));
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
        return beginIfStatement(CodeSnippet.of(condition));
    }

    public B elseStatement() {
        var ifStatement = popExpecting(IfStatementSpec.Builder.class);
        this.state.push(ElseStatementSpec.builder(ifStatement));
        return (B) this;
    }

    public B endIfStatement() {
        var last = popExpecting(IfStatementSpec.Builder.class, ElseStatementSpec.Builder.class);
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
        return beginForStatement(CodeSnippet.of(initializer));
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
        String catchParameter,
        Consumer<AbstractBlockBuilder<B, T>> catchBody
    ) {
        return tryStatement(tryBody, CodeSnippet.of(catchParameter), catchBody);
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

    AbstractBlockBuilder<?, ?> popExpecting(
        Class<? extends AbstractBlockBuilder<?, ?>> clazz0,
        Class<? extends AbstractBlockBuilder<?, ?>> clazz1
    ) {
        var last = state.pop();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getSimpleName() +
                                            " or " + clazz1.getSimpleName());
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

    AbstractBlockBuilder<?, ?> peekExpecting(
        Class<? extends AbstractBlockBuilder<?, ?>> clazz0,
        Class<? extends AbstractBlockBuilder<?, ?>> clazz1
    ) {
        var last = state.peekFirst();
        if (!(clazz0.isInstance(last) || clazz1.isInstance(last))) {
            throw new IllegalStateException("Expected to have a class on top instanceof " + clazz0.getSimpleName() +
                                            " or " + clazz1.getSimpleName() + ", but got instead: " +
                                            last.getClass().getSimpleName());
        }
        return last;
    }

    BlockStatementSpec toBlockStatement() {
        return BlockStatementSpec.builder()
                                 .addStatements(contents)
                                 .build();
    }

    public abstract T build();
}
