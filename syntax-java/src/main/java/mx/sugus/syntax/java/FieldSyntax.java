package mx.sugus.syntax.java;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.javapoet.TypeName;
import mx.sugus.util.CollectionBuilderReference;

public final class FieldSyntax implements SyntaxNode {
    private final String name;

    private final TypeName type;

    private final Set<Modifier> modifiers;

    private final List<Annotation> annotations;

    private FieldSyntax(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.type = Objects.requireNonNull(builder.type, "type");
        this.modifiers = builder.modifiers.asPersistent();
        this.annotations = builder.annotations.asPersistent();
    }

    public String name() {
        return this.name;
    }

    public TypeName type() {
        return this.type;
    }

    public Set<Modifier> modifiers() {
        return this.modifiers;
    }

    public List<Annotation> annotations() {
        return this.annotations;
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
        if (!(obj instanceof FieldSyntax)) {
            return false;
        }
        FieldSyntax other = (FieldSyntax) obj;
        return this.name.equals(other.name)
             && this.type.equals(other.type)
             && this.modifiers.equals(other.modifiers)
             && this.annotations.equals(other.annotations);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + type.hashCode();
        hashCode = 31 * hashCode + modifiers.hashCode();
        hashCode = 31 * hashCode + annotations.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "FieldSyntax{"
             + "name: " + name
             + ", type: " + type
             + ", modifiers: " + modifiers
             + ", annotations: " + annotations + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitFieldSyntax(this);
    }

    public static final class Builder {
        private String name;

        private TypeName type;

        private CollectionBuilderReference<Set<Modifier>> modifiers;

        private CollectionBuilderReference<List<Annotation>> annotations;

        private boolean _built;

        Builder() {
            this.modifiers = CollectionBuilderReference.forOrderedSet();
            this.annotations = CollectionBuilderReference.forList();
        }

        Builder(FieldSyntax data) {
            this.name = data.name;
            this.type = data.type;
            this.modifiers = CollectionBuilderReference.fromPersistentOrderedSet(data.modifiers);
            this.annotations = CollectionBuilderReference.fromPersistentList(data.annotations);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(TypeName type) {
            this.type = type;
            return this;
        }

        public Builder modifiers(Set<Modifier> modifiers) {
            this.modifiers.clear();
            this.modifiers.asTransient().addAll(modifiers);
            return this;
        }

        public Builder addModifier(Modifier modifier) {
            this.modifiers.asTransient().add(modifier);
            return this;
        }

        public Builder annotations(List<Annotation> annotations) {
            this.annotations.clear();
            this.annotations.asTransient().addAll(annotations);
            return this;
        }

        public Builder addAnnotation(Annotation annotation) {
            this.annotations.asTransient().add(annotation);
            return this;
        }

        public FieldSyntax build() {
            return new FieldSyntax(this);
        }
    }
}
