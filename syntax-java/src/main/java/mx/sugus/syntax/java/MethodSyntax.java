package mx.sugus.syntax.java;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.javapoet.TypeName;
import mx.sugus.util.CollectionBuilderReference;

public final class MethodSyntax implements SyntaxNode {
    private final String name;

    private final TypeName returnType;

    private final List<Annotation> annotations;

    private final Set<Modifier> modifiers;

    private final List<Parameter> parameters;

    private final Block body;

    private MethodSyntax(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.returnType = Objects.requireNonNull(builder.returnType, "returnType");
        this.annotations = builder.annotations.asPersistent();
        this.modifiers = builder.modifiers.asPersistent();
        this.parameters = builder.parameters.asPersistent();
        this.body = builder.body;
    }

    public String name() {
        return this.name;
    }

    public TypeName returnType() {
        return this.returnType;
    }

    public List<Annotation> annotations() {
        return this.annotations;
    }

    public Set<Modifier> modifiers() {
        return this.modifiers;
    }

    public List<Parameter> parameters() {
        return this.parameters;
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
        if (!(obj instanceof MethodSyntax)) {
            return false;
        }
        MethodSyntax other = (MethodSyntax) obj;
        return this.name.equals(other.name)
             && this.returnType.equals(other.returnType)
             && this.annotations.equals(other.annotations)
             && this.modifiers.equals(other.modifiers)
             && this.parameters.equals(other.parameters)
             && Objects.equals(this.body, other.body);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + returnType.hashCode();
        hashCode = 31 * hashCode + annotations.hashCode();
        hashCode = 31 * hashCode + modifiers.hashCode();
        hashCode = 31 * hashCode + parameters.hashCode();
        hashCode = 31 * hashCode + (body != null ? body.hashCode() : 0);
        return hashCode;
    }

    @Override
    public String toString() {
        return "MethodSyntax{"
             + "name: " + name
             + ", returnType: " + returnType
             + ", annotations: " + annotations
             + ", modifiers: " + modifiers
             + ", parameters: " + parameters
             + ", body: " + body + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitMethodSyntax(this);
    }

    public static final class Builder {
        private String name;

        private TypeName returnType;

        private CollectionBuilderReference<List<Annotation>> annotations;

        private CollectionBuilderReference<Set<Modifier>> modifiers;

        private CollectionBuilderReference<List<Parameter>> parameters;

        private Block body;

        private boolean _built;

        Builder() {
            this.annotations = CollectionBuilderReference.forList();
            this.modifiers = CollectionBuilderReference.forOrderedSet();
            this.parameters = CollectionBuilderReference.forList();
        }

        Builder(MethodSyntax data) {
            this.name = data.name;
            this.returnType = data.returnType;
            this.annotations = CollectionBuilderReference.fromPersistentList(data.annotations);
            this.modifiers = CollectionBuilderReference.fromPersistentOrderedSet(data.modifiers);
            this.parameters = CollectionBuilderReference.fromPersistentList(data.parameters);
            this.body = data.body;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder returnType(TypeName returnType) {
            this.returnType = returnType;
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

        public Builder modifiers(Set<Modifier> modifiers) {
            this.modifiers.clear();
            this.modifiers.asTransient().addAll(modifiers);
            return this;
        }

        public Builder addModifier(Modifier modifier) {
            this.modifiers.asTransient().add(modifier);
            return this;
        }

        public Builder parameters(List<Parameter> parameters) {
            this.parameters.clear();
            this.parameters.asTransient().addAll(parameters);
            return this;
        }

        public Builder addParameter(Parameter parameter) {
            this.parameters.asTransient().add(parameter);
            return this;
        }

        public Builder body(Block body) {
            this.body = body;
            return this;
        }

        public MethodSyntax build() {
            return new MethodSyntax(this);
        }
    }
}
