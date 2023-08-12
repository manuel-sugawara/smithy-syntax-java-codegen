package com.squareup.javapoet;

import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class MethodSpecTest {

    @Test
    public void test0() {
        var method = MethodSpec.methodBuilder("toString")
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .beginIfStatement("value == null")
            .ifStatement("anotherValue != null", ifBody -> ifBody.addStatement("return anotherValue"))
            .addStatement("return $S", "value is null")
            .elseStatement()
            .addStatement("return value + $S", "Value IS NOT null")
            .endIfStatement()
            .build();

        System.out.printf("=========================\n%s\n", method.toString());
    }
}