package mx.sugus.codegen.spec3.syntax;

import java.util.Objects;

public class RewriteVisitor implements SyntaxVisitor<SyntaxNode> {

    public SyntaxNode visit(SyntaxNode node) {
        return node.accept(this);
    }

    @Override
    public SyntaxNode visitMethod(MethodSyntax method) {
        var params = method.getParameters()
                           .stream().map(p -> p.accept(this))
                           .filter(Objects::nonNull)
                           .map(p -> (ParameterSyntax) p)
                           .toList();
        var body = (MethodBodySyntax) method.getBody().accept(this);
        return method.toBuilder()
                     .addParameters(params)
                     .body(body)
                     .build();
    }

    @Override
    public SyntaxNode visitMethodBody(MethodBodySyntax body) {
        return body;
    }

    @Override
    public SyntaxNode visitParameter(ParameterSyntax parameter) {
        return parameter;
    }

    @Override
    public SyntaxNode visitIfStatement(IfStatementSyntax ifStatement) {
        var condition = ifStatement.getCondition().accept(this);
        var statement = ifStatement.getStatement().accept(this);
        var elseStatement = ifStatement.getElseStatement();
        if (elseStatement != null) {
            elseStatement = elseStatement.accept(this);
        }
        return ifStatement.toBuilder()
                          .condition(condition)
                          .statement(statement)
                          .elseStatement(elseStatement)
                          .build();
    }

    @Override
    public SyntaxNode visitForStatement(ForStatementSyntax forStatement) {
        var initializer = forStatement.getInitializer().accept(this);
        var statement = forStatement.getStatement().accept(this);
        return forStatement.toBuilder()
                           .initializer(initializer)
                           .statement(statement)
                           .build();
    }

    @Override
    public SyntaxNode visitLiteralExpression(LiteralExpression expression) {
        return expression;
    }

    @Override
    public SyntaxNode visitFormatExpression(FormatExpression expression) {
        return expression;
    }

    @Override
    public SyntaxNode visitLiteralStatement(LiteralStatement statement) {
        return statement;
    }

    @Override
    public SyntaxNode visitFormatStatement(FormatStatement statement) {
        return statement;
    }

    @Override
    public SyntaxNode visitBlockStatement(BlockStatement statement) {
        var children = statement.children().stream().map(node -> node.accept(this)).toList();
        return BlockStatement.builder().addStatements(children).build();
    }

    @Override
    public SyntaxNode visitCodeSection(CodeSection codeSection) {
        var children = codeSection.children().stream().map(node -> node.accept(this)).toList();
        return CodeSection.builder().addStatements(children).build();
    }

    @Override
    public SyntaxNode visitClassBody(ClassBody classBody) {
        var fields = classBody.getFields().stream().map(n -> n.accept(this)).map(x -> (ClassField) x).toList();
        var methods = classBody.getMethods().stream().map(n -> n.accept(this)).map(x -> (MethodSyntax) x).toList();
        return classBody.toBuilder()
                        .addFields(fields)
                        .addMethods(methods)
                        .build();
    }

    @Override
    public SyntaxNode visitClassField(ClassField classField) {
        var initializer = classField.getInitializer().accept(this);
        return classField.toBuilder().initializer(initializer).build();
    }

    @Override
    public SyntaxNode visitClass(ClassSyntax aClass) {
        var body = (ClassBody) aClass.getBody().accept(this);
        return aClass.toBuilder()
                     .body(body)
                     .build();
    }

    @Override
    public SyntaxNode visitTryStatement(TryStatement tryStatement) {
        return null;
    }

    @Override
    public SyntaxNode visitCatchClause(CatchClause catchClause) {
        return null;
    }

    @Override
    public SyntaxNode visitFinallyClause(FinallyClause finallyClause) {
        return null;
    }
}
