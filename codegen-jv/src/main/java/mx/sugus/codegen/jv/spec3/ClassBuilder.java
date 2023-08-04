package mx.sugus.codegen.jv.spec3;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.SymbolConstants;
import mx.sugus.codegen.jv.spec3.syntax.ClassField;
import mx.sugus.codegen.jv.spec3.syntax.ClassSyntax;
import mx.sugus.codegen.jv.spec3.syntax.FormatExpression;
import mx.sugus.codegen.jv.spec3.syntax.LiteralExpression;
import mx.sugus.codegen.jv.spec3.syntax.Method;
import mx.sugus.codegen.jv.spec3.syntax.SyntaxNode;

public final class ClassBuilder {
    private final ClassSyntax.Builder classBuilder;

    ClassBuilder(String name) {
        classBuilder = ClassSyntax.builder(name);
    }

    public ClassBuilder addModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) {
            classBuilder.addModifier(modifier);
        }
        return this;
    }

    public ClassBuilder addField(Object type, String name) {
        classBuilder.addField(ClassField.builder()
                                        .addModifier(Modifier.PRIVATE)
                                        .addModifier(Modifier.FINAL)
                                        .type(SymbolConstants.toSymbol(type))
                                        .name(name)
                                        .build());
        return this;
    }

    public ClassBuilder addField(Object type, String name, String initializer) {
        classBuilder.addField(ClassField.builder()
                                        .addModifier(Modifier.PRIVATE)
                                        .addModifier(Modifier.FINAL)
                                        .type(SymbolConstants.toSymbol(type))
                                        .name(name)
                                        .initializer(LiteralExpression.create(initializer))
                                        .build());
        return this;
    }

    public ClassBuilder addField(Object type, String name, String format, Object... args) {
        classBuilder.addField(ClassField.builder()
                                        .addModifier(Modifier.PRIVATE)
                                        .addModifier(Modifier.FINAL)
                                        .type(SymbolConstants.toSymbol(type))
                                        .name(name)
                                        .initializer(FormatExpression.create(format, args))
                                        .build());
        return this;
    }

    public ClassBuilder addField(Object type, String name, SyntaxNode initializer) {
        classBuilder.addField(ClassField.builder()
                                        .addModifier(Modifier.PRIVATE)
                                        .addModifier(Modifier.FINAL)
                                        .type(SymbolConstants.toSymbol(type))
                                        .name(name)
                                        .initializer(initializer)
                                        .build());
        return this;
    }

    public ClassBuilder addField(ClassField field) {
        classBuilder.addField(field);
        return this;
    }

    public ClassBuilder addMethod(Method method) {
        classBuilder.addMethod(method);
        return this;
    }

    public ClassSyntax build() {
        return classBuilder.build();
    }
}
