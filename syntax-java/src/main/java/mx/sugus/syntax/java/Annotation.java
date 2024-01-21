package mx.sugus.syntax.java;

import java.util.Objects;

public final class Annotation implements SyntaxNode {
    private final String member;

    private final SyntaxNode value;

    private Annotation(Builder builder) {
        this.member = Objects.requireNonNull(builder.member, "member");
        this.value = Objects.requireNonNull(builder.value, "value");
    }

    public String member() {
        return this.member;
    }

    public SyntaxNode value() {
        return this.value;
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
        if (!(obj instanceof Annotation)) {
            return false;
        }
        Annotation other = (Annotation) obj;
        return this.member.equals(other.member)
             && this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + member.hashCode();
        hashCode = 31 * hashCode + value.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "Annotation{"
             + "member: " + member
             + ", value: " + value + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitAnnotation(this);
    }

    public static final class Builder {
        private String member;

        private SyntaxNode value;

        private boolean _built;

        Builder() {
        }

        Builder(Annotation data) {
            this.member = data.member;
            this.value = data.value;
        }

        public Builder member(String member) {
            this.member = member;
            return this;
        }

        public Builder value(SyntaxNode value) {
            this.value = value;
            return this;
        }

        public Annotation build() {
            return new Annotation(this);
        }
    }
}
