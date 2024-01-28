package mx.sugus.codegen.plugin.data;

import static mx.sugus.codegen.util.PoetUtils.toClassName;

import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Modifier;
import mx.sugus.codegen.IsaKnowledgeIndex;
import mx.sugus.codegen.SymbolConstants;
import mx.sugus.codegen.plugin.JavaShapeDirective;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.FieldSpec;
import mx.sugus.javapoet.MethodSpec;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeSpec;
import mx.sugus.javapoet.TypeVariableName;
import mx.sugus.syntax.java.ConstTrait;
import software.amazon.smithy.model.shapes.MemberShape;

public class InterfaceStructureGenerator implements DirectedStructure {

    @Override
    public ClassName className(JavaShapeDirective state) {
        return toClassName(state.symbol());
    }

    @Override
    public TypeSpec.Builder typeSpec(JavaShapeDirective state) {
        var result = TypeSpec.interfaceBuilder(state.symbol().getName())
                             .addModifiers(Modifier.PUBLIC)
                             .addTypeVariable(
                                 TypeVariableName.get("T", className(state)))
                             .addTypeVariable(
                                 TypeVariableName.get("B", builderClassName(state)));
        var shape = state.shape();
        var parent = IsaKnowledgeIndex.of(state.model()).parent(shape);
        if (parent != null) {
            var parentClass = state.symbolProvider().toClassName(parent);
            //var container = className(state);
            var parentClassNew = ParameterizedTypeName.get(parentClass,
                                                           TypeVariableName.get("T"),
                                                           TypeVariableName.get("B"));
            result.addSuperinterface(parentClassNew);
        } else {

        }

        return result;
    }

    @Override
    public List<FieldSpec> fieldsFor(JavaShapeDirective state, MemberShape member) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodSpec> constructors(JavaShapeDirective state) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodSpec> methodsFor(JavaShapeDirective state, MemberShape member) {
        return List.of(accessor(state, member));
    }


    @Override
    public List<MethodSpec> extraMethods(JavaShapeDirective state) {
        return List.of(toBuilderMethod(state));
    }

    public MethodSpec toBuilderMethod(JavaShapeDirective state) {
        return MethodSpec.methodBuilder("toBuilder")
                         .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                         .returns(TypeVariableName.get("B"))
                         .build();
    }

    private MethodSpec accessor(JavaShapeDirective state, MemberShape member) {
        var symbolProvider = state.symbolProvider();
        var name = symbolProvider.toMemberName(member);
        var type = symbolProvider.toTypeName(member);
        return MethodSpec.methodBuilder(name)
                         .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                         .returns(type)
                         .build();
    }

    @Override
    public List<DirectedStructure> innerTypes(JavaShapeDirective state) {
        return List.of(new BuilderGenerator());
    }

    private ClassName builderClassName(JavaShapeDirective state) {
        return className(state).nestedClass("Builder");
    }

    class BuilderGenerator implements DirectedStructure {
        @Override
        public ClassName className(JavaShapeDirective state) {
            return builderClassName(state);
        }

        @Override
        public TypeSpec.Builder typeSpec(JavaShapeDirective state) {
            var result = TypeSpec.interfaceBuilder(builderClassName(state).simpleName())
                           .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                           .addTypeVariable(
                               TypeVariableName.get("T", InterfaceStructureGenerator.this.className(state)))
                           .addTypeVariable(
                               TypeVariableName.get("B", builderClassName(state)));

            var shape = state.shape();
            var parent = IsaKnowledgeIndex.of(state.model()).parent(shape);
            if (parent != null) {
                var parentClass = state.symbolProvider().toClassName(parent);
                //var container = className(state);
                var parentClassNew = ParameterizedTypeName.get(parentClass.nestedClass("Builder"),
                                                               TypeVariableName.get("T"),
                                                               TypeVariableName.get("B"));
                result.addSuperinterface(parentClassNew);
            } else {

            }

            return result;

        }

        @Override
        public List<FieldSpec> fieldsFor(JavaShapeDirective state, MemberShape member) {
            return Collections.emptyList();
        }

        @Override
        public List<MethodSpec> constructors(JavaShapeDirective state) {
            return Collections.emptyList();
        }

        @Override
        public List<MethodSpec> methodsFor(JavaShapeDirective state, MemberShape member) {
            if (member.hasTrait(ConstTrait.class)) {
                return Collections.emptyList();
            }
            var symbol = state.symbolProvider().toSymbol(member);
            var aggregateType = SymbolConstants.aggregateType(symbol);
            if (aggregateType == SymbolConstants.AggregateType.NONE || aggregateType == SymbolConstants.AggregateType.MAP) {
                return List.of(setter(state, member));
            }
            return List.of(setter(state, member), adder(state, member));
        }

        private MethodSpec adder(JavaShapeDirective state, MemberShape member) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberJavaName(member);
            var symbol = symbolProvider.toSymbol(member);
            var paramName = name.toSingularSpelling().toString();
            var builder = MethodSpec.methodBuilder("add" + name.toSingularSpelling().asCamelCase())
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(TypeVariableName.get("B"));
            builder.addParameter(SymbolConstants.typeParam(symbol), paramName);
            return builder.build();
        }

        public MethodSpec setter(JavaShapeDirective state, MemberShape member) {
            var symbolProvider = state.symbolProvider();
            var name = symbolProvider.toMemberName(member);
            var type = symbolProvider.toTypeName(member);
            var builder = MethodSpec.methodBuilder(name)
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .addParameter(type, name)
                                    .returns(TypeVariableName.get("B"));

            return builder.build();
        }

        @Override
        public List<MethodSpec> extraMethods(JavaShapeDirective state) {
            return List.of(buildMethod(state));
        }

        public MethodSpec buildMethod(JavaShapeDirective state) {
            var builder = MethodSpec.methodBuilder("build")
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(TypeVariableName.get("T"));

            return builder.build();
        }
    }

}
