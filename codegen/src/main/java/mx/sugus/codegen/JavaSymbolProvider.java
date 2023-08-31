package mx.sugus.codegen;

import mx.sugus.codegen.util.Name;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.ParameterizedTypeName;
import mx.sugus.javapoet.TypeName;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.SymbolReference;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;

public interface JavaSymbolProvider extends SymbolProvider {

    default ClassName toClassName(Shape shape) {
        Symbol s = toSymbol(shape);
        return ClassName.get(s.getNamespace(), s.getName());
    }

    default TypeName toTypeName(Shape shape) {
        var symbol = toSymbol(shape);
        var baseClass = ClassName.get(symbol.getNamespace(), symbol.getName());
        if (symbol.getReferences().isEmpty()) {
            return baseClass;
        }
        TypeName[] params =
            symbol.getReferences().stream().map(SymbolReference::getSymbol).map(PoetUtils::toTypeName).toArray(TypeName[]::new);
        return ParameterizedTypeName.get(baseClass, params);
    }

    default Symbol concreteClassFor(Symbol symbol) {
        return SymbolConstants.concreteClassFor(symbol);
    }

    default ClassName concreteClassFor2(Symbol symbol) {
        Symbol s = concreteClassFor(symbol);
        return ClassName.get(s.getNamespace(), s.getName());
    }
    default String toUnmodifiableCollection(Symbol symbol) {
        return SymbolConstants.toUnmodifiableCollection(symbol);
    }

    default Name toMemberJavaName(MemberShape shape) {
        var name = toMemberName(shape);
        return Name.of(name);
    }

    default Name toShapeJavaName(Shape shape) {
        var name = shape.getId().getName();
        return Name.of(name);
    }
}
