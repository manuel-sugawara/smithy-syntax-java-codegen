package mx.sugus.syntax.java;

import java.util.Objects;
import mx.sugus.javapoet.TypeName;

public final class GetRootNodeRequest {
    private final ClassSyntax value;

    private final FormatterString formatterString;

    private final FormatterLiteral formatterLiteral;

    private final FormatterTypeName formatterTypeName;

    private final FormatterName formatterName;

    private final TypeName typeName;

    private final Object javaObject;

    private final SyntaxFormatter syntaxFormatter;

    private final Expression expression;

    private final ExpressionFormatter expressionFormatter;

    private final StatementFormatter statementFormatter;

    private final IfStatement ifStatement;

    private final ForStatement forStatement;

    private final TryStatement tryStatement;

    private final SwitchStatement switchStatement;

    private final EnumSyntax enumSyntax;

    private final InterfaceSyntax interfaceSyntax;

    private final TypeSyntax typeSyntax;

    private GetRootNodeRequest(Builder builder) {
        this.value = Objects.requireNonNull(builder.value, "value");
        this.formatterString = Objects.requireNonNull(builder.formatterString, "formatterString");
        this.formatterLiteral = Objects.requireNonNull(builder.formatterLiteral, "formatterLiteral");
        this.formatterTypeName = Objects.requireNonNull(builder.formatterTypeName, "formatterTypeName");
        this.formatterName = Objects.requireNonNull(builder.formatterName, "formatterName");
        this.typeName = Objects.requireNonNull(builder.typeName, "typeName");
        this.javaObject = Objects.requireNonNull(builder.javaObject, "javaObject");
        this.syntaxFormatter = Objects.requireNonNull(builder.syntaxFormatter, "syntaxFormatter");
        this.expression = Objects.requireNonNull(builder.expression, "expression");
        this.expressionFormatter = Objects.requireNonNull(builder.expressionFormatter, "expressionFormatter");
        this.statementFormatter = Objects.requireNonNull(builder.statementFormatter, "statementFormatter");
        this.ifStatement = Objects.requireNonNull(builder.ifStatement, "ifStatement");
        this.forStatement = Objects.requireNonNull(builder.forStatement, "forStatement");
        this.tryStatement = Objects.requireNonNull(builder.tryStatement, "tryStatement");
        this.switchStatement = Objects.requireNonNull(builder.switchStatement, "switchStatement");
        this.enumSyntax = Objects.requireNonNull(builder.enumSyntax, "enumSyntax");
        this.interfaceSyntax = Objects.requireNonNull(builder.interfaceSyntax, "interfaceSyntax");
        this.typeSyntax = Objects.requireNonNull(builder.typeSyntax, "typeSyntax");
    }

    public ClassSyntax value() {
        return this.value;
    }

    public FormatterString formatterString() {
        return this.formatterString;
    }

    public FormatterLiteral formatterLiteral() {
        return this.formatterLiteral;
    }

    public FormatterTypeName formatterTypeName() {
        return this.formatterTypeName;
    }

    public FormatterName formatterName() {
        return this.formatterName;
    }

    public TypeName typeName() {
        return this.typeName;
    }

    public Object javaObject() {
        return this.javaObject;
    }

    public SyntaxFormatter syntaxFormatter() {
        return this.syntaxFormatter;
    }

    public Expression expression() {
        return this.expression;
    }

    public ExpressionFormatter expressionFormatter() {
        return this.expressionFormatter;
    }

    public StatementFormatter statementFormatter() {
        return this.statementFormatter;
    }

    public IfStatement ifStatement() {
        return this.ifStatement;
    }

    public ForStatement forStatement() {
        return this.forStatement;
    }

    public TryStatement tryStatement() {
        return this.tryStatement;
    }

    public SwitchStatement switchStatement() {
        return this.switchStatement;
    }

    public EnumSyntax enumSyntax() {
        return this.enumSyntax;
    }

    public InterfaceSyntax interfaceSyntax() {
        return this.interfaceSyntax;
    }

