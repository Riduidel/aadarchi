# Maven metadata inferer

Maven metadata inferer is used to retrieve as much usefull data as possible from the `pom.xml`.

## Activating
To allow extraction of maven metadata, 
one has to set either `aadarchi.maven.pom` to the path of the maven pom for that module, 
or `aadarchi.maven.class` to a class contained in maven pom

## Extracted data
Currently extracted datas are

* technology, read from artifact type and from some well-known dependencies (typically Spring)
* maven coordinates, stored into the `aadarchi.maven.coordinates` property
* SCM url, stored into `aadarchi.scm.project`
* Issue manager url, stored into `aadarchi.issue.manager`
* Java sources folder, stored into `aadarchi.java.source`. Multiple folders are separated by the `;`character
* All maven properties are also added, prefixed by `aadarchi`
