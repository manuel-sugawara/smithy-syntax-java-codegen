package mx.sugus.syntax.java;

import java.util.List;
import java.util.Objects;
import mx.sugus.util.CollectionBuilderReference;

public final class SwitchStatement implements Statement {
    private final Expression expression;

    private final List<CaseClause> cases;

    private final DefaultCaseClause defaultCase;

    private SwitchStatement(Builder builder) {
        this.expression = Objects.requireNonNull(builder.expression, "expression");
        this.cases = builder.cases.asPersistent();
        this.defaultCase = builder.defaultCase;
    }

    public Expression expression() {
        return this.expression;
    }

    public List<CaseClause> cases() {
        return this.cases;
    }

    public DefaultCaseClause defaultCase() {
        return this.defaultCase;
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
        if (!(obj instanceof SwitchStatement)) {
            return false;
        }
        SwitchStatement other = (SwitchStatement) obj;
        return this.expression.equals(other.expression)
             && this.cases.equals(other.cases)
             && Objects.equals(this.defaultCase, other.defaultCase);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + expression.hashCode();
        hashCode = 31 * hashCode + cases.hashCode();
        hashCode = 31 * hashCode + (defaultCase != null ? defaultCase.hashCode() : 0);
        return hashCode;
    }

    @Override
    public String toString() {
        return "SwitchStatement{"
             + "expression: " + expression
             + ", cases: " + cases
             + ", defaultCase: " + defaultCase + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitSwitchStatement(this);
    }

    public static final class Builder {
        private Expression expression;

        private CollectionBuilderReference<List<CaseClause>> cases;

        private DefaultCaseClause defaultCase;

        private boolean _built;

        Builder() {
            this.cases = CollectionBuilderReference.forList();
        }

        Builder(SwitchStatement data) {
            this.expression = data.expression;
            this.cases = CollectionBuilderReference.fromPersistentList(data.cases);
            this.defaultCase = data.defaultCase;
        }

        public Builder expression(Expression expression) {
            this.expression = expression;
            return this;
        }

        public Builder cases(List<CaseClause> cases) {
            this.cases.clear();
            this.cases.asTransient().addAll(cases);
            return this;
        }

        public Builder addACase(CaseClause aCase) {
            this.cases.asTransient().add(aCase);
            return this;
        }

        public Builder defaultCase(DefaultCaseClause defaultCase) {
            this.defaultCase = defaultCase;
            return this;
        }

        public SwitchStatement build() {
            return new SwitchStatement(this);
        }
    }
}
