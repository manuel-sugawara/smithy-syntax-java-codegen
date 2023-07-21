package mx.sugus.codegen.spec2;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.spec2.emitters.AbstractCodeEmitter;
import mx.sugus.codegen.spec2.emitters.CodeEmitter;
import mx.sugus.codegen.spec2.emitters.Emitters;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;

public final class FieldSpec extends AbstractCodeEmitter {
    private final Symbol type;
    private final String name;
    private final Set<Modifier> modifiers;
    private final CodeEmitter initializer;

    private FieldSpec(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.modifiers = Collections.unmodifiableSet(new LinkedHashSet<>(builder.modifiers));
        this.initializer = builder.initializer;
    }

    public static Builder builder(Object type, String name) {
        return new Builder(type, name);
    }

    public boolean isStatic() {
        return modifiers.contains(Modifier.STATIC);
    }

    public String name() {
        return name;
    }

    public Set<Modifier> modifiers() {
        return modifiers;
    }

    public CodeEmitter initializer() {
        return initializer;
    }

    @Override
    public void emit(CodegenWriter writer) {
        writer.writeInlineWithNoFormatting(SpecUtils.toString(modifiers));
        writer.writeInline(" $T", type);
        writer.writeInlineWithNoFormatting(" " + name);
        if (initializer != null) {
            writer.writeInlineWithNoFormatting(" = ");
            initializer.emit(writer);
        }
        writer.write(";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldSpec)) {
            return false;
        }
        FieldSpec fieldSpec = (FieldSpec) o;
        return type.equals(fieldSpec.type)
               && name.equals(fieldSpec.name)
               && modifiers.equals(fieldSpec.modifiers)
               && Objects.equals(initializer, fieldSpec.initializer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, modifiers, initializer);
    }

    public static class Builder {
        private Symbol type;
        private String name;
        private Set<Modifier> modifiers = new LinkedHashSet<>();
        private CodeEmitter initializer;

        Builder(Object type, String name) {
            this.type = SymbolConstants.toSymbol(Objects.requireNonNull(type));
            this.name = Objects.requireNonNull(name);
        }

        public Builder addModifiers(Modifier... modifiers) {
            for (var modifier : modifiers) {
                this.modifiers.add(modifier);
            }
            return this;
        }

        public Builder initializer(String value) {
            this.initializer = Emitters.literalInline(value);
            return this;
        }

        public Builder initializer(String format, Object... args) {
            this.initializer = Emitters.formatInline(format, args);
            return this;
        }

        public FieldSpec build() {
            return new FieldSpec(this);
        }
    }
}
