package mx.sugus.codegen.spec3.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.spec2.SpecUtils;
import mx.sugus.codegen.spec2.TypeSpec;
import mx.sugus.codegen.writer.CodegenWriter;

public final class ClassSyntax implements SyntaxNode {
    private final String name;
    private final Set<Modifier> modifiers;
    private final ClassBody body;

    ClassSyntax(Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.modifiers = new LinkedHashSet<>(builder.modifiers);
        this.body = Objects.requireNonNull(builder.body);
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
        if (body == null || this.modifiers.contains(Modifier.ABSTRACT)) {
            writer.write(";");
        } else {
            body.emit(writer);
        }
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

    public ClassBody getBody() {
        return body;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private final Set<Modifier> modifiers = new LinkedHashSet<>();
        private String name;
        private ClassBody body;

        public Builder() {
        }

        public Builder(String name) {
            this.name = name;
        }

        public Builder(ClassSyntax method) {
            this.name = method.name;
            this.body = method.body;
            this.modifiers.addAll(method.modifiers);
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

        public Builder body(ClassBody body) {
            this.body = body;
            return this;
        }

        public ClassSyntax build() {
            return new ClassSyntax(this);
        }
    }
}