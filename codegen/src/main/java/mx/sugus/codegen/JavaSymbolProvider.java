package mx.sugus.codegen;

import static mx.sugus.codegen.util.PoetUtils.SUBTYPE_OF_OBJECT;

import mx.sugus.codegen.util.Name;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.javapoet.ClassName;
import mx.sugus.javapoet.TypeName;
import mx.sugus.syntax.java.InterfaceTrait;
import mx.sugus.syntax.java.IsaTrait;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;

public interface JavaSymbolProvider extends SymbolProvider {

    default ClassName toClassName(Shape shape) {
        Symbol s = toSymbol(shape);
        return ClassName.get(s.getNamespace(), s.getName());
    }

    default TypeName toSimpleTypeName(Shape shape) {
        var symbol = toSymbol(shape);
        return ClassName.get(symbol.getNamespace(), symbol.getName());
    }

    default TypeName toTypeName(Shape shape) {
        var symbol = toSymbol(shape);
        return PoetUtils.toTypeName(symbol);
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

    default String emptyReferenceBuilder(SymbolConstants.AggregateType type) {
        return SymbolConstants.emptyReferenceBuilder(type);
    }

    default String initReferenceBuilder(SymbolConstants.AggregateType type) {
        return SymbolConstants.initReferenceBuilder(type);
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
