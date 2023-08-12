package com.squareup.javapoet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TryStatementSpecTest {

    @Test
    public void tryWithoutCatchOrFinallyThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            MethodSpec
                .methodBuilder("test")
                .beginTryStatement()
                .addStatement("foobar()")
                .addStatement("bar = foo()")
                .endTryStatement()
                .build();
        });
    }

    @Test
    public void tryWithNullResourcesThrows() {
        assertThrows(NullPointerException.class, () -> {
            MethodSpec
                .methodBuilder("test")
                .beginTryStatement(null)
                .addStatement("foobar()")
                .addStatement("bar = foo()")
                .endTryStatement()
                .build();
        });
    }

    @Test
    public void tryCatchBuilderWithExplicitBeingEnd() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginTryStatement()
            .addStatement("foobar()")
            .addStatement("bar = foo()")
            .beginCatchStatement("$T e", IOException.class)
            .addStatement("$T.out.println(e.toString())", System.class)
            .endTryStatement()
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 try {
                                   foobar();
                                   bar = foo();
                                 } catch (java.io.IOException e) {
                                   java.lang.System.out.println(e.toString());
                                 }
                               }
                               """));
    }

    @Test
    public void tryCatchBuilderWithFunctionalStyle() {
        var spec = MethodSpec
            .methodBuilder("test")
            .tryStatement(
                body -> {
                    body.addStatement("foobar()")
                        .addStatement("bar = foo()");
                }, Expression.of("$T e", IOException.class),
                catchBody -> {
                    catchBody.addStatement("$T.out.println(e.toString())", System.class);
                })
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 try {
                                   foobar();
                                   bar = foo();
                                 } catch (java.io.IOException e) {
                                   java.lang.System.out.println(e.toString());
                                 }
                               }
                               """));
    }

    @Test
    public void tryCatchFinallyBuilderWithExplicitBeingEnd() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginTryStatement()
            .addStatement("foobar()")
            .addStatement("bar = foo()")
            .beginCatchStatement("$T e", IOException.class)
            .addStatement("$T.out.println(e.toString())", System.class)
            .beginFinallyStatement()
            .addStatement("something.close()")
            .endTryStatement()
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 try {
                                   foobar();
                                   bar = foo();
                                 } catch (java.io.IOException e) {
                                   java.lang.System.out.println(e.toString());
                                 } finally {
                                   something.close();
                                 }
                               }
                               """));
    }

    @Test
    public void tryCatchFinallyBuilderWithFunctionalStyle() {
        var spec = MethodSpec
            .methodBuilder("test")
            .tryStatement(
                body -> {
                    body.addStatement("foobar()")
                        .addStatement("bar = foo()");
                }, Expression.of("$T e", IOException.class),
                catchBody -> {
                    catchBody.addStatement("$T.out.println(e.toString())", System.class);
                }, finallyBody -> {
                    finallyBody.addStatement("something.close()");
                })
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 try {
                                   foobar();
                                   bar = foo();
                                 } catch (java.io.IOException e) {
                                   java.lang.System.out.println(e.toString());
                                 } finally {
                                   something.close();
                                 }
                               }
                               """));
    }

    @Test
    public void tryFinallyBuilderWithExplicitBeingEnd() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginTryStatement()
            .addStatement("foobar()")
            .addStatement("bar = foo()")
            .beginFinallyStatement()
            .addStatement("something.close()")
            .endTryStatement()
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 try {
                                   foobar();
                                   bar = foo();
                                 } finally {
                                   something.close();
                                 }
                               }
                               """));
    }

    @Test
    public void tryFinallyBuilderWithFunctionalStyle() {
        var spec = MethodSpec
            .methodBuilder("test")
            .tryStatement(
                body -> {
                    body.addStatement("foobar()")
                        .addStatement("bar = foo()");
                }, finallyBody -> {
                    finallyBody.addStatement("something.close()");
                })
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 try {
                                   foobar();
                                   bar = foo();
                                 } finally {
                                   something.close();
                                 }
                               }
                               """));
    }

    @Test
    public void tryWithResourcesFinallyBuilderWithExplicitBeingEnd() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginTryStatement(Expression.of("var bar = getInputStream()"))
            .addStatement("foo = bar.read()")
            .beginFinallyStatement()
            .addStatement("bar.close()")
            .endTryStatement()
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 try (var bar = getInputStream()) {
                                   foo = bar.read();
                                 } finally {
                                   bar.close();
                                 }
                               }
                               """));
    }

}