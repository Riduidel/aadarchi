# Spring component detector

Spring component detector configures [Structurizr-Spring](https://github.com/structurizr/java-extensions/tree/master/structurizr-spring) for optimal component detection and decoration

## Activating

For this component to be activated, it must be in the path.
Then, an agile architecture documentation system Enhancer will be registered which, 
for each `Container` which technology contains Spring, 
will start a Structurizr `ComponentFinder` configured to detect Spring annotated `@Component` and descendant annotations (`Controller`, `@Repository`, and so on).

Those detected components will be added to architecture system

## Limitations

* This detector only detects annotations, and not beans declared using XML
