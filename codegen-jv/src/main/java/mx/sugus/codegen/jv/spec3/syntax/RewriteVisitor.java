package mx.sugus.codegen.jv.spec3.syntax;

import java.util.Objects;

public class RewriteVisitor implements SyntaxVisitor<SyntaxNode> {

    public SyntaxNode visit(SyntaxNode node) {
        return node.accept(this);
    }

    @Override
    public SyntaxNode visitMethod(Method method) {
        var params = method.getParameters()
                           .stream().map(p -> p.accept(this))
                           .filter(Objects::nonNull)
                           .map(p -> (Parameter) p)
                           .toList();
        var body = (MethodBody) method.getBody().accept(this);
        return method.toBuilder()
                     .addParameters(params)
                     .body(body)
                     .build();
    }

    @Override
    public SyntaxNode visitMethodBody(MethodBody body) {
        return body;
    }

    @Override
    public SyntaxNode visitParameter(Parameter parameter) {
        return parameter;
    }

    @Override
    public SyntaxNode visitIfStatement(IfStatement ifStatement) {
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
    public SyntaxNode visitForStatement(ForStatement forStatement) {
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
    public SyntaxNode visitClassField(ClassField classField) {
        var initializer = classField.getInitializer().accept(this);
        return classField.toBuilder().initializer(initializer).build();
    }

    @Override
    public SyntaxNode visitClass(ClassSyntax aClass) {
        var fields = aClass.getFields().stream().map(n -> n.accept(this)).map(x -> (ClassField) x).toList();
        var methods = aClass.getMethods().stream().map(n -> n.accept(this)).map(x -> (Method) x).toList();
        return aClass.toBuilder()
                     .addFields(fields)
                     .addMethods(methods)
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
