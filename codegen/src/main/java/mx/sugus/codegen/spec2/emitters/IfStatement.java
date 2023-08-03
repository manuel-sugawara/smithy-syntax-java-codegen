package mx.sugus.codegen.spec2.emitters;

import mx.sugus.codegen.writer.CodegenWriter;

public class IfStatement extends BlockCodeEmitter2<IfStatement.Builder, IfStatement> {
    private final BlockCodeEmitter2<?, ?> elseBody;

    IfStatement(Builder builder) {
        super(builder);
        elseBody = null;
    }

    @SuppressWarnings("unchecked")
    IfStatement(Builder builder, Builder.ElseBuilder elseBuilder) {
        super(builder);
        elseBody = new BlockCodeEmitter2<>((BlockCodeEmitter2.Builder<?, ?>) elseBuilder);
    }

    public static Builder builder(String condition) {
        return new Builder(Emitters.literalInline(condition));
    }

    public static Builder builder(CodeEmitter conditionEmitter) {
        return new Builder(conditionEmitter);
    }

    @Override
    public void emit(CodegenWriter writer) {
        prefix().emit(writer);
        writer.write("{")
              .indent();
        for (var emitter : contents) {
            emitter.emit(writer);
        }
        if (elseBody == null) {
            writer.dedent().writeWithNoFormatting("}");
            return;
        }
        writer.dedent().writeInlineWithNoFormatting("} else");
        elseBody.emit(writer);
    }

    @Override
    public CodeEmitter prefix() {
        return writer -> {
            writer.writeInlineWithNoFormatting("if (");
            prefix.emit(writer);
            writer.writeInlineWithNoFormatting(") ");
        };
    }

    public static class Builder extends BlockCodeEmitter2.Builder<Builder, IfStatement> {
        private Builder(CodeEmitter prefix) {
            super(prefix);
        }

        public ElseBuilder orElse() {
            return new ElseBuilder();
        }

        public IfStatement build() {
            return new IfStatement(this);
        }

        public class ElseBuilder extends BlockCodeEmitter2.Builder<ElseBuilder, IfStatement> {

            public IfStatement build() {
                return new IfStatement(IfStatement.Builder.this, this);
            }
        }
    }

    public static class ElseBodyBuilder extends BlockCodeEmitter2.Builder<ElseBodyBuilder, IfStatement> {
        ElseBodyBuilder() {
        }

        public IfStatement build() {
            return null;
        }
    }
}
