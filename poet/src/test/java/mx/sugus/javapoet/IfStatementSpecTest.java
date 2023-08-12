package mx.sugus.javapoet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class IfStatementSpecTest {

    @Test
    public void testSimpleIf() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginIfStatement("foo == null")
            .addStatement("return null")
            .endIfStatement()
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
    public void testSimpleIfFunctionalStyle() {
        var spec = MethodSpec
            .methodBuilder("test")
            .ifStatement("foo == null", ifBody -> {
                ifBody.addStatement("return null");
            })
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
    public void testSimpleIfElse() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginIfStatement("foo == null")
            .addStatement("return null")
            .elseStatement()
            .addStatement("return foo + $S", " is not null")
            .endIfStatement()
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

    @Test
    public void testSimpleIfElseFunctionalStyle() {
        var spec = MethodSpec
            .methodBuilder("test")
            .ifStatement("foo == null", ifBody -> {
                ifBody.addStatement("return null");
            }, elseBody -> {
                elseBody.addStatement("return foo + $S", " is not null");
            })
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

    @Test
    public void testMultiArmIf() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginIfStatement("color.equals($S)", "red")
            .addStatement("return Colors.RED")
            .elseIfStatement("color.equals($S)", "blue")
            .addStatement("return Colors.BLUE")
            .elseIfStatement("color.equals($S)", "white")
            .addStatement("return Colors.WHITE")
            .elseStatement()
            .addStatement("return Colors.UNKNOWN")
            .endIfStatement()
            .build();

        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 if (color.equals("red")) {
                                   return Colors.RED;
                                 } else if (color.equals("blue")) {
                                   return Colors.BLUE;
                                 } else if (color.equals("white")) {
                                   return Colors.WHITE;
                                 } else {
                                   return Colors.UNKNOWN;
                                 }
                               }
                               """));

    }

    @Test
    public void elseWithoutIfThrows() {
        assertThrows(IllegalStateException.class, () -> {
            MethodSpec
                .methodBuilder("test")
                .addStatement("foobar()")
                .elseStatement()
                .addStatement("bar = foo()")
                .build();
        });
    }

    @Test
    public void elseIfWithoutIfThrows() {
        assertThrows(IllegalStateException.class, () -> {
            MethodSpec
                .methodBuilder("test")
                .addStatement("foobar()")
                .elseIfStatement("foo == null")
                .addStatement("bar = foo()")
                .build();
        });
    }

    @Test
    public void nonTerminatedIfThrows() {
        assertThrows(IllegalStateException.class, () -> {
            MethodSpec
                .methodBuilder("test")
                .beginIfStatement("foo == null")
                .addStatement("bar = foo()")
                .build();
        });
    }

}