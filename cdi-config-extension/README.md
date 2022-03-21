This component contains some CDI extension.
Typically, there is

* `LogProducer`, very useful to have loggers injected with the right name by simply writing `@Inject Logger logger;`
* `FileConfigPropertyProducer`, which allows injection of File path from configuration.