package mx.sugus.codegen.spec2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.spec2.emitters.AbstractCodeEmitter;
import mx.sugus.codegen.spec2.emitters.BlockCodeEmitter;
import mx.sugus.codegen.spec2.emitters.CodeEmitter;
import mx.sugus.codegen.spec2.emitters.Emitters;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;

public final class MethodSpec extends AbstractCodeEmitter {
    private final String name;
    private final Set<Modifier> modifiers;
    private final Symbol returns;
    private final List<ParameterSpec> params;
    private final List<AnnotationSpec> annotations;
    private final BlockCodeEmitter body;
    private List<CodeEmitter> javadocs;

    private MethodSpec(Builder builder) {
        this.name = builder.name;
        this.modifiers = new LinkedHashSet<>(builder.modifiers);
        this.returns = builder.returns;
        this.params = List.copyOf(builder.params);
        this.javadocs = List.copyOf(builder.javadocs);
        this.annotations = List.copyOf(builder.annotations);
        this.body = builder.state.pop().build();
    }

    public static Builder constructorBuilder() {
        return new Builder();
    }

    public static Builder methodBuilder(String name) {
        return new Builder(name);
    }

    @Override
    public void emit(CodegenWriter writer) {
        emit(name, writer, TypeSpec.TypeKind.CLASS);
    }

