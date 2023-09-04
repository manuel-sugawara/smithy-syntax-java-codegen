package mx.sugus.codegen.plugin;

public final class BaseModule {
    private final BaseModuleConfig config;

    public BaseModule(BaseModuleConfig config) {
        this.config = config;
    }

    public void generateShape(JavaShapeDirective directive) {
        var shape = directive.shape();
        for (var task : config.inits(shape)) {
            var result = runTask(directive, task);
            if (result != null) {
                serializeResult(directive, task, result);
            }
        }
    }

    private <T> T runTask(JavaShapeDirective directive, ShapeTask<T> task) {
        var result = task.handler().apply(directive);
        for (var interceptor : config.interceptors(task)) {
            result = interceptor.handler().apply(directive, result);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    private <T> void serializeResult(
        JavaShapeDirective directive,
        ShapeTask<T> task,
        T typeSpec
    ) {
        for (var serializer : config.serializers(directive, task)) {
            serializer.handler().accept(directive, typeSpec);
        }
    }
}
