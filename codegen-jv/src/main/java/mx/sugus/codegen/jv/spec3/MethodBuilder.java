package mx.sugus.codegen.jv.spec3;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.SymbolConstants;
import mx.sugus.codegen.jv.spec3.syntax.MethodBody;
import mx.sugus.codegen.jv.spec3.syntax.Method;
import mx.sugus.codegen.jv.spec3.syntax.Parameter;

public final class MethodBuilder extends AbstractBlock<MethodBuilder, Method> {
    private final Method.Builder methodBuilder;

    MethodBuilder(String name) {
        methodBuilder = Method.builder(name);
    }

    MethodBuilder returns(Object returnType) {
        methodBuilder.returnType(SymbolConstants.toSymbol(returnType));
        return this;
    }

    MethodBuilder addModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) {
            methodBuilder.addModifier(modifier);
        }
        return this;
    }

    MethodBuilder addParameter(Object type, String name) {
        methodBuilder.addParameter(Parameter.builder().name(name).type(SymbolConstants.toSymbol(type)).build());
        return this;
    }

    @Override
    public Method build() {
        var body = MethodBody.builder();
        body.addStatements(contents);
        return methodBuilder.body(body.build()).build();
    }
}
