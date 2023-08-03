package mx.sugus.codegen.spec3;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.spec3.syntax.ClassBody;
import mx.sugus.codegen.spec3.syntax.ClassField;
import mx.sugus.codegen.spec3.syntax.ClassSyntax;
import org.junit.jupiter.api.Test;

class ClassSyntaxTest {

    @Test
    public void test0() {
        ClassSyntax.Builder classBuilder = ClassSyntax.builder("MyStructure");
        classBuilder.addModifier(Modifier.PUBLIC);
        ClassBody.Builder bodyBuilder = ClassBody.builder();
        bodyBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.fromClass(String.class))
                                       .name("name")
                                       .build());
        bodyBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.fromClass(int.class))
                                       .name("age")
                                       .build());
        classBuilder.body(bodyBuilder.build());
        System.out.printf("========================\n%s\n", classBuilder.build().toString());

    }

}