package mx.sugus.codegen.data;

import mx.sugus.codegen.plugin.BaseModuleConfig;
import mx.sugus.codegen.plugin.ShapeTask;
import mx.sugus.codegen.plugin.ShapeTaskInterceptor;
import software.amazon.smithy.model.shapes.ShapeType;

public final class DataModuleConfig {

    public static BaseModuleConfig config() {
        var init  = initDataTask();
        var builder = BaseModuleConfig
            .builder()
            .addInit(ShapeType.STRUCTURE, init)
            .addInterceptor(init.taskId(), interceptor());
        return builder.build();
    }

    static ShapeTask initDataTask() {
        return ShapeTask
            .builder()
            .type(ShapeType.STRUCTURE)
            .taskId(ShapeTask.of("mx.sugus.codegen.data#init"))
            .build();
    }

    static ShapeTaskInterceptor interceptor() {
        return ShapeTaskInterceptor
            .builder()
            .type(ShapeType.STRUCTURE)
            .name("default")
            .build();
    }
}
