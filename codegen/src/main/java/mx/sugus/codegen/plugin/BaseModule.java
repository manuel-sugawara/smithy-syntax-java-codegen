package mx.sugus.codegen.plugin;

import mx.sugus.javapoet.TypeSpec;

public final class BaseModule {
    private final BaseModuleConfig config;

    public BaseModule(BaseModuleConfig config) {
        this.config = config;
    }

    public void generateShape(JavaShapeDirective directive) {
        var shape = directive.shape();
        for (var task : config.inits(shape)) {
            var typeSpec = runTask(directive, task);
            if (typeSpec != null) {
                serializeResult(directive, task, typeSpec);
            }
        }
    }

    private TypeSpec runTask(JavaShapeDirective directive, ShapeTask task) {
        var typeSpec = task.handler().apply(directive);
        for (var interceptor : config.interceptors(task)) {
            typeSpec = interceptor.handler().apply(directive, typeSpec);
            if (typeSpec == null) {
                return null;
            }
        }
        return typeSpec;
    }

    private void serializeResult(
        JavaShapeDirective directive,
        ShapeTask task,
        TypeSpec typeSpec
    ) {
        for (var serializer : config.serializers(directive, task)) {
            serializer.handler().accept(directive, typeSpec);
        }
    }
}
