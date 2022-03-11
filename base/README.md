# Agile architecture documentation base

Agile architecture documentation base is the workhorse of this architecture tooling.

It bootstraps a CDI context and use it to inject the architecture model, as written by the user, 
then decorates this model using enhancers (that are free to do any meaningful operation)
before to finally write diagrams and asciidoc documents in output folder.

Various components are available to enhance generated documentation.

## Configuration
There are two types of configuration available for this project.

### System properties
Global configuration is set via system property given on command-line (see in pom for examples).
Typical examples are the `force` flag, or output folder.

### Model element properties
Each structurizr model element can receive additional properties in Java code, through calls to `Element#addProperty(key, value)`.
This configuration mode is to be used for each element requiring specific configuration (typically, SCM address is configured this way).

## `ArchitectureEnhancer`
This class will run all model and views enhancers, and is used to define the folder in which data is stored.
This storage folder (referenced in this document as `${ENHANCEMENTS_DIR}`) is configured by the `agile.architecture.enhancements` which uses DeltaSpike configuration loading mechanism and has no default value in code, but one is set in pom generated from archetype (value is `${agile.architecture.output.enhancements}` maven property, which resolves to `target/structurizr/enhancements`).

When available, all components configuration properties are mentionned in this document.

## Organization of storage in `${ENHANCEMENTS_DIR}`
TO have things reasonably sorted, we generate the following structure in `${ENHANCEMENTS_DIR}` depending upon three elements

1. The structurizr model element for which the file is generated
1. The enhancer priority and name
1. A file identifier suffix, eventually containing the file type, but potentially having more content in)

This organization is enforced by the OutputBuidler interface in this module, which only implementation is `ArchitectureEnhancer`.

## Enhancers
Then come the turn of the various enhancers, which will add various elements.
They're described here in the order defined by the `Enhancer#priority()|  method.

### `ImplicitIncludeManager`

This folder will look for files defined in each section associated content folder.
THis content folder is defined by concatenating `asciidoc.source.dir` configuration property (which resolves to `src/docs/asciidoc`) and the section folder name (which is `${section_number}_${section_name}`, exactly like file names).
For each folder having content, a file will be generated in `${ENHANCEMENTS_DIR}` that will include content to have it in final doc without having to write any include.

This enhancer is set as first, to make sure the content written in documentation is always displayed before any kind of automated report.

This behaviour cannot be deactivated.

### `SCMLinkGenerator`

This component will generate a link to the SCM repository containing the structurizr element code. It makes use of any implementation of `SCMHandler` that is present in CLASSPATH to load the file.

#### Configuration
##### System properties
The component can be forced to rewrite the generated file by the common `force` flag.

##### Model element configuration
This component is activated only for model element having a value for the `agile.architecture.scm.project` property.
additionally, path to element in repository can be configured with the `agile.architecture.scm.path` property, also to define in Architure class.

### `SCMReadmeReader`

This component will include the Readme of any model element for which the `agile.architecture.scm.project` property is defined.

If readme is a markdown file, it will be converted to asciidoc before to be included.

Notice that links will not be rewritten, so avoid relative links.


#### Configuration
##### System properties
The component can be forced to rewrite the generated file by the common `force` flag.

##### Model element configuration
This component is activated only for model element having a value for the `agile.architecture.scm.project` property.
Additionally, path to element in repository can be configured with the `agile.architecture.scm.path` property, also to define in Architure class.
And readme name can be configured by setting the `agile.architecture.scm.readme.path` which will be concatenated to `agile.architecture.scm.path`.

### `DocumentsCollector`
This component is an internal none, that will collect all files in `${ENHANCEMENTS_DIR}` and includes them in the good sections.

### `GraphEmitter`

This component generates all the diagrams defined in Architecture class.

#### Configuration
##### System properties
The component can be forced to rewrite the generated files by the common `force` flag.

Diagrams will be generated in `agile.architecture.diagrams` folder, which defaults in Java code to `target/structurizr/architecture`.
