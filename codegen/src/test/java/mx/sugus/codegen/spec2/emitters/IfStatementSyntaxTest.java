package mx.sugus.codegen.spec2.emitters;

import org.junit.jupiter.api.Test;

class IfStatementSyntaxTest {

    @Test
    public void test0() {
        var ifStatement = IfStatement.builder("foo == bar")
                                     .addStatement("index++")
                                     .addStatement("System.out.printf($S)", "Hello world")
                                     .orElse()
                                     .addStatement("index--")
                                     .addStatement("System.out.printf($S)", "Hello America")
                                     .build();
        System.out.printf("=========\n%s", ifStatement.toString());
    }

}