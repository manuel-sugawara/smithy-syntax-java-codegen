package mx.sugus.syntax.java;

import java.util.List;
import mx.sugus.util.CollectionBuilderReference;

public final class Block implements Statement {
    private final List<Statement> statements;

    private Block(Builder builder) {
        this.statements = builder.statements.asPersistent();
    }

    public List<Statement> statements() {
        return this.statements;
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
        if (!(obj instanceof Block)) {
            return false;
        }
        Block other = (Block) obj;
        return this.statements.equals(other.statements);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + statements.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "Block{"
             + "statements: " + statements + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitBlock(this);
    }

    public static final class Builder {
        private CollectionBuilderReference<List<Statement>> statements;

        private boolean _built;

        Builder() {
            this.statements = CollectionBuilderReference.forList();
        }

        Builder(Block data) {
            this.statements = CollectionBuilderReference.fromPersistentList(data.statements);
        }

        public Builder statements(List<Statement> statements) {
            this.statements.clear();
            this.statements.asTransient().addAll(statements);
            return this;
        }

        public Builder addStatement(Statement statement) {
            this.statements.asTransient().add(statement);
            return this;
        }

        public Block build() {
            return new Block(this);
        }
    }
}
