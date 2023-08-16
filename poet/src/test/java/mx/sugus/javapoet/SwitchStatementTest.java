package mx.sugus.javapoet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class SwitchStatementTest {

    @Test
    public void test0() {
        var spec = MethodSpec
            .methodBuilder("test")
            .beginSwitchStatement(Expression.of("value"))
            .nextSwitchCase("$S", "foo")
            .addStatement("return FOO")
            .nextSwitchCase("$S", "bar")
            .addStatement("return BAR")
            .defaultSwitchCase()
            .addStatement("return UNKNOWN")
            .endSwitchStatement()
            .build();
        assertThat(spec.toString(),
                   equalTo("""
                               void test() {
                                 switch (value) {
                                   case "foo":
                                     return FOO;
                                   case "bar":
                                     return BAR;
                                   default:
                                     return UNKNOWN;
                                 }
                               }
                               """));
    }
}