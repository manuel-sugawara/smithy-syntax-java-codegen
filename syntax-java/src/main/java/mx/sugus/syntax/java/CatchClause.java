package mx.sugus.syntax.java;

import java.util.Objects;

public final class CatchClause implements SyntaxNode {
    private final SyntaxNode parameter;

    private final Block statement;

    private CatchClause(Builder builder) {
        this.parameter = Objects.requireNonNull(builder.parameter, "parameter");
        this.statement = Objects.requireNonNull(builder.statement, "statement");
    }

    public SyntaxNode parameter() {
        return this.parameter;
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
        if (!(obj instanceof CatchClause)) {
            return false;
        }
        CatchClause other = (CatchClause) obj;
        return this.parameter.equals(other.parameter)
             && this.statement.equals(other.statement);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + parameter.hashCode();
        hashCode = 31 * hashCode + statement.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "CatchClause{"
             + "parameter: " + parameter
             + ", statement: " + statement + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitCatchClause(this);
    }

    public static final class Builder {
        private SyntaxNode parameter;

        private Block statement;

        private boolean _built;

        Builder() {
        }

        Builder(CatchClause data) {
            this.parameter = data.parameter;
            this.statement = data.statement;
        }

        public Builder parameter(SyntaxNode parameter) {
            this.parameter = parameter;
            return this;
        }

        public Builder statement(Block statement) {
            this.statement = statement;
            return this;
        }

        public CatchClause build() {
            return new CatchClause(this);
        }
    }
}
