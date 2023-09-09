package mx.sugus.codegen.plugin.data;

import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.javapoet.TypeSpec;

@FunctionalInterface
public interface DirectiveToTypeSpec {

    TypeSpec build(JavaShapeDirective directive);
}
