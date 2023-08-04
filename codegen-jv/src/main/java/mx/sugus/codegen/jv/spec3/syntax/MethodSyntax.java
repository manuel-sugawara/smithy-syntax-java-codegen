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
import mx.sugus.codegen.jv.spec3.util.Emit;
import mx.sugus.codegen.jv.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;

public final class MethodSyntax implements SyntaxNode {
    private final String name;
    private final Set<Modifier> modifiers;
    private final List<ParameterSyntax> parameters;
    private final Symbol returnType;
    private final MethodBodySyntax body;

    MethodSyntax(Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.modifiers = new LinkedHashSet<>(builder.modifiers);
        this.parameters = List.copyOf(builder.parameters);
        this.returnType = Objects.requireNonNull(builder.returnType);
        this.body = Objects.requireNonNull(builder.body);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    @Override
    public void emit(CodegenWriter writer) {
        var result = new StringJoiner(" ");
        var kind = TypeSpec.TypeKind.CLASS;
        var actualModifiers = SpecUtils.toString(this.modifiers, kind.getImplicitMethodModifiers());
        if (!actualModifiers.isEmpty()) {
            result.add(actualModifiers);
        }

        // return value
        var args = new ArrayList<>();
        if (returnType != null) {
            result.add("$T");
            args.add(returnType);
        }
        writer.writeInline(result.toString(), args.toArray());
        writer.writeInlineWithNoFormatting(" " + name);
        Emit.emitJoining(writer, parameters, ", ", "(", ")");
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
        return visitor.visitMethod(this);
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

    public List<ParameterSyntax> getParameters() {
        return parameters;
    }

    public Symbol getReturnType() {
        return returnType;
    }

    public MethodBodySyntax getBody() {
        return body;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private final Set<Modifier> modifiers = new LinkedHashSet<>();
        private final List<ParameterSyntax> parameters = new ArrayList<>();
        private final String name;
        private Symbol returnType;
        private MethodBodySyntax body;

        public Builder(String name) {
            this.name = name;
        }

        public Builder(MethodSyntax method) {
            this.name = method.name;
            this.body = method.body;
            this.returnType = method.returnType;
            this.modifiers.addAll(method.modifiers);
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

        public Builder addParameter(ParameterSyntax parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public Builder addParameters(Collection<ParameterSyntax> parameters) {
            this.parameters.clear();
            this.parameters.addAll(parameters);
            return this;
        }

        public Builder returnType(Symbol returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder body(MethodBodySyntax body) {
            this.body = body;
            return this;
        }

        public MethodSyntax build() {
            return new MethodSyntax(this);
        }
    }
}
