package mx.sugus.syntax.java;

import java.util.List;
import java.util.Objects;
import mx.sugus.util.CollectionBuilderReference;

public final class TryStatement implements Statement {
    private final SyntaxNode resources;

    private final Block statement;

    private final List<CatchClause> catchClauses;

    private final FinallyClause finallyClause;

    private TryStatement(Builder builder) {
        this.resources = builder.resources;
        this.statement = Objects.requireNonNull(builder.statement, "statement");
        this.catchClauses = builder.catchClauses.asPersistent();
        this.finallyClause = builder.finallyClause;
    }

    public SyntaxNode resources() {
        return this.resources;
    }

    public Block statement() {
        return this.statement;
    }

    public List<CatchClause> catchClauses() {
        return this.catchClauses;
    }

    public FinallyClause finallyClause() {
        return this.finallyClause;
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
        if (!(obj instanceof TryStatement)) {
            return false;
        }
        TryStatement other = (TryStatement) obj;
        return Objects.equals(this.resources, other.resources)
             && this.statement.equals(other.statement)
             && this.catchClauses.equals(other.catchClauses)
             && Objects.equals(this.finallyClause, other.finallyClause);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + (resources != null ? resources.hashCode() : 0);
        hashCode = 31 * hashCode + statement.hashCode();
        hashCode = 31 * hashCode + catchClauses.hashCode();
        hashCode = 31 * hashCode + (finallyClause != null ? finallyClause.hashCode() : 0);
        return hashCode;
    }

    @Override
    public String toString() {
        return "TryStatement{"
             + "resources: " + resources
             + ", statement: " + statement
             + ", catchClauses: " + catchClauses
             + ", finallyClause: " + finallyClause + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitTryStatement(this);
    }

    public static final class Builder {
        private SyntaxNode resources;

        private Block statement;

        private CollectionBuilderReference<List<CatchClause>> catchClauses;

        private FinallyClause finallyClause;

        private boolean _built;

        Builder() {
            this.catchClauses = CollectionBuilderReference.forList();
        }

        Builder(TryStatement data) {
            this.resources = data.resources;
            this.statement = data.statement;
            this.catchClauses = CollectionBuilderReference.fromPersistentList(data.catchClauses);
            this.finallyClause = data.finallyClause;
        }

        public Builder resources(SyntaxNode resources) {
            this.resources = resources;
            return this;
        }

        public Builder statement(Block statement) {
            this.statement = statement;
            return this;
        }

        public Builder catchClauses(List<CatchClause> catchClauses) {
            this.catchClauses.clear();
            this.catchClauses.asTransient().addAll(catchClauses);
            return this;
        }

        public Builder addCatchClause(CatchClause catchClause) {
            this.catchClauses.asTransient().add(catchClause);
            return this;
        }

        public Builder finallyClause(FinallyClause finallyClause) {
            this.finallyClause = finallyClause;
            return this;
        }

        public TryStatement build() {
            return new TryStatement(this);
        }
    }
}
