package mx.sugus.syntax.java;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.util.CollectionBuilderReference;

public final class ClassSyntax implements TypeSyntax {
    private final List<MethodSyntax> methods;

    private final String name;

    private final List<Annotation> annotations;

    private final Set<Modifier> modifiers;

    private final List<FieldSyntax> fields;

    private final List<TypeSyntax> innerTypes;

    private ClassSyntax(Builder builder) {
        this.methods = builder.methods.asPersistent();
        this.name = Objects.requireNonNull(builder.name, "name");
        this.annotations = builder.annotations.asPersistent();
        this.modifiers = builder.modifiers.asPersistent();
        this.fields = builder.fields.asPersistent();
        this.innerTypes = builder.innerTypes.asPersistent();
    }

    public List<MethodSyntax> methods() {
        return this.methods;
    }

    public String name() {
        return this.name;
    }

    public List<Annotation> annotations() {
        return this.annotations;
    }

    public Set<Modifier> modifiers() {
        return this.modifiers;
    }

    public List<FieldSyntax> fields() {
        return this.fields;
    }

    public List<TypeSyntax> innerTypes() {
        return this.innerTypes;
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
        if (!(obj instanceof ClassSyntax)) {
            return false;
        }
        ClassSyntax other = (ClassSyntax) obj;
        return this.methods.equals(other.methods)
             && this.name.equals(other.name)
             && this.annotations.equals(other.annotations)
             && this.modifiers.equals(other.modifiers)
             && this.fields.equals(other.fields)
             && this.innerTypes.equals(other.innerTypes);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + methods.hashCode();
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + annotations.hashCode();
        hashCode = 31 * hashCode + modifiers.hashCode();
        hashCode = 31 * hashCode + fields.hashCode();
        hashCode = 31 * hashCode + innerTypes.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "ClassSyntax{"
             + "methods: " + methods
             + ", name: " + name
             + ", annotations: " + annotations
             + ", modifiers: " + modifiers
             + ", fields: " + fields
             + ", innerTypes: " + innerTypes + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitClassSyntax(this);
    }

    public static final class Builder {
        private CollectionBuilderReference<List<MethodSyntax>> methods;

        private String name;

        private CollectionBuilderReference<List<Annotation>> annotations;

        private CollectionBuilderReference<Set<Modifier>> modifiers;

        private CollectionBuilderReference<List<FieldSyntax>> fields;

        private CollectionBuilderReference<List<TypeSyntax>> innerTypes;

        private boolean _built;

        Builder() {
            this.methods = CollectionBuilderReference.forList();
            this.annotations = CollectionBuilderReference.forList();
            this.modifiers = CollectionBuilderReference.forOrderedSet();
            this.fields = CollectionBuilderReference.forList();
            this.innerTypes = CollectionBuilderReference.forList();
        }

        Builder(ClassSyntax data) {
            this.methods = CollectionBuilderReference.fromPersistentList(data.methods);
            this.name = data.name;
            this.annotations = CollectionBuilderReference.fromPersistentList(data.annotations);
            this.modifiers = CollectionBuilderReference.fromPersistentOrderedSet(data.modifiers);
            this.fields = CollectionBuilderReference.fromPersistentList(data.fields);
            this.innerTypes = CollectionBuilderReference.fromPersistentList(data.innerTypes);
        }

        public Builder methods(List<MethodSyntax> methods) {
            this.methods.clear();
            this.methods.asTransient().addAll(methods);
            return this;
        }

        public Builder addMethod(MethodSyntax method) {
            this.methods.asTransient().add(method);
            return this;
        }

        public Builder name(String name) {
            this.name = name;
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

        public Builder fields(List<FieldSyntax> fields) {
            this.fields.clear();
            this.fields.asTransient().addAll(fields);
            return this;
        }

        public Builder addField(FieldSyntax field) {
            this.fields.asTransient().add(field);
            return this;
        }

        public Builder innerTypes(List<TypeSyntax> innerTypes) {
            this.innerTypes.clear();
            this.innerTypes.asTransient().addAll(innerTypes);
            return this;
        }

        public Builder addInnerType(TypeSyntax innerType) {
            this.innerTypes.asTransient().add(innerType);
            return this;
        }

        public ClassSyntax build() {
            return new ClassSyntax(this);
        }
    }
}
