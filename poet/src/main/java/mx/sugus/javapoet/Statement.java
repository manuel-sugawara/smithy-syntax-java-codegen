package mx.sugus.javapoet;


import java.util.ArrayList;
import java.util.List;

public final class Statement implements SyntaxNode {
    private final String format;
    private final List<Object> args;
    private final CodeBlock block;

    Statement(Builder builder) {
        this.format = String.join("", builder.formats);
        this.args = List.copyOf(builder.args);
        this.block = CodeBlock.builder()
                              .add("$[")
                              .add(format, args.toArray())
                              .add("$];\n")
                              .build();
    }

    public static Statement of(String statement) {
        return builder()
            .addCode(statement)
            .build();
    }

    public static Statement of(String format, Object... args) {
        return builder()
            .addCode(format, args)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit(block);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        CodeWriter codeWriter = new CodeWriter(out);
        codeWriter.emit(block);
        return out.toString();
    }

    public static final class Builder {
        private final List<String> formats = new ArrayList<>();
        private final List<Object> args = new ArrayList<>();

        public Builder addCode(String format, Object... args) {
            this.formats.add(format);
            for (var arg : args) {
                this.args.add(arg);
            }
            return this;
        }

        public Builder addCodeLn(String format, Object... args) {
            this.formats.add(format + "\n");
            for (var arg : args) {
                this.args.add(arg);
            }
            return this;
        }

        public Statement build() {
            return new Statement(this);
        }
    }
}
