package mx.sugus.codegen.spec2;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.spec2.emitters.IfStatement;
import org.junit.jupiter.api.Test;

class MethodSyntaxSpecTest {

    @Test
    public void test() {
        var builder = MethodSpec.methodBuilder("toString")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(String.class);

        builder.ifStatement("wholeWorld", body ->
            body.addStatement("return $S", "Hello world!"));
        builder.addStatement("wholeWorld = !america");

        builder.addStatement(IfStatement
                                 .builder("america")
                                 .addStatement("System.out.printf($S)", "Hello America!")
                                 .orElse()
                                 .addStatement("System.out.printf($S)", "Hello USA!")
                                 .build());
        builder.addStatement(IfStatement
                                 .builder("!america")
                                 .addStatement("System.out.printf($S)", "Hello to the whole world!")
                                 .build())
            .addStatement("return $S", "Says hello to all");

        System.out.printf("%s\n", builder.build().toString());
    }

}