package mx.sugus.syntax.java;

import java.util.List;
import mx.sugus.util.CollectionBuilderReference;

public final class SyntaxFormatter implements SyntaxNode {
    private final List<FormatterNode> parts;

    private SyntaxFormatter(Builder builder) {
        this.parts = builder.parts.asPersistent();
    }

    public List<FormatterNode> parts() {
        return this.parts;
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
        if (!(obj instanceof SyntaxFormatter)) {
            return false;
        }
        SyntaxFormatter other = (SyntaxFormatter) obj;
        return this.parts.equals(other.parts);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + parts.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "SyntaxFormatter{"
             + "parts: " + parts + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitSyntaxFormatter(this);
    }

    public static final class Builder {
        private CollectionBuilderReference<List<FormatterNode>> parts;

        private boolean _built;

        Builder() {
            this.parts = CollectionBuilderReference.forList();
        }

        Builder(SyntaxFormatter data) {
            this.parts = CollectionBuilderReference.fromPersistentList(data.parts);
        }

        public Builder parts(List<FormatterNode> parts) {
            this.parts.clear();
            this.parts.asTransient().addAll(parts);
            return this;
        }

        public Builder addPart(FormatterNode part) {
            this.parts.asTransient().add(part);
            return this;
        }

        public SyntaxFormatter build() {
            return new SyntaxFormatter(this);
        }
    }
}
