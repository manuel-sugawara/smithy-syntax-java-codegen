package mx.sugus.syntax.java;

import java.util.List;

public class SyntaxNodeWalkVisitor implements SyntaxNodeVisitor<SyntaxNode> {
    @Override
    public SyntaxNode visitEnumConstant(EnumConstant node) {
        return node;
    }

    @Override
    public SyntaxNode visitStatementFormatter(StatementFormatter node) {
        return node;
    }

    @Override
    public SyntaxNode visitExpressionFormatter(ExpressionFormatter node) {
        return node;
    }

    @Override
    public SyntaxNode visitMethodSyntax(MethodSyntax node) {
        List<Annotation> annotations = node.annotations();
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            value.accept(this);
        }
        List<Parameter> parameters = node.parameters();
        for (int idx = 0; idx < parameters.size(); idx++) {
            Parameter value = parameters.get(idx);
            value.accept(this);
        }
        Block body = node.body();
        if (body != null) {
            body.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitInterfaceSyntax(InterfaceSyntax node) {
        List<MethodSyntax> methods = node.methods();
        for (int idx = 0; idx < methods.size(); idx++) {
            MethodSyntax value = methods.get(idx);
            value.accept(this);
        }
        List<Annotation> annotations = node.annotations();
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            value.accept(this);
        }
        List<FieldSyntax> fields = node.fields();
        for (int idx = 0; idx < fields.size(); idx++) {
            FieldSyntax value = fields.get(idx);
            value.accept(this);
        }
        List<TypeSyntax> innerTypes = node.innerTypes();
        for (int idx = 0; idx < innerTypes.size(); idx++) {
            TypeSyntax value = innerTypes.get(idx);
            value.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitSyntaxFormatter(SyntaxFormatter node) {
        return node;
    }

    @Override
    public SyntaxNode visitParameter(Parameter node) {
        return node;
    }

    @Override
    public SyntaxNode visitIfStatement(IfStatement node) {
        node.expression().accept(this);
        node.statement().accept(this);
        Statement elseStatement = node.elseStatement();
        if (elseStatement != null) {
            elseStatement.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitAnnotation(Annotation node) {
        return node;
    }

    @Override
    public SyntaxNode visitEnumSyntax(EnumSyntax node) {
        List<EnumConstant> enumConstants = node.enumConstants();
        for (int idx = 0; idx < enumConstants.size(); idx++) {
            EnumConstant value = enumConstants.get(idx);
            value.accept(this);
        }
        List<MethodSyntax> methods = node.methods();
        for (int idx = 0; idx < methods.size(); idx++) {
            MethodSyntax value = methods.get(idx);
            value.accept(this);
        }
        List<Annotation> annotations = node.annotations();
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            value.accept(this);
        }
        List<FieldSyntax> fields = node.fields();
        for (int idx = 0; idx < fields.size(); idx++) {
            FieldSyntax value = fields.get(idx);
            value.accept(this);
        }
        List<TypeSyntax> innerTypes = node.innerTypes();
        for (int idx = 0; idx < innerTypes.size(); idx++) {
            TypeSyntax value = innerTypes.get(idx);
            value.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitForStatement(ForStatement node) {
        node.statement().accept(this);
        return node;
    }

    @Override
    public SyntaxNode visitBlock(Block node) {
        List<Statement> statements = node.statements();
        for (int idx = 0; idx < statements.size(); idx++) {
            Statement value = statements.get(idx);
            value.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitClassSyntax(ClassSyntax node) {
        List<MethodSyntax> methods = node.methods();
        for (int idx = 0; idx < methods.size(); idx++) {
            MethodSyntax value = methods.get(idx);
            value.accept(this);
        }
        List<Annotation> annotations = node.annotations();
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            value.accept(this);
        }
        List<FieldSyntax> fields = node.fields();
        for (int idx = 0; idx < fields.size(); idx++) {
            FieldSyntax value = fields.get(idx);
            value.accept(this);
        }
        List<TypeSyntax> innerTypes = node.innerTypes();
        for (int idx = 0; idx < innerTypes.size(); idx++) {
            TypeSyntax value = innerTypes.get(idx);
            value.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitCaseClause(CaseClause node) {
        node.body().accept(this);
        return node;
    }

    @Override
    public SyntaxNode visitSwitchStatement(SwitchStatement node) {
        node.expression().accept(this);
        List<CaseClause> cases = node.cases();
        for (int idx = 0; idx < cases.size(); idx++) {
            CaseClause value = cases.get(idx);
            value.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitCatchClause(CatchClause node) {
        node.statement().accept(this);
        return node;
    }

    @Override
    public SyntaxNode visitTryStatement(TryStatement node) {
        node.statement().accept(this);
        List<CatchClause> catchClauses = node.catchClauses();
        for (int idx = 0; idx < catchClauses.size(); idx++) {
            CatchClause value = catchClauses.get(idx);
            value.accept(this);
        }
        FinallyClause finallyClause = node.finallyClause();
        if (finallyClause != null) {
            finallyClause.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitFieldSyntax(FieldSyntax node) {
        List<Annotation> annotations = node.annotations();
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            value.accept(this);
        }
        return node;
    }

    @Override
    public SyntaxNode visitFinallyClause(FinallyClause node) {
        node.statement().accept(this);
        return node;
    }
}
