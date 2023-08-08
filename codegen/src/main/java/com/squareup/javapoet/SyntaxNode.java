package com.squareup.javapoet;

public interface SyntaxNode {

    void emit(CodeWriter writer);
}
