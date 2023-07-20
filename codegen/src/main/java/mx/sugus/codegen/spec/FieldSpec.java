package mx.sugus.codegen.spec;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.spec.emitters.CodeEmitter;
import mx.sugus.codegen.spec.emitters.Emitters;
import mx.sugus.codegen.writer.CodegenWriter;

public class FieldSpec implements CodeEmitter {
    private final Object type;
    private final String name;
    private final Set<Modifier> modifiers;
    private final CodeEmitter initializer;

    FieldSpec(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.modifiers = new LinkedHashSet<>(builder.modifiers);
        this.initializer = builder.initializer;
    }

    public static Builder builder(Object type, String name) {
        return new Builder(type, name);
    }

    public boolean isStatic() {
        return modifiers.contains(Modifier.STATIC);
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

    public static class Builder {
        private Object type;
        private String name;
        private Set<Modifier> modifiers = new LinkedHashSet<>();
        private CodeEmitter initializer;

        Builder(Object type, String name) {
            this.type = Objects.requireNonNull(type);
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
