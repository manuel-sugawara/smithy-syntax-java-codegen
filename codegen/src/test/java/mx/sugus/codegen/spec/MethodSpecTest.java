package mx.sugus.codegen.spec;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.writer.CodegenWriter;
import org.junit.jupiter.api.Test;

class MethodSpecTest {

    @Test
    public void test1() {
        var spec = MethodSpec.methodBuilder("main")
                             .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                             .returns(void.class)
                             .addParameter(String[].class, "args")
                             .ifStatement("args.length > 0", ifBody -> {
                                 ifBody.addStatement("$T.out.printf($S, args[0])", System.class, "Hello \"%s\" from "
                                                                                                 + "SmithyJava!\n");
                             }, elseBody -> {
                                 elseBody.addStatement("$T.out.printf($S, args[1])", System.class, "and you \"%s\" from "
                                                                                                   + "SmithyJava!\n");
                             })
                             .startControlFlow("if (args.length > 0)")
                             .addStatement("$T.out.printf($S, args[0])", System.class, "Hello \"%s\" from SmithyJava!\n")
                             .startControlFlow("if (args.length > 1)")
                             .addStatement("$T.out.printf($S, args[1])", System.class, "and you \"%s\" from SmithyJava!\n")
                             .endControlFlow()
                             .nextControlFlow("else")
                             .addStatement("$T.out.println($S)", System.class, "Hello from SmithyJava!")
                             .endControlFlow()
                             .build();

        var writer = new CodegenWriter("mx.sugus.example");
        spec.emit(writer);
        System.out.println(writer.toString());
    }


    @Test
    public void test2() {
        var writer = new CodegenWriter("mx.sugus.example");
        writer.openBlock("open {");
        writer.setNewline(";\n");
        writer.write("foo");
        writer.write("foo\nbar");
        writer.write("bar");
        writer.writeWithNoFormatting("");
        writer.writeWithNoFormatting("baz");
        writer.setNewline("\n");
        writer.closeBlock("}");
        writer.write("foo2");
        writer.write("bar2");
        System.out.println(writer.toString());
    }

}