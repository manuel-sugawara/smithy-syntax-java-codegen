package com.squareup.javapoet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class IfStatementSpecTest {

    @Test
    public void test0() {
        var ifStatement = IfStatementSpec.builder(Expression.of("foo == null"))
            .addStatement("foo = new $T<>()", List.class);
        var ifElseStatement = ElseStatementSpec.builder(ifStatement)
            .addStatement("return $T.unmodifiableList(foo)", Collections.class)
            .build();
        System.out.printf("========================\n%s\n", ifElseStatement.toString());
    }
}