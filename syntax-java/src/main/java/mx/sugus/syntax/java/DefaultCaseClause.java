package mx.sugus.syntax.java;

import java.util.Objects;

public final class DefaultCaseClause {
    private final SyntaxNode label;

    private final Block body;

    private DefaultCaseClause(Builder builder) {
        this.label = Objects.requireNonNull(builder.label, "label");
        this.body = Objects.requireNonNull(builder.body, "body");
    }

    public SyntaxNode label() {
        return this.label;
    }

    public Block body() {
        return this.body;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultCaseClause)) {
            return false;
        }
        DefaultCaseClause other = (DefaultCaseClause) obj;
        return this.label.equals(other.label)
             && this.body.equals(other.body);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + label.hashCode();
        hashCode = 31 * hashCode + body.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "DefaultCaseClause{"
             + "label: " + label
             + ", body: " + body + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SyntaxNode label;

        private Block body;

        private boolean _built;

        Builder() {
        }

        Builder(DefaultCaseClause data) {
            this.label = data.label;
            this.body = data.body;
        }

        public Builder label(SyntaxNode label) {
            this.label = label;
            return this;
        }

        public Builder body(Block body) {
            this.body = body;
            return this;
        }

        public DefaultCaseClause build() {
            return new DefaultCaseClause(this);
        }
    }
}
