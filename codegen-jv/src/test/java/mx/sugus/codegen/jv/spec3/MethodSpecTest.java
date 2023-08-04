package mx.sugus.codegen.jv.spec3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.spec3.syntax.FormatExpression;
import mx.sugus.codegen.jv.spec3.syntax.LiteralExpression;
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

    @Test
    public void test3() {
        MethodSpec spec = new MethodSpec("readInputStream");
        spec.addModifiers(Modifier.PUBLIC);
        spec.addParameter(InputStream.class, "input");
        spec.returns(String.class);
        spec.ifStatement("input == null", ifBody ->
            ifBody.addStatement("throw new NullPointerException($S)", "input")
        );
        spec.addStatement("String result");
        spec.beginTryStatement();
        {
            spec.addStatement("var bytes = input.readAllBytes()");
            spec.addStatement("result = new String(bytes, $T.UTF_8)", StandardCharsets.class);
        }
        spec.beginCatchStatement(LiteralExpression.create("IOException e"));
        {
            spec.addStatement("throw new RuntimeException(e)");
        }
        spec.beginFinallyStatement();
        {
            spec.beginTryStatement();
            spec.addStatement("input.close()");
            spec.beginCatchStatement(FormatExpression.create("$T e", IOException.class));
            spec.endTryStatement();
        }
        spec.endTryStatement();

        System.out.printf("====================\n%s\n", spec.build().toString());
    }


    @Test
    public void test4() {
        MethodSpec spec = new MethodSpec("readInputStream");
        spec.addModifiers(Modifier.PUBLIC);
        spec.addParameter(InputStream.class, "input");
        spec.returns(String.class);
        spec.ifStatement("input == null", ifBody ->
            ifBody.addStatement("throw new NullPointerException($S)", "input")
        );
        spec.addStatement("String result");
        spec.beginTryStatement();
        {
            spec.addStatement("var bytes = input.readAllBytes()");
            spec.addStatement("result = new String(bytes, $T.UTF_8)", StandardCharsets.class);
        }
        spec.beginCatchStatement(LiteralExpression.create("IOException e"));
        {
            spec.addStatement("throw new RuntimeException(e)");
        }
        spec.beginFinallyStatement();
        {
            spec.tryStatement(tryBody -> spec.addStatement("input.close()"),
                              FormatExpression.create("$T e", IOException.class),
                              catchBody -> {});
        }
        spec.endTryStatement();

        System.out.printf("====================\n%s\n", spec.build().toString());
    }
}