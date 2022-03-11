# Maven metadata inferer

Maven metadata inferer is used to retrieve as much usefull data as possible from the `pom.xml`.

## Activating
To allow extraction of maven metadata, 
one has to set either `agile.architecture.maven.pom` to the path of the maven pom for that module, 
or `agile.architecture.maven.class` to a class contained in maven pom

## Extracted data
Currently extracted datas are

* technology, read from artifact type and from some well-known dependencies (typically Spring)
* maven coordinates, stored into the `agile.architecture.maven.coordinates` property
* SCM url, stored into `agile.architecture.scm.project`
* Issue manager url, stored into `agile.architecture.issue.manager`
* Java sources folder, stored into `agile.architecture.java.source`. Multiple folders are separated by the `;`character
* All maven properties are also added, prefixed by `agile.architecture`
