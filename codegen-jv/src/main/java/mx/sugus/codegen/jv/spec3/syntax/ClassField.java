package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.spec2.SpecUtils;
import mx.sugus.codegen.jv.spec2.TypeSpec;
import mx.sugus.codegen.jv.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;

public final class ClassField implements SyntaxNode {
    private final Set<Modifier> modifiers;
    private final Symbol type;
    private final String name;
    private final SyntaxNode initializer;

    ClassField(Builder builder) {
        this.modifiers = Collections.unmodifiableSet(new LinkedHashSet<>(builder.modifiers));
        this.name = builder.name;
        this.type = builder.type;
        this.initializer = builder.initializer;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        var kind = TypeSpec.TypeKind.CLASS;
        var actualModifiers = SpecUtils.toString(this.modifiers, kind.getImplicitFieldModifiers());
        writer.writeInline("$L $T $L", actualModifiers, type, name);
        if (initializer != null) {
            initializer.emit(writer);
        }
        writer.writeWithNoFormatting(";");
    }

    @Override
    public Kind kind() {
        return Kind.ClassField;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitClassField(this);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public Symbol getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public SyntaxNode getInitializer() {
        return initializer;
    }

    public static class Builder {
        private final Set<Modifier> modifiers = new LinkedHashSet<>();
        private Symbol type;
        private String name;
        private SyntaxNode initializer;

        Builder() {
        }

        Builder(ClassField classField) {
            this.type = classField.type;
            this.name = classField.name;
            this.modifiers.addAll(classField.modifiers);
            this.initializer = classField.initializer;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(Symbol type) {
            this.type = type;
            return this;
        }

        public Builder addModifier(Modifier modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public Builder addModifiers(Collection<Modifier> modifiers) {
            this.modifiers.clear();
            this.modifiers.addAll(modifiers);
            return this;
        }

        public Builder initializer(SyntaxNode initializer) {
            this.initializer = initializer;
            return this;
        }

        public ClassField build() {
            return new ClassField(this);
        }
    }
}
