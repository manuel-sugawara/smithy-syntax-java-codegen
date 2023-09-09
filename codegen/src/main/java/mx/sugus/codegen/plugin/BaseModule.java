package mx.sugus.codegen.plugin;

import mx.sugus.codegen.JavaCodegenSettings;
import software.amazon.smithy.model.Model;

public final class BaseModule {
    private final BaseModuleConfig config;

    public BaseModule(BaseModuleConfig config) {
        this.config = config;
    }

    public Model preprocessModel(Model model, JavaCodegenSettings settings) {
        /*
        I want here something along the lines of
        if (settings.generateSyntheticServiceForNamespace()) {
            return new
         */
        return model;
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

    private <T> T runTask(JavaShapeDirective directive, ShapeBaseTask<T> task) {
        var result = task.produce().apply(directive);
        if (result != null) {
            for (var interceptor : config.interceptors(task)) {
                result = interceptor.transform().apply(directive, result);
                if (result == null) {
                    return null;
                }
            }
        }
        return result;
    }

    private <T> void serializeResult(
        JavaShapeDirective directive,
        ShapeBaseTask<?> task,
        T typeSpec
    ) {
        @SuppressWarnings("unchecked")
        var taskNew = (ShapeBaseTask<T>) task;
        for (var serializer : config.serializers(directive, taskNew)) {
            serializer.consume().accept(directive,  typeSpec);
        }
    }
}
