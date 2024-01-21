package mx.sugus.syntax.java;

import java.util.Objects;

public final class FinallyClause implements SyntaxNode {
    private final Block statement;

    private FinallyClause(Builder builder) {
        this.statement = Objects.requireNonNull(builder.statement, "statement");
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
        if (!(obj instanceof FinallyClause)) {
            return false;
        }
        FinallyClause other = (FinallyClause) obj;
        return this.statement.equals(other.statement);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + statement.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "FinallyClause{"
             + "statement: " + statement + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitFinallyClause(this);
    }

    public static final class Builder {
        private Block statement;

        private boolean _built;

        Builder() {
        }

        Builder(FinallyClause data) {
            this.statement = data.statement;
        }

        public Builder statement(Block statement) {
            this.statement = statement;
            return this;
        }

        public FinallyClause build() {
            return new FinallyClause(this);
        }
    }
}
