package mx.sugus.syntax.java;

import java.util.Objects;

public final class ForStatement implements Statement {
    private final SyntaxNode initializer;

    private final Block statement;

    private ForStatement(Builder builder) {
        this.initializer = Objects.requireNonNull(builder.initializer, "initializer");
        this.statement = Objects.requireNonNull(builder.statement, "statement");
    }

    public SyntaxNode initializer() {
        return this.initializer;
    }

    public Block statement() {
        return this.statement;
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
        if (!(obj instanceof ForStatement)) {
            return false;
        }
        ForStatement other = (ForStatement) obj;
        return this.initializer.equals(other.initializer)
             && this.statement.equals(other.statement);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + initializer.hashCode();
        hashCode = 31 * hashCode + statement.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "ForStatement{"
             + "initializer: " + initializer
             + ", statement: " + statement + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitForStatement(this);
    }

    public static final class Builder {
        private SyntaxNode initializer;

        private Block statement;

        private boolean _built;

        Builder() {
        }

        Builder(ForStatement data) {
            this.initializer = data.initializer;
            this.statement = data.statement;
        }

        public Builder initializer(SyntaxNode initializer) {
            this.initializer = initializer;
            return this;
        }

        public Builder statement(Block statement) {
            this.statement = statement;
            return this;
        }

        public ForStatement build() {
            return new ForStatement(this);
        }
    }
}
