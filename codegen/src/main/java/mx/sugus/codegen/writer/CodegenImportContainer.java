package mx.sugus.codegen.writer;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import software.amazon.smithy.codegen.core.ImportContainer;
import software.amazon.smithy.codegen.core.Symbol;

public class CodeGenImportContainer implements ImportContainer {
    private final String packagename;
    private final Set<Symbol> imports;

    CodeGenImportContainer(String packagename) {
        this.packagename = packagename;
        this.imports = new TreeSet<>(Comparator.comparing(Symbol::getFullName));
    }

    @Override
    public void importSymbol(Symbol symbol, String s) {
        // No aliasing support, import the symbol as-is.
        importSymbol(symbol);
    }

    @Override
    public void importSymbol(Symbol symbol) {
        var namespace = symbol.getNamespace();
        if (!namespace.startsWith("java.lang")
            && !namespace.equals(packagename)
            && !namespace.isEmpty()
        ) {
            imports.add(symbol);
        }
    }

    @Override
    public String toString() {
        if (imports.isEmpty()) {
            return "";
        }
        var buf = new StringBuilder();
        imports.forEach(dependency ->
                            buf.append("import ")
                               .append(dependency.getFullName())
                               .append(";\n"));
        buf.append("\n");
        return buf.toString();
    }
}
