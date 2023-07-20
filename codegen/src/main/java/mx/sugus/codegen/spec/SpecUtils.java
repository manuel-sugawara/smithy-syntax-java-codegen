package mx.sugus.codegen.spec;

import java.util.EnumSet;
import java.util.Set;
import java.util.StringJoiner;
import javax.lang.model.element.Modifier;

public class SpecUtils {

    public static String toString(Set<Modifier> modifiers) {
        if (modifiers.isEmpty()) {
            return "";
        }
        var joiner = new StringJoiner(" ");
        for (var modifier : modifiers) {
            joiner.add(modifier.toString());
        }
        return joiner.toString();
    }

    public static String toString(Set<Modifier> modifiers, Set<Modifier> implicit) {
        if (modifiers.isEmpty()) {
            return "";
        }
        var copied = EnumSet.copyOf(modifiers);
        var joiner = new StringJoiner(" ");
        for (var modifier : copied) {
            if (!implicit.contains(modifier)) {
                joiner.add(modifier.toString());
            }
        }
        return joiner.toString();
    }
}
