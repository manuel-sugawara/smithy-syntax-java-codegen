package mx.sugus.syntax.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SyntaxNodeRewriteVisitor implements SyntaxNodeVisitor<SyntaxNode> {
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
        MethodSyntax.Builder builder = null;
        List<Annotation> annotations = node.annotations();
        List<Annotation> newAnnotations = null;
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            Annotation newValue = (Annotation) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newAnnotations == null) {
                    newAnnotations = new ArrayList<>();
                    newAnnotations.addAll(annotations.subList(0, idx));
                }
                value = newValue;
            }
            if (newAnnotations != null) {
                newAnnotations.add(value);
            }
        }
        if (newAnnotations != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.annotations(newAnnotations);
        }
        List<Parameter> parameters = node.parameters();
        List<Parameter> newParameters = null;
        for (int idx = 0; idx < parameters.size(); idx++) {
            Parameter value = parameters.get(idx);
            Parameter newValue = (Parameter) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newParameters == null) {
                    newParameters = new ArrayList<>();
                    newParameters.addAll(parameters.subList(0, idx));
                }
                value = newValue;
            }
            if (newParameters != null) {
                newParameters.add(value);
            }
        }
        if (newParameters != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.parameters(newParameters);
        }
        Block oldBody = node.body();
        Block newBody = (Block) oldBody.accept(this);
        if (!Objects.equals(oldBody, newBody)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.body(newBody);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitInterfaceSyntax(InterfaceSyntax node) {
        InterfaceSyntax.Builder builder = null;
        List<MethodSyntax> methods = node.methods();
        List<MethodSyntax> newMethods = null;
        for (int idx = 0; idx < methods.size(); idx++) {
            MethodSyntax value = methods.get(idx);
            MethodSyntax newValue = (MethodSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newMethods == null) {
                    newMethods = new ArrayList<>();
                    newMethods.addAll(methods.subList(0, idx));
                }
                value = newValue;
            }
            if (newMethods != null) {
                newMethods.add(value);
            }
        }
        if (newMethods != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.methods(newMethods);
        }
        List<Annotation> annotations = node.annotations();
        List<Annotation> newAnnotations = null;
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            Annotation newValue = (Annotation) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newAnnotations == null) {
                    newAnnotations = new ArrayList<>();
                    newAnnotations.addAll(annotations.subList(0, idx));
                }
                value = newValue;
            }
            if (newAnnotations != null) {
                newAnnotations.add(value);
            }
        }
        if (newAnnotations != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.annotations(newAnnotations);
        }
        List<FieldSyntax> fields = node.fields();
        List<FieldSyntax> newFields = null;
        for (int idx = 0; idx < fields.size(); idx++) {
            FieldSyntax value = fields.get(idx);
            FieldSyntax newValue = (FieldSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newFields == null) {
                    newFields = new ArrayList<>();
                    newFields.addAll(fields.subList(0, idx));
                }
                value = newValue;
            }
            if (newFields != null) {
                newFields.add(value);
            }
        }
        if (newFields != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.fields(newFields);
        }
        List<TypeSyntax> innerTypes = node.innerTypes();
        List<TypeSyntax> newInnerTypes = null;
        for (int idx = 0; idx < innerTypes.size(); idx++) {
            TypeSyntax value = innerTypes.get(idx);
            TypeSyntax newValue = (TypeSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newInnerTypes == null) {
                    newInnerTypes = new ArrayList<>();
                    newInnerTypes.addAll(innerTypes.subList(0, idx));
                }
                value = newValue;
            }
            if (newInnerTypes != null) {
                newInnerTypes.add(value);
            }
        }
        if (newInnerTypes != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.innerTypes(newInnerTypes);
        }
        if (builder != null) {
            return builder.build();
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
        IfStatement.Builder builder = null;
        Expression oldExpression = node.expression();
        Expression newExpression = (Expression) oldExpression.accept(this);
        if (!Objects.equals(oldExpression, newExpression)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.expression(newExpression);
        }
        Block oldStatement = node.statement();
        Block newStatement = (Block) oldStatement.accept(this);
        if (!Objects.equals(oldStatement, newStatement)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.statement(newStatement);
        }
        Statement oldElseStatement = node.elseStatement();
        Statement newElseStatement = (Statement) oldElseStatement.accept(this);
        if (!Objects.equals(oldElseStatement, newElseStatement)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.elseStatement(newElseStatement);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitAnnotation(Annotation node) {
        return node;
    }

    @Override
    public SyntaxNode visitEnumSyntax(EnumSyntax node) {
        EnumSyntax.Builder builder = null;
        List<EnumConstant> enumConstants = node.enumConstants();
        List<EnumConstant> newEnumConstants = null;
        for (int idx = 0; idx < enumConstants.size(); idx++) {
            EnumConstant value = enumConstants.get(idx);
            EnumConstant newValue = (EnumConstant) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newEnumConstants == null) {
                    newEnumConstants = new ArrayList<>();
                    newEnumConstants.addAll(enumConstants.subList(0, idx));
                }
                value = newValue;
            }
            if (newEnumConstants != null) {
                newEnumConstants.add(value);
            }
        }
        if (newEnumConstants != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.enumConstants(newEnumConstants);
        }
        List<MethodSyntax> methods = node.methods();
        List<MethodSyntax> newMethods = null;
        for (int idx = 0; idx < methods.size(); idx++) {
            MethodSyntax value = methods.get(idx);
            MethodSyntax newValue = (MethodSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newMethods == null) {
                    newMethods = new ArrayList<>();
                    newMethods.addAll(methods.subList(0, idx));
                }
                value = newValue;
            }
            if (newMethods != null) {
                newMethods.add(value);
            }
        }
        if (newMethods != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.methods(newMethods);
        }
        List<Annotation> annotations = node.annotations();
        List<Annotation> newAnnotations = null;
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            Annotation newValue = (Annotation) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newAnnotations == null) {
                    newAnnotations = new ArrayList<>();
                    newAnnotations.addAll(annotations.subList(0, idx));
                }
                value = newValue;
            }
            if (newAnnotations != null) {
                newAnnotations.add(value);
            }
        }
        if (newAnnotations != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.annotations(newAnnotations);
        }
        List<FieldSyntax> fields = node.fields();
        List<FieldSyntax> newFields = null;
        for (int idx = 0; idx < fields.size(); idx++) {
            FieldSyntax value = fields.get(idx);
            FieldSyntax newValue = (FieldSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newFields == null) {
                    newFields = new ArrayList<>();
                    newFields.addAll(fields.subList(0, idx));
                }
                value = newValue;
            }
            if (newFields != null) {
                newFields.add(value);
            }
        }
        if (newFields != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.fields(newFields);
        }
        List<TypeSyntax> innerTypes = node.innerTypes();
        List<TypeSyntax> newInnerTypes = null;
        for (int idx = 0; idx < innerTypes.size(); idx++) {
            TypeSyntax value = innerTypes.get(idx);
            TypeSyntax newValue = (TypeSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newInnerTypes == null) {
                    newInnerTypes = new ArrayList<>();
                    newInnerTypes.addAll(innerTypes.subList(0, idx));
                }
                value = newValue;
            }
            if (newInnerTypes != null) {
                newInnerTypes.add(value);
            }
        }
        if (newInnerTypes != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.innerTypes(newInnerTypes);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitForStatement(ForStatement node) {
        ForStatement.Builder builder = null;
        Block oldStatement = node.statement();
        Block newStatement = (Block) oldStatement.accept(this);
        if (!Objects.equals(oldStatement, newStatement)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.statement(newStatement);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitBlock(Block node) {
        Block.Builder builder = null;
        List<Statement> statements = node.statements();
        List<Statement> newStatements = null;
        for (int idx = 0; idx < statements.size(); idx++) {
            Statement value = statements.get(idx);
            Statement newValue = (Statement) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newStatements == null) {
                    newStatements = new ArrayList<>();
                    newStatements.addAll(statements.subList(0, idx));
                }
                value = newValue;
            }
            if (newStatements != null) {
                newStatements.add(value);
            }
        }
        if (newStatements != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.statements(newStatements);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitClassSyntax(ClassSyntax node) {
        ClassSyntax.Builder builder = null;
        List<MethodSyntax> methods = node.methods();
        List<MethodSyntax> newMethods = null;
        for (int idx = 0; idx < methods.size(); idx++) {
            MethodSyntax value = methods.get(idx);
            MethodSyntax newValue = (MethodSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newMethods == null) {
                    newMethods = new ArrayList<>();
                    newMethods.addAll(methods.subList(0, idx));
                }
                value = newValue;
            }
            if (newMethods != null) {
                newMethods.add(value);
            }
        }
        if (newMethods != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.methods(newMethods);
        }
        List<Annotation> annotations = node.annotations();
        List<Annotation> newAnnotations = null;
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            Annotation newValue = (Annotation) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newAnnotations == null) {
                    newAnnotations = new ArrayList<>();
                    newAnnotations.addAll(annotations.subList(0, idx));
                }
                value = newValue;
            }
            if (newAnnotations != null) {
                newAnnotations.add(value);
            }
        }
        if (newAnnotations != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.annotations(newAnnotations);
        }
        List<FieldSyntax> fields = node.fields();
        List<FieldSyntax> newFields = null;
        for (int idx = 0; idx < fields.size(); idx++) {
            FieldSyntax value = fields.get(idx);
            FieldSyntax newValue = (FieldSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newFields == null) {
                    newFields = new ArrayList<>();
                    newFields.addAll(fields.subList(0, idx));
                }
                value = newValue;
            }
            if (newFields != null) {
                newFields.add(value);
            }
        }
        if (newFields != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.fields(newFields);
        }
        List<TypeSyntax> innerTypes = node.innerTypes();
        List<TypeSyntax> newInnerTypes = null;
        for (int idx = 0; idx < innerTypes.size(); idx++) {
            TypeSyntax value = innerTypes.get(idx);
            TypeSyntax newValue = (TypeSyntax) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newInnerTypes == null) {
                    newInnerTypes = new ArrayList<>();
                    newInnerTypes.addAll(innerTypes.subList(0, idx));
                }
                value = newValue;
            }
            if (newInnerTypes != null) {
                newInnerTypes.add(value);
            }
        }
        if (newInnerTypes != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.innerTypes(newInnerTypes);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitCaseClause(CaseClause node) {
        CaseClause.Builder builder = null;
        Block oldBody = node.body();
        Block newBody = (Block) oldBody.accept(this);
        if (!Objects.equals(oldBody, newBody)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.body(newBody);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitSwitchStatement(SwitchStatement node) {
        SwitchStatement.Builder builder = null;
        Expression oldExpression = node.expression();
        Expression newExpression = (Expression) oldExpression.accept(this);
        if (!Objects.equals(oldExpression, newExpression)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.expression(newExpression);
        }
        List<CaseClause> cases = node.cases();
        List<CaseClause> newCases = null;
        for (int idx = 0; idx < cases.size(); idx++) {
            CaseClause value = cases.get(idx);
            CaseClause newValue = (CaseClause) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newCases == null) {
                    newCases = new ArrayList<>();
                    newCases.addAll(cases.subList(0, idx));
                }
                value = newValue;
            }
            if (newCases != null) {
                newCases.add(value);
            }
        }
        if (newCases != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.cases(newCases);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitCatchClause(CatchClause node) {
        CatchClause.Builder builder = null;
        Block oldStatement = node.statement();
        Block newStatement = (Block) oldStatement.accept(this);
        if (!Objects.equals(oldStatement, newStatement)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.statement(newStatement);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitTryStatement(TryStatement node) {
        TryStatement.Builder builder = null;
        Block oldStatement = node.statement();
        Block newStatement = (Block) oldStatement.accept(this);
        if (!Objects.equals(oldStatement, newStatement)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.statement(newStatement);
        }
        List<CatchClause> catchClauses = node.catchClauses();
        List<CatchClause> newCatchClauses = null;
        for (int idx = 0; idx < catchClauses.size(); idx++) {
            CatchClause value = catchClauses.get(idx);
            CatchClause newValue = (CatchClause) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newCatchClauses == null) {
                    newCatchClauses = new ArrayList<>();
                    newCatchClauses.addAll(catchClauses.subList(0, idx));
                }
                value = newValue;
            }
            if (newCatchClauses != null) {
                newCatchClauses.add(value);
            }
        }
        if (newCatchClauses != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.catchClauses(newCatchClauses);
        }
        FinallyClause oldFinallyClause = node.finallyClause();
        FinallyClause newFinallyClause = (FinallyClause) oldFinallyClause.accept(this);
        if (!Objects.equals(oldFinallyClause, newFinallyClause)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.finallyClause(newFinallyClause);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitFieldSyntax(FieldSyntax node) {
        FieldSyntax.Builder builder = null;
        List<Annotation> annotations = node.annotations();
        List<Annotation> newAnnotations = null;
        for (int idx = 0; idx < annotations.size(); idx++) {
            Annotation value = annotations.get(idx);
            Annotation newValue = (Annotation) value.accept(this);
            if (!Objects.equals(value, newValue)) {
                if (newAnnotations == null) {
                    newAnnotations = new ArrayList<>();
                    newAnnotations.addAll(annotations.subList(0, idx));
                }
                value = newValue;
            }
            if (newAnnotations != null) {
                newAnnotations.add(value);
            }
        }
        if (newAnnotations != null) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.annotations(newAnnotations);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }

    @Override
    public SyntaxNode visitFinallyClause(FinallyClause node) {
        FinallyClause.Builder builder = null;
        Block oldStatement = node.statement();
        Block newStatement = (Block) oldStatement.accept(this);
        if (!Objects.equals(oldStatement, newStatement)) {
            if (builder == null) {
                builder = node.toBuilder();
            }
            builder.statement(newStatement);
        }
        if (builder != null) {
            return builder.build();
        }
        return node;
    }
}
