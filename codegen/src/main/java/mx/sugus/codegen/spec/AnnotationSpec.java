package mx.sugus.codegen.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.sugus.codegen.spec.emitters.CodeEmitter;
import mx.sugus.codegen.spec.emitters.Emitters;
import mx.sugus.codegen.writer.CodegenWriter;

public final class AnnotationSpec implements CodeEmitter {
    private final Object type;
    private final List<MemberValue> members;

    private AnnotationSpec(Builder builder) {
        this.type = builder.type;
        this.members = List.copyOf(builder.members);
    }

    public static Builder builder(Object type) {
        return new Builder(type);
    }

    @Override
    public void emit(CodegenWriter writer) {
        // TODO, very basic support for the current use cases
        if (members.isEmpty()) {
            writer.writeInline("@$T", type);
            return;
        }
        if (members.size() == 1) {
            var member = members.get(0);
            if (member.name.equals("<default>")) {
                writer.writeInline("@$T(", type);
                member.value.emit(writer);
                writer.writeInlineWithNoFormatting(")");
            }
            return;
        }
        throw new UnsupportedOperationException("full support for annotations not yet implemented");
    }

    record MemberValue(String name, CodeEmitter value) {
    }

    public static class Builder {
        private Object type;
        private List<MemberValue> members = new ArrayList<>();

        Builder(Object type) {
            this.type = Objects.requireNonNull(type);
        }

        public Builder addMember(String name, String value) {
            members.add(new MemberValue(Objects.requireNonNull(name),
                                        Emitters.literalInline(value)));
            return this;
        }

        public Builder addMember(String name, String format, Object... args) {
            members.add(new MemberValue(Objects.requireNonNull(name),
                                        Emitters.formatInline(format, args)));
            return this;
        }

        public Builder addMember(String name, CodeEmitter emitter) {
            members.add(new MemberValue(Objects.requireNonNull(name), emitter));
            return this;
        }

        public Builder addValue(String value) {
            return addMember("<default>", "$S", value);
        }

        public AnnotationSpec build() {
            return new AnnotationSpec(this);
        }
    }
}
