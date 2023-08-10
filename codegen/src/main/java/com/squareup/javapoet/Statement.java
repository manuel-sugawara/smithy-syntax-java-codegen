package com.squareup.javapoet;

public class Statement implements SyntaxNode {
    private final String format;
    private final Object[] args;
    private final CodeBlock block;

    Statement(Builder builder) {
        this.format = builder.format;
        this.args = builder.args;
        this.block = CodeBlock.builder()
            .add("$[")
            .add(format, args)
            .add("$];\n")
            .build();
    }

    public static Statement of(String statement) {
        return null;
    }

    public static Statement of(String format, Object... args) {
        return null;
    }

    @Override
    public void emit(CodeWriter writer) {
        writer.emit(block);
    }

    public static final class Builder {
        private String format;
        private Object[] args;

        public Builder format(String format, Object...args) {
            this.format = format;
            this.args = args;
            return this;
        }

        public Statement build() {
            return new Statement(this);
        }
    }
}
