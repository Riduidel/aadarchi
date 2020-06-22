Agile architecture documentation base is the workhorse of this architecture tooling.

It bootstraps a CDI context and use it to inject the architecture model, as written by the user, 
then decorates this model using enhancers (that are free to do any meaningful operation)
before to finally write diagrams and asciidoc documents in output folder. 