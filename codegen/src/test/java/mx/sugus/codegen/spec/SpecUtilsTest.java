package mx.sugus.codegen.spec;

import java.util.Set;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class SpecUtilsTest {

    @Test
    public void test0() {
        System.out.printf("===>> <<%s>>\n", SpecUtils.toString(Set.of(Modifier.PUBLIC, Modifier.ABSTRACT),
                                                               TypeSpec.TypeKind.INTERFACE.implicitMethodModifiers));
    }
}