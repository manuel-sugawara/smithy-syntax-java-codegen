package mx.sugus.codegen.plugin;

import mx.sugus.codegen.JavaCodegenContext;
import mx.sugus.codegen.JavaCodegenSettings;
import mx.sugus.codegen.JavaSymbolProviderImpl;
import mx.sugus.codegen.JavaSymbolProviderWrapper;
import mx.sugus.codegen.integration.JavaCodegenIntegration;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.codegen.core.directed.DirectedCodegen;
import software.amazon.smithy.codegen.core.directed.GenerateEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateErrorDirective;
import software.amazon.smithy.codegen.core.directed.GenerateIntEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.codegen.core.directed.GenerateUnionDirective;
import software.amazon.smithy.codegen.core.directed.ShapeDirective;
import software.amazon.smithy.model.shapes.Shape;

public class SmithyGenerator implements DirectedCodegen<JavaCodegenContext, JavaCodegenSettings, JavaCodegenIntegration> {

    private final BaseModule module;

    public SmithyGenerator(BaseModule module) {
        this.module = module;
    }

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<JavaCodegenSettings> directive) {
        return SymbolProvider.cache(JavaSymbolProviderImpl.create(directive));
    }

    @Override
    public JavaCodegenContext createContext(CreateContextDirective<JavaCodegenSettings, JavaCodegenIntegration> directive) {
        return JavaCodegenContext.fromContextDirective(directive);
    }

    @Override
    public void generateService(GenerateServiceDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        var javaShapeDirective = from(directive);
        module.generateShape(javaShapeDirective);
    }

    @Override
    public void generateStructure(GenerateStructureDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        var javaShapeDirective = from(directive);
        module.generateShape(javaShapeDirective);

    }

    @Override
    public void generateError(GenerateErrorDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        var javaShapeDirective = from(directive);
        module.generateShape(javaShapeDirective);
    }

    @Override
    public void generateUnion(GenerateUnionDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        var javaShapeDirective = from(directive);
        module.generateShape(javaShapeDirective);

    }

    @Override
    public void generateEnumShape(GenerateEnumDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        var javaShapeDirective = from(directive);
        module.generateShape(javaShapeDirective);
    }

    @Override
    public void generateIntEnumShape(GenerateIntEnumDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        var javaShapeDirective = from(directive);
        module.generateShape(javaShapeDirective);
    }

    private JavaShapeDirective from(ShapeDirective<? extends Shape, JavaCodegenContext, JavaCodegenSettings> directive) {
        return JavaShapeDirective
            .builder()
            .model(directive.model())
            .symbol(directive.symbol())
            .shape(directive.shape())
            .symbolProvider(new JavaSymbolProviderWrapper(directive.symbolProvider()))
            .context(directive.context())
            .settings(directive.settings())
            .build();
    }
}
