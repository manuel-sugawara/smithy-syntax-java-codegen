package com.squareup.javapoet;

public interface SyntaxNode {

    void emit(CodeWriter writer);

    default void emitInline(CodeWriter writer) {
        emit(writer);
    }
}
