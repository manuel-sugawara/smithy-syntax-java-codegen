# Demo Smithy Directed Code Generator for Java
This project is a simple code generator based on the following guide: [Creating A Smithy Code Generator](https://quip-amazon.com/zEN4AUzmkatk/Creating-a-Smithy-Code-Generator)

The git history is intended to provide a rough guide of the steps needed to create a code generator.
If you want to start with the bare minimum and avoid setup like gradle configuration, start at the 
following commit: [here](https://code.amazon.com/packages/Demo-Smithy-Directed-Codegen-hpm/commits/6f4fb02f177222b86bb28c2f21f3adea9a9a31a9#)

---
## What is included?
### Base Generators 
- Generation of *Enum*, *IntEnum*, *Structure* and *Error Structure* shapes 
- Handling of simple (i.e. non-third-party) imports 
- Use of `CodeSection`s for triggering integrations
- Use of Symbol decoration

### Integrations
- *JavaDoc*: Generates field comments and class JavaDocs based on `@documentation` traits in the model
- *GeneratedAnnotation*: Adds a `@Generated` annotation to all generated class files
- *ReTyping*: Demonstrates how to decorate the Symbol provider to change the type of symbol based on a trait. 
    In this case, the trait `@uuidTrait` causes the type of a string shape to be change to `UUID` in the generated classes.
- *Method*: Adds a method to a generated class based on a trait on the structure shape. In this case adding `@forkable`
    causes a method `fork()` that returns `"FORK"` is added to the generated class.

---

## What does this project not do
- Does not Generate Operation, Union, Resource, Or service code
- Does not generate 3rd party dependencies
- Does not generate a working client or server, just shape files
- Every class file in this project is created at the same level in a `models/` directory. In a real generator the
    class files should be able to generate in nested packages based on the relative model namespaces.
- (TODO) Does not handle field initialization
- Does not have adders/putters for lists and maps (i.e. `add(item)`, `put(item)`, `addAll(items)`)

### How to run
To run this project first clone the repo either with git or by adding the package via brazil. 
Then run `gradle build` to execute code generation. 

Generated code files will end up in the `/build` directory of the `codegen-test` subproject which 
generates Java code for an example service

---
### Example Generated Structure
*Smithy Model*
```smithy
@trait(selector: "string")
structure uuidTrait {}

// "pattern" is a trait.
@uuidTrait
string CityId

// CitySummary contains a reference to a City.
@references([{resource: City}])

@trait(selector: "structure")
structure forkable {}

@references([{resource: City}])
@forkable
structure CitySummary {
@required
cityId: CityId,

@required
name: String,

number: BigDecimal,
yesNo: SimpleYesNo,
}
```

*Generated Java Code*
```java
/*
 * Copyright 2022 example.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.example.weather;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import com.example.weather.CitySummary;
import java.util.UUID;
import com.example.weather.models.SimpleYesNo;

@Generated("mx.sugus.codegen.SmithyCodegenPlugin")
public final class CitySummary {
    private UUID cityId;
    private String name;
    private BigDecimal number;
    private SimpleYesNo yesNo;


    public CitySummary(UUID cityId,
                       String name,
                       BigDecimal number,
                       SimpleYesNo yesNo
    ){
        this.cityId = cityId;
        this.name = name;
        this.number = number;
        this.yesNo = yesNo;
    }


    public String fork() {
        return "FORK";
    }
    public UUID getCityId() {
        return cityId;
    }


    public String getName() {
        return name;
    }


    public BigDecimal getNumber() {
        return number;
    }


    public SimpleYesNo getYesNo() {
        return yesNo;
    }


    public void setCityId(UUID cityId) {
        this.cityId = cityId;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setNumber(BigDecimal number) {
        this.number = number;
    }


    public void setYesNo(SimpleYesNo yesNo) {
        this.yesNo = yesNo;
    }
}
```

### Example Generated Enum
*Smithy Model*
```smithy
@documentation("I do the documentation thing")
enum SimpleYesNo {
    @documentation("For Sure")
    YES,
    @documentation("No thanks")
    NO
}
```

*Generated Java Code*
```java
/*
 * Copyright 2022 example.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.example.weather.models;

import javax.annotation.processing.Generated;

/**
 * I do the documentation thing
 */
@Generated("mx.sugus.codegen.SmithyCodegenPlugin")
public enum SimpleYesNo {
    // For Sure
    YES("YES"),
    // No thanks
    NO("NO");
    
    
    private final String value;
    
    
    public SimpleYesNo(String value) {
        this.value = value;
    }
    
    
    public String getValue() {
        return value;
    }
}
```