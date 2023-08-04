package mx.sugus.codegen.jv.spec3.syntax;

public interface SyntaxVisitor<T> {
    T visitMethod(MethodSyntax method);

    T visitMethodBody(MethodBodySyntax body);

    T visitParameter(ParameterSyntax parameter);

    T visitIfStatement(IfStatementSyntax statement);

    T visitForStatement(ForStatementSyntax statement);

    T visitLiteralExpression(LiteralExpression expression);

    T visitFormatExpression(FormatExpression expression);

    T visitLiteralStatement(LiteralStatement statement);

    T visitFormatStatement(FormatStatement statement);

    T visitBlockStatement(BlockStatement statement);

    T visitCodeSection(CodeSection codeSection);

    T visitClassField(ClassField classField);

    T visitClass(ClassSyntax aClass);

    T visitTryStatement(TryStatement tryStatement);

    T visitCatchClause(CatchClause catchClause);

    T visitFinallyClause(FinallyClause finallyClause);
}
