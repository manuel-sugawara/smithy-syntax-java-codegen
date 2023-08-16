package mx.sugus.plugin;

import mx.sugus.javapoet.TypeSpec;

@FunctionalInterface
public interface ClassRewriter {

    TypeSpec rewrite(TypeSpec original);
}
