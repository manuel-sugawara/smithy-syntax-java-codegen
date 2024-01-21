package mx.sugus.codegen.plugin;

import software.amazon.smithy.model.Model;

public interface TransformModelTask {

    Identifier taskId();

    Model transform(Model in);
}
