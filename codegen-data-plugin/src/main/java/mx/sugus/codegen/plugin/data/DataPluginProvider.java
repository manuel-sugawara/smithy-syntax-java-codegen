package mx.sugus.codegen.plugin.data;

import java.util.Collection;
import java.util.Collections;
import mx.sugus.codegen.generators2.BaseEnumData;
import mx.sugus.codegen.generators2.BaseStructureData;
import mx.sugus.codegen.generators2.InterfaceStructureGenerator;
import mx.sugus.codegen.plugin.BaseModuleConfig;
import mx.sugus.codegen.plugin.Identifier;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.codegen.plugin.PluginProvider;
import mx.sugus.codegen.plugin.ShapeTask;
import mx.sugus.codegen.plugin.SmithyGeneratorPlugin;
import mx.sugus.codegen.plugin.TypeSpecResult;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.syntax.java.InterfaceTrait;
import mx.sugus.syntax.java.JavaTrait;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeType;

public class DataPluginProvider implements PluginProvider {

    private final ObjectNode config;

    public DataPluginProvider(ObjectNode node) {
        this.config = node;
    }

    public DataPluginProvider() {
        this.config = null;
    }

    public static BaseModuleConfig newBaseConfig() {
        return BaseModuleConfig
            .builder()
            .addInit(ShapeType.STRUCTURE,
                     ShapeTask.builder(TypeSpecResult.class)
                              .type(ShapeType.STRUCTURE)
                              .taskId(Identifier.of("mx.sugus.codegen.plugin.data", "Default"))
                              .handler(DataPluginProvider::generateData)
                              .build())
            .addInit(ShapeType.ENUM,
                     ShapeTask.builder(TypeSpecResult.class)
                              .type(ShapeType.ENUM)
                              .taskId(Identifier.of("mx.sugus.codegen.plugin.data", "EnumDefault"))
                              .handler(DataPluginProvider::generateEnumData)
                              .build())
            .build();
    }

    private static TypeSpecResult generateData(JavaShapeDirective directive) {
        var shape = directive.shape();
        if (shape.hasTrait(JavaTrait.class)) {
            return null;
        }
        TypeSpec spec;
        if (shape.hasTrait(InterfaceTrait.class)) {
            spec = new InterfaceStructureGenerator().build(directive);
        } else {
            spec = new BaseStructureData().build(directive);
        }
        return TypeSpecResult.builder()
                             .spec(spec)
                             .build();
    }

    private static TypeSpecResult generateEnumData(JavaShapeDirective directive) {
        var shape = directive.shape();
        if (shape.hasTrait(JavaTrait.class)) {
            return null;
        }
        TypeSpec spec;
        if (shape.hasTrait(InterfaceTrait.class)) {
            spec = new InterfaceStructureGenerator().build(directive);
        } else {
            spec = new BaseEnumData().build(directive);
        }
        return TypeSpecResult.builder()
                             .spec(spec)
                             .build();
    }

    @Override
    public Identifier name() {
        return Identifier.of("mx.sugus.codegen.plugin.data", "DataPluginProvider");
    }

    @Override
    public SmithyGeneratorPlugin build(ObjectNode config) {
        return new DataPlugin();
    }

    static class DataPlugin implements SmithyGeneratorPlugin {

        @Override
        public ClassName name() {
            return ClassName.get("mx.sugus.codegen.plugin.data", "DataPluginProvider");
        }

        @Override
        public Collection<ClassName> requires() {
            return Collections.emptyList();
        }

        @Override
        public BaseModuleConfig merge(BaseModuleConfig config) {
            return BaseModuleConfig.builder()
                                   .merge(newBaseConfig())
                                   .merge(config)
                                   .build();
        }

        @Override
        public BaseModuleConfig merge(ObjectNode node, BaseModuleConfig config) {
            return BaseModuleConfig.builder()
                                   .merge(newBaseConfig())
                                   .merge(config)
                                   .build();
        }
    }
}
