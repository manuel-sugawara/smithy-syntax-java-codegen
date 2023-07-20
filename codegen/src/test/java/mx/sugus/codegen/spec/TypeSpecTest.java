package mx.sugus.codegen.spec;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.writer.CodegenWriter;
import org.junit.jupiter.api.Test;

public class TypeSpecTest {

    @Test
    public void test0() {
        var spec = TypeSpec.classBuilder("HelloWorld")
                           .addModifiers(Modifier.PUBLIC)
                           .addField(FieldSpec.builder(String.class, "name")
                                              .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                              .build())
                           .addMethod(MethodSpec.constructorBuilder()
                                                .addParameter(String.class, "name")
                                                .addStatement("this.name = name")
                                                .build())
                           .addMethod(MethodSpec.methodBuilder("sayHello")
                                                .returns(void.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .addStatement("$T.out.println($S + name)", System.class, "Hello ")
                                                .build())
                           .addMethod(MethodSpec.methodBuilder("sayHelloTo")
                                                .returns(void.class)
                                                .addModifiers(Modifier.PUBLIC)
                                                .addParameter(String.class, "name")
                                                .ifStatement("this.name.equals(name)", ifBody -> {
                                                    ifBody.addStatement("$T.out.println($S + name)", System.class, "Hello ");
                                                }, elseBody -> {
                                                    elseBody.addStatement("$T.out.println($S + this.name + $S + name)",
                                                                          System.class,
                                                                          "Hello ", ", and ");
                                                })
                                                .build())
                           .build();

        var writer = new CodegenWriter("mx.sugus.example");
        spec.emit(writer);
        System.out.println(writer.toString());

    }
}