    public boolean isConstructor() {
        return name == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MethodSpec)) {
            return false;
        }
        MethodSpec that = (MethodSpec) o;
        return Objects.equals(name, that.name)
               && modifiers.equals(that.modifiers)
               && returns.equals(that.returns)
               && params.equals(that.params)
               && annotations.equals(that.annotations)
               && body.equals(that.body)
               && javadocs.equals(that.javadocs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, modifiers, returns, params, annotations, body, javadocs);
    }

    public String name() {
        return name;
    }

    public Set<Modifier> modifiers() {
        return modifiers;
    }

    public Symbol returns() {
        return returns;
    }

    public List<ParameterSpec> params() {
        return params;
    }

    public List<AnnotationSpec> annotations() {
        return annotations;
    }

    public BlockCodeEmitter body() {
        return body;
    }

    public List<CodeEmitter> javadocs() {
        return javadocs;
    }

    void emit(CodegenWriter writer, TypeSpec.TypeKind kind) {
        emit(name, writer, kind);
    }

    void emit(String actualName, CodegenWriter writer, TypeSpec.TypeKind kind) {
        emitJavadocs(writer);
        for (var annotation : annotations) {
            annotation.emit(writer);
            writer.writeWithNoFormatting("");
        }

        var result = new StringJoiner(" ");
        var actualModifiers = SpecUtils.toString(this.modifiers, kind.implicitMethodModifiers);
        if (!actualModifiers.isEmpty()) {
            result.add(actualModifiers);
        }

        // return value
        var args = new ArrayList<>();
        if (returns != null) {
            result.add("$T");
            args.add(returns);
        }
        writer.writeInline(result.toString(), args.toArray());
        writer.writeInlineWithNoFormatting(" " + actualName);
        Emitters.emitJoining(writer, params, ", ", "(", ")");
        if (this.modifiers.contains(Modifier.ABSTRACT) || kind == TypeSpec.TypeKind.INTERFACE) {
            writer.write(";");
        } else {
            body.emit(writer);
        }
    }

    private void emitJavadocs(CodegenWriter writer) {
        if (javadocs.isEmpty()) {
            return;
        }
        writer.pushState();
        writer.write("/**");
        writer.setNewlinePrefix(" * ");
        for (var javadoc : javadocs) {
            javadoc.emit(writer);
        }

        var hasParamsJavadocs = params.stream().anyMatch(ParameterSpec::hasJavadoc);
        if (hasParamsJavadocs) {
            writer.writeWithNoFormatting("");
            for (var param : params) {
                writer.writeInline("@param $L ", param.name());
                for (var javadoc : param.javaDocs()) {
                    javadoc.emit(writer);
                }
            }
        }
        writer.popState();
        writer.write(" */");
    }

    public static class Builder {
        private final List<AnnotationSpec> annotations = new ArrayList<>();
        private String name;
        private Set<Modifier> modifiers = new LinkedHashSet<>();
        private Symbol returns;
        private List<ParameterSpec> params = new ArrayList<>();
        private List<CodeEmitter> javadocs = new ArrayList<>();
        private Deque<BlockCodeEmitter.Builder> state = new ArrayDeque<>();

        Builder(String name) {
            this.name = Objects.requireNonNull(name);
            this.state.push(BlockCodeEmitter.builder().prefix(Emitters.emptyInline()));
        }

        Builder() {
            this.state.push(BlockCodeEmitter.builder().prefix(Emitters.emptyInline()));
        }

        public Builder addAnnotation(Object type) {
            this.annotations.add(AnnotationSpec.builder(type).build());
            return this;
        }

        public Builder addAnnotation(AnnotationSpec annotation) {
            this.annotations.add(annotation);
            return this;
        }

        public Builder addModifiers(Modifier... modifiers) {
            this.modifiers.addAll(Arrays.asList(modifiers));
            return this;
        }

        public Builder returns(Object returns) {
            this.returns = SymbolConstants.toSymbol(returns);
            return this;
        }

        public Builder addParameter(Object type, String name) {
            this.params.add(ParameterSpec.builder(type, name).build());
            return this;
        }

        public Builder addParameter(ParameterSpec param) {
            this.params.add(param);
            return this;
        }

        public Builder addStatement(String line) {
            assert state.peekFirst() != null;
            state.peekFirst().addContent(Emitters.literal(line));
            return this;
        }

        public Builder addJavadoc(String javadoc) {
            if (javadoc != null) {
                this.javadocs.add(Emitters.literalComment(javadoc));
            }
            return this;
        }

        public Builder addJavadoc(String format, Object... args) {
            this.javadocs.add(Emitters.formatComment(format, args));
            return this;
        }

        public Builder addStatement(String format, Object... params) {
            assert state.peekFirst() != null;
            state.peekFirst().addContent(Emitters.format(format, params));
            return this;
        }

        public Builder addLabel(String format, Object... params) {
            assert state.peekFirst() != null;
            state.peekFirst().addContent(Emitters.formatComment(format, params));
            return this;
        }

        public Builder startControlFlow(String content) {
            var blockBuilder = BlockCodeEmitter.builder();
            blockBuilder.prefix(Emitters.literalInline(content));
            state.push(blockBuilder);
            return this;
        }

        public Builder startControlFlow(String format, Object... args) {
            var blockBuilder = BlockCodeEmitter.builder();
            blockBuilder.prefix(Emitters.formatInline(format, args));
            state.push(blockBuilder);
            return this;
        }

        public Builder nextControlFlow(String content) {
            var blockBuilder = BlockCodeEmitter.builder();
            blockBuilder.prefix(Emitters.literalInline(content));
            var previous = state.pop();
            assert state.peek() != null;
            state.peek().addContent(previous.hasNext().build());
            state.push(blockBuilder);
            return this;
        }

        public Builder nextControlFlow(String format, Object... args) {
            if (state.size() <= 1) {
                throw new IllegalStateException("nextControlFlow requires a previous call to startControlFlow");
            }
            var blockBuilder = BlockCodeEmitter.builder();
            blockBuilder.prefix(Emitters.format(format, args));
            var previous = state.pop();
            state.peek().addContent(previous.hasNext().build());
            state.push(blockBuilder);
            return this;
        }

        public Builder addCodeEmitter(CodeEmitter emitter) {
            assert state.peekFirst() != null;
            state.peekFirst().addContent(emitter);
            return this;
        }

        public Builder endControlFlow() {
            if (state.size() <= 1) {
                throw new IllegalStateException("nextControlFlow requires a previous call to startControlFlow");
            }
            var previous = state.pop();
            state.peek().addContent(previous.build());
            return this;
        }

        public Builder ifStatement(String condition, Consumer<Builder> body) {
            startControlFlow("if (" + Objects.requireNonNull(condition) + ")");
            body.accept(this);
            endControlFlow();
            return this;
        }

        public Builder ifStatement(String condition, Consumer<Builder> body, Consumer<Builder> elseBody) {
            startControlFlow("if (" + Objects.requireNonNull(condition) + ")");
            body.accept(this);
            nextControlFlow("else");
            elseBody.accept(this);
            endControlFlow();
            return this;
        }

        public MethodSpec build() {
            return new MethodSpec(this);
        }
    }

    record Parameter(Object type, String name) {
    }
}
