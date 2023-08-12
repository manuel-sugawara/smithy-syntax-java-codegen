package com.squareup.javapoet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ForStatementSpecTest {

    @Test
    public void testEnhancedForStatementBeginEnd() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginForStatement(Expression.of("var bar : listOfBars"))
            .addStatement("$T.out.println(bar)", System.class)
            .endForStatement()
            .build();
        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 for (var bar : listOfBars) {
                                   java.lang.System.out.println(bar);
                                 }
                               }
                               """));

    }

    @Test
    public void testEnhancedForStatementLambda() {
        var spec = MethodSpec
            .methodBuilder("test")
            .forStatement("var bar : listOfBars", body -> {
                body.addStatement("$T.out.println(bar)", System.class);
            })
            .build();
        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 for (var bar : listOfBars) {
                                   java.lang.System.out.println(bar);
                                 }
                               }
                               """));

    }

    @Test
    public void testBasicForStatementBeginEnd() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginForStatement("var idx = 0; idx < listOfBars.size(); ++idx")
            .addStatement("var bar = listOfBars.get(idx)")
            .addStatement("$T.out.println(bar)", System.class)
            .endForStatement()
            .build();
        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 for (var idx = 0; idx < listOfBars.size(); ++idx) {
                                   var bar = listOfBars.get(idx);
                                   java.lang.System.out.println(bar);
                                 }
                               }
                               """));
    }

    @Test
    public void testBasicForStatementLambda() {
        var spec = MethodSpec
            .methodBuilder("test")
            .forStatement("var idx = 0; idx < listOfBars.size(); ++idx", body -> {
                body.addStatement("var bar = listOfBars.get(idx)")
                    .addStatement("$T.out.println(bar)", System.class);
            })
            .build();
        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 for (var idx = 0; idx < listOfBars.size(); ++idx) {
                                   var bar = listOfBars.get(idx);
                                   java.lang.System.out.println(bar);
                                 }
                               }
                               """));
    }

    @Test
    public void testThrowsWithNullInitializer() {
        assertThrows(NullPointerException.class, () -> {
            String init = null;
            MethodSpec
                .methodBuilder("test")
                .forStatement(init, body -> {
                    body.addStatement("var bar = listOfBars.get(idx)")
                        .addStatement("$T.out.println(bar)", System.class);
                })
                .build();

        });
    }

    @Test
    public void testThrowsWithNullInitializer2() {
        assertThrows(NullPointerException.class, () -> {
            SyntaxNode init = null;
            MethodSpec
                .methodBuilder("test")
                .forStatement(init, body -> {
                    body.addStatement("var bar = listOfBars.get(idx)")
                        .addStatement("$T.out.println(bar)", System.class);
                })
                .build();

        });
    }
}