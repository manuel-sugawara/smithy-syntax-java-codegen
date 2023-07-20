package mx.sugus.codegen.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.spec.emitters.CodeEmitter;
import mx.sugus.codegen.spec.emitters.Emitters;
import mx.sugus.codegen.writer.CodegenWriter;

public final class ParameterSpec implements CodeEmitter {
    private final List<AnnotationSpec> annotations;
    private final Set<Modifier> modifiers;
    private final Object type;
    private final String name;
    private final List<CodeEmitter> javadocs;

    private ParameterSpec(Builder builder) {
        this.annotations = List.copyOf(builder.annotations);
        this.modifiers = Collections.unmodifiableSet(new LinkedHashSet<>(builder.modifiers));
        this.type = Objects.requireNonNull(builder.type);
        this.name = Objects.requireNonNull(builder.name);
        this.javadocs = List.copyOf(builder.javadocs);
    }

    public static ParameterSpec.Builder builder(Object type, String name) {
        return new Builder(type, name);
    }

    public List<CodeEmitter> javaDocs() {
        return javadocs;
    }

    public boolean hasJavadoc() {
        return !javadocs.isEmpty();
    }

    @Override
    public void emit(CodegenWriter writer) {
        for (var annotation : annotations) {
            annotation.emit(writer);
            writer.writeWithNoFormatting(" ");
        }
        var actualModifiers = SpecUtils.toString(this.modifiers);
        if (!actualModifiers.isEmpty()) {
            writer.writeInlineWithNoFormatting(actualModifiers);
            writer.writeWithNoFormatting(" ");
        }
        writer.writeInline("$T $L", type, name);
    }

    public String name() {
        return name;
    }

    public static final class Builder {
        private final List<AnnotationSpec> annotations = new ArrayList<>();
        private final Set<Modifier> modifiers = new LinkedHashSet<>();
        private Object type;
        private String name;
        private final List<CodeEmitter> javadocs = new ArrayList<>();

        private Builder(Object type, String name) {
            this.type = type;
            this.name = name;
        }

        public Builder addModifiers(Modifier... modifiers) {
            Collections.addAll(this.modifiers, modifiers);
            return this;
        }

        public Builder addJavadoc(String javadoc) {
            this.javadocs.add(Emitters.literalComment(javadoc));
            return this;
        }

        public Builder addJavadoc(String format, Object...args) {
            this.javadocs.add(Emitters.formatComment(format, args));
            return this;
        }

        public ParameterSpec build() {
            return new ParameterSpec(this);
        }
    }
}
