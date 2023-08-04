package mx.sugus.codegen.jv.spec3;

import javax.lang.model.element.Modifier;
import mx.sugus.codegen.jv.SymbolConstants;
import mx.sugus.codegen.jv.spec3.syntax.MethodBodySyntax;
import mx.sugus.codegen.jv.spec3.syntax.MethodSyntax;
import mx.sugus.codegen.jv.spec3.syntax.ParameterSyntax;

public class MethodSpec extends AbstractBlock<MethodSpec, MethodSyntax> {
    private final MethodSyntax.Builder methodBuilder;

    public MethodSpec(String name) {
        methodBuilder = MethodSyntax.builder(name);
    }

    public MethodSpec returns(Object returnType) {
        methodBuilder.returnType(SymbolConstants.toSymbol(returnType));
        return this;
    }

    public MethodSpec addModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) {
            methodBuilder.addModifier(modifier);
        }
        return this;
    }

    public MethodSpec addParameter(Object type, String name) {
        methodBuilder.addParameter(ParameterSyntax.builder().name(name).type(SymbolConstants.toSymbol(type)).build());
        return this;
    }

    @Override
    public MethodSyntax build() {
        var body = MethodBodySyntax.builder();
        body.addStatements(contents);
        return methodBuilder.body(body.build()).build();
    }
}
