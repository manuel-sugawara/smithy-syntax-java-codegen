package mx.sugus.codegen.jv.spec3.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.spec2.SpecUtils;
import mx.sugus.codegen.jv.spec2.TypeSpec;
import mx.sugus.codegen.jv.writer.CodegenWriter;

public final class ClassSyntax implements SyntaxNode {
    public final List<ClassField> fields;
    public final List<MethodSyntax> methods;
    private final String name;
    private final Set<Modifier> modifiers;

    ClassSyntax(Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.modifiers = new LinkedHashSet<>(builder.modifiers);
        this.methods = List.copyOf(builder.methods);
        this.fields = List.copyOf(builder.fields);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodegenWriter writer) {
        var result = new StringJoiner(" ");
        var kind = TypeSpec.TypeKind.CLASS;
        var actualModifiers = SpecUtils.toString(this.modifiers, kind.getImplicitTypeModifiers());
        if (!actualModifiers.isEmpty()) {
            result.add(actualModifiers);
        }

        var args = new ArrayList<>();
        writer.writeInline(result.toString(), args.toArray());
        writer.writeInlineWithNoFormatting(" class ");
        writer.writeInlineWithNoFormatting(name);
        writer.write(" {").indent();
        for (var field : fields) {
            field.emit(writer);
        }
        for (var method : methods) {
            method.emit(writer);
        }
        writer.dedent().writeWithNoFormatting("}");
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visitClass(this);
    }

    @Override
    public String toString() {
        var writer = new CodegenWriter("<none>");
        emit(writer);
        return writer.toString();
    }

    public String getName() {
        return name;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public List<MethodSyntax> getMethods() {
        return methods;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        public final List<ClassField> fields = new ArrayList<>();
        private final Set<Modifier> modifiers = new LinkedHashSet<>();
        private final List<MethodSyntax> methods = new ArrayList<>();
        private String name;

        public Builder() {
        }

        public Builder(String name) {
            this.name = name;
        }

        public Builder(ClassSyntax method) {
            this.name = method.name;
            this.modifiers.addAll(method.modifiers);
            this.fields.addAll(method.fields);
            this.methods.addAll(method.methods);
        }

        public Builder name(String name) {
            this.name = name;
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

        public Builder addField(ClassField field) {
            this.fields.add(field);
            return this;
        }

        public Builder addFields(Collection<ClassField> fields) {
            this.fields.clear();
            this.fields.addAll(fields);
            return this;
        }

        public Builder addMethod(MethodSyntax method) {
            this.methods.add(method);
            return this;
        }

        public Builder addMethods(Collection<MethodSyntax> methods) {
            this.methods.clear(); // XXX Should we do this?
            this.methods.addAll(methods);
            return this;
        }

        public ClassSyntax build() {
            return new ClassSyntax(this);
        }
    }
}
