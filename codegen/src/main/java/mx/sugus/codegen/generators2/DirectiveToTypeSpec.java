package mx.sugus.codegen.generators2;

import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.javapoet.TypeSpec;

@FunctionalInterface
public interface DirectiveToTypeSpec {

    TypeSpec build(JavaShapeDirective directive);
}
