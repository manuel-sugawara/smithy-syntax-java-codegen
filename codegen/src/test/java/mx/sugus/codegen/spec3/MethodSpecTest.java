package mx.sugus.codegen.spec3;

import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class MethodSpecTest {

    @Test
    public void test0() {
        MethodSpec spec = new MethodSpec("addInteger")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Integer.class, "left")
            .addParameter(Integer.class, "right")
            .returns(Integer.class)
            .beginIfStatement("left == null")
            .beginIfStatement("right == null")
            .addStatement("return null")
            .elseStatement()
            .addStatement("return right")
            .endIfStatement()
            .endIfStatement()
            .addStatement("return left + right");

        System.out.printf("====================\n%s\n", spec.build().toString());
    }

    @Test
    public void test2() {
        MethodSpec spec = new MethodSpec("addInteger")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Integer.class, "left")
            .addParameter(Integer.class, "right")
            .returns(Integer.class)
            .ifStatement("left == null", ifBody ->
                ifBody.ifStatement("right == null",
                                   innerIfBody ->
                                       innerIfBody.addStatement("return null"),
                                   innerElseBody ->
                                       innerElseBody.addStatement("return right")
                )
            )
            .addStatement("return left + right");

        System.out.printf("====================\n%s\n", spec.build().toString());
    }
}