    public TypeSyntax typeSyntax() {
        return this.typeSyntax;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GetRootNodeRequest)) {
            return false;
        }
        GetRootNodeRequest other = (GetRootNodeRequest) obj;
        return this.value.equals(other.value)
             && this.formatterString.equals(other.formatterString)
             && this.formatterLiteral.equals(other.formatterLiteral)
             && this.formatterTypeName.equals(other.formatterTypeName)
             && this.formatterName.equals(other.formatterName)
             && this.typeName.equals(other.typeName)
             && this.javaObject.equals(other.javaObject)
             && this.syntaxFormatter.equals(other.syntaxFormatter)
             && this.expression.equals(other.expression)
             && this.expressionFormatter.equals(other.expressionFormatter)
             && this.statementFormatter.equals(other.statementFormatter)
             && this.ifStatement.equals(other.ifStatement)
             && this.forStatement.equals(other.forStatement)
             && this.tryStatement.equals(other.tryStatement)
             && this.switchStatement.equals(other.switchStatement)
             && this.enumSyntax.equals(other.enumSyntax)
             && this.interfaceSyntax.equals(other.interfaceSyntax)
             && this.typeSyntax.equals(other.typeSyntax);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + value.hashCode();
        hashCode = 31 * hashCode + formatterString.hashCode();
        hashCode = 31 * hashCode + formatterLiteral.hashCode();
        hashCode = 31 * hashCode + formatterTypeName.hashCode();
        hashCode = 31 * hashCode + formatterName.hashCode();
        hashCode = 31 * hashCode + typeName.hashCode();
        hashCode = 31 * hashCode + javaObject.hashCode();
        hashCode = 31 * hashCode + syntaxFormatter.hashCode();
        hashCode = 31 * hashCode + expression.hashCode();
        hashCode = 31 * hashCode + expressionFormatter.hashCode();
        hashCode = 31 * hashCode + statementFormatter.hashCode();
        hashCode = 31 * hashCode + ifStatement.hashCode();
        hashCode = 31 * hashCode + forStatement.hashCode();
        hashCode = 31 * hashCode + tryStatement.hashCode();
        hashCode = 31 * hashCode + switchStatement.hashCode();
        hashCode = 31 * hashCode + enumSyntax.hashCode();
        hashCode = 31 * hashCode + interfaceSyntax.hashCode();
        hashCode = 31 * hashCode + typeSyntax.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "GetRootNodeRequest{"
             + "value: " + value
             + ", formatterString: " + formatterString
             + ", formatterLiteral: " + formatterLiteral
             + ", formatterTypeName: " + formatterTypeName
             + ", formatterName: " + formatterName
             + ", typeName: " + typeName
             + ", javaObject: " + javaObject
             + ", syntaxFormatter: " + syntaxFormatter
             + ", expression: " + expression
             + ", expressionFormatter: " + expressionFormatter
             + ", statementFormatter: " + statementFormatter
             + ", ifStatement: " + ifStatement
             + ", forStatement: " + forStatement
             + ", tryStatement: " + tryStatement
             + ", switchStatement: " + switchStatement
             + ", enumSyntax: " + enumSyntax
             + ", interfaceSyntax: " + interfaceSyntax
             + ", typeSyntax: " + typeSyntax + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ClassSyntax value;

        private FormatterString formatterString;

        private FormatterLiteral formatterLiteral;

        private FormatterTypeName formatterTypeName;

        private FormatterName formatterName;

        private TypeName typeName;

        private Object javaObject;

        private SyntaxFormatter syntaxFormatter;

        private Expression expression;

        private ExpressionFormatter expressionFormatter;

        private StatementFormatter statementFormatter;

        private IfStatement ifStatement;

        private ForStatement forStatement;

        private TryStatement tryStatement;

        private SwitchStatement switchStatement;

        private EnumSyntax enumSyntax;

        private InterfaceSyntax interfaceSyntax;

        private TypeSyntax typeSyntax;

        private boolean _built;

        Builder() {
        }

        Builder(GetRootNodeRequest data) {
            this.value = data.value;
            this.formatterString = data.formatterString;
            this.formatterLiteral = data.formatterLiteral;
            this.formatterTypeName = data.formatterTypeName;
            this.formatterName = data.formatterName;
            this.typeName = data.typeName;
            this.javaObject = data.javaObject;
            this.syntaxFormatter = data.syntaxFormatter;
            this.expression = data.expression;
            this.expressionFormatter = data.expressionFormatter;
            this.statementFormatter = data.statementFormatter;
            this.ifStatement = data.ifStatement;
            this.forStatement = data.forStatement;
            this.tryStatement = data.tryStatement;
            this.switchStatement = data.switchStatement;
            this.enumSyntax = data.enumSyntax;
            this.interfaceSyntax = data.interfaceSyntax;
            this.typeSyntax = data.typeSyntax;
        }

        public Builder value(ClassSyntax value) {
            this.value = value;
            return this;
        }

        public Builder formatterString(FormatterString formatterString) {
            this.formatterString = formatterString;
            return this;
        }

        public Builder formatterLiteral(FormatterLiteral formatterLiteral) {
            this.formatterLiteral = formatterLiteral;
            return this;
        }

        public Builder formatterTypeName(FormatterTypeName formatterTypeName) {
            this.formatterTypeName = formatterTypeName;
            return this;
        }

        public Builder formatterName(FormatterName formatterName) {
            this.formatterName = formatterName;
            return this;
        }

        public Builder typeName(TypeName typeName) {
            this.typeName = typeName;
            return this;
        }

        public Builder javaObject(Object javaObject) {
            this.javaObject = javaObject;
            return this;
        }

        public Builder syntaxFormatter(SyntaxFormatter syntaxFormatter) {
            this.syntaxFormatter = syntaxFormatter;
            return this;
        }

        public Builder expression(Expression expression) {
            this.expression = expression;
            return this;
        }

        public Builder expressionFormatter(ExpressionFormatter expressionFormatter) {
            this.expressionFormatter = expressionFormatter;
            return this;
        }

        public Builder statementFormatter(StatementFormatter statementFormatter) {
            this.statementFormatter = statementFormatter;
            return this;
        }

        public Builder ifStatement(IfStatement ifStatement) {
            this.ifStatement = ifStatement;
            return this;
        }

        public Builder forStatement(ForStatement forStatement) {
            this.forStatement = forStatement;
            return this;
        }

        public Builder tryStatement(TryStatement tryStatement) {
            this.tryStatement = tryStatement;
            return this;
        }

        public Builder switchStatement(SwitchStatement switchStatement) {
            this.switchStatement = switchStatement;
            return this;
        }

        public Builder enumSyntax(EnumSyntax enumSyntax) {
            this.enumSyntax = enumSyntax;
            return this;
        }

        public Builder interfaceSyntax(InterfaceSyntax interfaceSyntax) {
            this.interfaceSyntax = interfaceSyntax;
            return this;
        }

        public Builder typeSyntax(TypeSyntax typeSyntax) {
            this.typeSyntax = typeSyntax;
            return this;
        }

        public GetRootNodeRequest build() {
            return new GetRootNodeRequest(this);
        }
    }
}
