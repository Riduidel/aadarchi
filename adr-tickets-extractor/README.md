# adr-tickets-extractor
This module provides tooling for extracting architecture decisions from an issue tracker configured for each requiring structurizr element.

The rationale is that decisions are in fact team discussion, which are poorly captured in static text documents.
As a consequence, it is way more efficient to use issue trackers 
(which precise role is to track discussions about software evolution)
and have the issue tracker content emitted as documentation.

# How to use
This is exposed in [agile architecture documentation example](https://github.com/Riduidel/agile-architecture-documentation-example/).

This obviously requires a valid SCM handler.

* Add the dependency to your POM:

```xml
		<dependency>
			<groupId>com.github.Riduidel.agile-architecture-documentation-system</groupId>
			<artifactId>adr-tickets-extractor</artifactId>
			<version>${version.agile-architecture}</version>
		</dependency>

```

* Add the property to the model element supposed to contain architecture decision records. Typically, it will be your `SoftwareSystem`.

```
		SoftwareSystem kafkatrain = model.addSoftwareSystem("kafkatrain", "Crowd-sourced transport timetable prediction system");
		kafkatrain.addProperty(ADRExtractor.AGILE_ARCHITECTURE_TICKETS_PROJECT, "https://github.com/Riduidel/agile-architecture-documentation-example/");
```

And that's all! the auto-include feature of agile-architecture-documentation-system will make sure the tickets are included, so the only thing you have to do is to go into the decision-log section to add a decision indicating your desire to switch to decisions stored in an issue tracker.