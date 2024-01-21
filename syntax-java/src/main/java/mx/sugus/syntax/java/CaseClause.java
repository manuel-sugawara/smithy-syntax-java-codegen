package mx.sugus.syntax.java;

import java.util.List;
import java.util.Objects;
import mx.sugus.util.CollectionBuilderReference;

public final class CaseClause implements SyntaxNode {
    private final List<SyntaxNode> label;

    private final Block body;

    private CaseClause(Builder builder) {
        this.label = builder.label.asPersistent();
        this.body = Objects.requireNonNull(builder.body, "body");
    }

    public List<SyntaxNode> label() {
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
        if (!(obj instanceof CaseClause)) {
            return false;
        }
        CaseClause other = (CaseClause) obj;
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
        return "CaseClause{"
             + "label: " + label
             + ", body: " + body + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitCaseClause(this);
    }

    public static final class Builder {
        private CollectionBuilderReference<List<SyntaxNode>> label;

        private Block body;

        private boolean _built;

        Builder() {
            this.label = CollectionBuilderReference.forList();
        }

        Builder(CaseClause data) {
            this.label = CollectionBuilderReference.fromPersistentList(data.label);
            this.body = data.body;
        }

        public Builder label(List<SyntaxNode> label) {
            this.label.clear();
            this.label.asTransient().addAll(label);
            return this;
        }

        public Builder addLabel(SyntaxNode label) {
            this.label.asTransient().add(label);
            return this;
        }

        public Builder body(Block body) {
            this.body = body;
            return this;
        }

        public CaseClause build() {
            return new CaseClause(this);
        }
    }
}
