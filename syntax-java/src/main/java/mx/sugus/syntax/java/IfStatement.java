package mx.sugus.syntax.java;

import java.util.Objects;

public final class IfStatement implements Statement {
    private final Expression expression;

    private final Block statement;

    private final Statement elseStatement;

    private IfStatement(Builder builder) {
        this.expression = Objects.requireNonNull(builder.expression, "expression");
        this.statement = Objects.requireNonNull(builder.statement, "statement");
        this.elseStatement = builder.elseStatement;
    }

    public Expression expression() {
        return this.expression;
    }

    public Block statement() {
        return this.statement;
    }

    public Statement elseStatement() {
        return this.elseStatement;
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
        if (!(obj instanceof IfStatement)) {
            return false;
        }
        IfStatement other = (IfStatement) obj;
        return this.expression.equals(other.expression)
             && this.statement.equals(other.statement)
             && Objects.equals(this.elseStatement, other.elseStatement);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + expression.hashCode();
        hashCode = 31 * hashCode + statement.hashCode();
        hashCode = 31 * hashCode + (elseStatement != null ? elseStatement.hashCode() : 0);
        return hashCode;
    }

    @Override
    public String toString() {
        return "IfStatement{"
             + "expression: " + expression
             + ", statement: " + statement
             + ", elseStatement: " + elseStatement + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitIfStatement(this);
    }

    public static final class Builder {
        private Expression expression;

        private Block statement;

        private Statement elseStatement;

        private boolean _built;

        Builder() {
        }

        Builder(IfStatement data) {
            this.expression = data.expression;
            this.statement = data.statement;
            this.elseStatement = data.elseStatement;
        }

        public Builder expression(Expression expression) {
            this.expression = expression;
            return this;
        }

        public Builder statement(Block statement) {
            this.statement = statement;
            return this;
        }

        public Builder elseStatement(Statement elseStatement) {
            this.elseStatement = elseStatement;
            return this;
        }

        public IfStatement build() {
            return new IfStatement(this);
        }
    }
}
