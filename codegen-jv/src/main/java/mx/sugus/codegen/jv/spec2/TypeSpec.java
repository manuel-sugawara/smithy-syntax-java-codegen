package mx.sugus.codegen.jv.spec2;

import java.util.Set;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.util.Sets;

public class TypeSpec {

    public enum TypeKind {
        CLASS(
            Sets.empty(),
            Sets.empty(),
            Sets.empty(),
            Sets.empty()),

        INTERFACE(
            Set.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Set.of(Modifier.PUBLIC, Modifier.ABSTRACT),
            Set.of(Modifier.PUBLIC, Modifier.STATIC),
            Set.of(Modifier.STATIC)),

        ENUM(
            Sets.empty(),
            Sets.empty(),
            Sets.empty(),
            Set.of(Modifier.STATIC)),

        ANNOTATION(
            Set.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Set.of(Modifier.PUBLIC, Modifier.ABSTRACT),
            Set.of(Modifier.PUBLIC, Modifier.STATIC),
            Set.of(Modifier.STATIC));

        final Set<Modifier> implicitFieldModifiers;
        final Set<Modifier> implicitMethodModifiers;
        final Set<Modifier> implicitTypeModifiers;
        final Set<Modifier> asMemberModifiers;

        TypeKind(Set<Modifier> implicitFieldModifiers,
                 Set<Modifier> implicitMethodModifiers,
                 Set<Modifier> implicitTypeModifiers,
                 Set<Modifier> asMemberModifiers) {
            this.implicitFieldModifiers = implicitFieldModifiers;
            this.implicitMethodModifiers = implicitMethodModifiers;
            this.implicitTypeModifiers = implicitTypeModifiers;
            this.asMemberModifiers = asMemberModifiers;
        }

        public Set<Modifier> getImplicitFieldModifiers() {
            return implicitFieldModifiers;
        }

        public Set<Modifier> getImplicitMethodModifiers() {
            return implicitMethodModifiers;
        }

        public Set<Modifier> getImplicitTypeModifiers() {
            return implicitTypeModifiers;
        }

        public Set<Modifier> getAsMemberModifiers() {
            return asMemberModifiers;
        }
    }
}
