package mx.sugus.codegen.plugin;

import mx.sugus.javapoet.TypeSpec;

@FunctionalInterface
public interface ClassRewriter {

    TypeSpec rewrite(TypeSpec original);
}
