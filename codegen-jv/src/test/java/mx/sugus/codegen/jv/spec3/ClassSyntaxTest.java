package mx.sugus.codegen.jv.spec3;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.SymbolConstants;
import mx.sugus.codegen.jv.spec3.syntax.ClassField;
import mx.sugus.codegen.jv.spec3.syntax.ClassSyntax;
import org.junit.jupiter.api.Test;

class ClassSyntaxTest {

    @Test
    public void test0() {
        ClassSyntax.Builder classBuilder = ClassSyntax.builder("MyStructure");
        classBuilder.addModifier(Modifier.PUBLIC);
        classBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.fromClass(String.class))
                                       .name("name")
                                       .build());
        classBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.fromClass(int.class))
                                       .name("age")
                                       .build());
        System.out.printf("========================\n%s\n", classBuilder.build().toString());

    }

}