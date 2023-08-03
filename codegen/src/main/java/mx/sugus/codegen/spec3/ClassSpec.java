package mx.sugus.codegen.spec3;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.spec3.syntax.ClassBody;
import mx.sugus.codegen.spec3.syntax.ClassField;
import mx.sugus.codegen.spec3.syntax.ClassSyntax;
import mx.sugus.codegen.spec3.syntax.FormatExpression;
import mx.sugus.codegen.spec3.syntax.LiteralExpression;
import mx.sugus.codegen.spec3.syntax.SyntaxNode;

public class ClassSpec {
    private final ClassSyntax.Builder classBuilder;
    private final ClassBody.Builder bodyBuilder = ClassBody.builder();

    ClassSpec(String name) {
        classBuilder = ClassSyntax.builder(name);
    }

    public ClassSpec addField(Object type, String name) {
        bodyBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.toSymbol(type))
                                       .name(name)
                                       .build());
        return this;
    }

    public ClassSpec addField(Object type, String name, String initializer) {
        bodyBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.toSymbol(type))
                                       .name(name)
                                       .initializer(LiteralExpression.create(initializer))
                                       .build());
        return this;
    }

    public ClassSpec addField(Object type, String name, String format, Object... args) {
        bodyBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.toSymbol(type))
                                       .name(name)
                                       .initializer(FormatExpression.create(format, args))
                                       .build());
        return this;
    }

    public ClassSpec addField(Object type, String name, SyntaxNode initializer) {
        bodyBuilder.addField(ClassField.builder()
                                       .addModifier(Modifier.PRIVATE)
                                       .addModifier(Modifier.FINAL)
                                       .type(SymbolConstants.toSymbol(type))
                                       .name(name)
                                       .initializer(initializer)
                                       .build());
        return this;
    }

    public ClassSpec addField(ClassField field) {
        bodyBuilder.addField(field);
        return this;
    }


}
