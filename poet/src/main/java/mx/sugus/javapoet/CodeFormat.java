package mx.sugus.javapoet;

import java.util.ArrayList;
import java.util.List;

public class CodeFormat implements SyntaxNode {

    private final List<FormatPart> parts;

    public CodeFormat(List<FormatPart> parts) {
        this.parts = parts;
    }

    @Override
    public void emit(CodeWriter writer) {
        throw new UnsupportedOperationException();
    }

    public enum FormatType {
        LITERAL, NAME, STRING, TYPE_NAME
    }

    interface FormatPart extends SyntaxNode {
        FormatType type();
    }

    public static final class Builder {
        private List<FormatPart> parts = new ArrayList<>();

        public Builder addLiteral(Object literal) {
            parts.add(new LiteralFormatPart(literal));
            return this;
        }
    }

    public static final class LiteralFormatPart implements FormatPart {
        private final Object value;

        public LiteralFormatPart(Object literal) {
            this.value = literal;
        }

        @Override
        public void emit(CodeWriter writer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FormatType type() {
            return FormatType.LITERAL;
        }
    }

    public static final class NameFormatPart implements FormatPart {
        private final String value;

        public NameFormatPart(String value) {
            this.value = value;
        }

        @Override
        public void emit(CodeWriter writer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FormatType type() {
            return FormatType.NAME;
        }
    }

    public static final class StringFormatPart implements FormatPart {
        private final String value;

        public StringFormatPart(String value) {
            this.value = value;
        }


        @Override
        public void emit(CodeWriter writer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FormatType type() {
            return FormatType.STRING;
        }
    }

    public static final class TypeNameFormatPart implements FormatPart {
        private final TypeName value;

        public TypeNameFormatPart(TypeName value) {
            this.value = value;
        }

        @Override
        public void emit(CodeWriter writer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FormatType type() {
            return FormatType.TYPE_NAME;
        }
    }
}
