package com.squareup.javapoet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class AbstractControlFlowTest {

    @Test
    public void testIfUsingAbstractControlFlow() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginControlFlow("if (foo == null)")
            .addStatement("return null")
            .endControlFlow()
            .addStatement("return foo + $S", " is not null")
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 if (foo == null) {
                                   return null;
                                 }
                                 return foo + " is not null";
                               }
                               """));

    }

    @Test
    public void testIfElseUsingAbstractControlFlow() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginControlFlow("if (foo == null)")
            .addStatement("return null")
            .nextControlFlow("else")
            .addStatement("return foo + $S", " is not null")
            .endControlFlow()
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 if (foo == null) {
                                   return null;
                                 } else {
                                   return foo + " is not null";
                                 }
                               }
                               """));
    }


}