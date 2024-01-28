package mx.sugus.syntax.java;

import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

public interface TypeSyntax extends SyntaxNode {
    String name();

    Set<Modifier> modifiers();

    List<Annotation> annotations();

    List<FieldSyntax> fields();

    List<MethodSyntax> methods();

    List<TypeSyntax> innerTypes();
}
