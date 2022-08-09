<h1 align="center">How to Aadarchi ?</h1>
<p>
	<a href="https://github.com/Riduidel/aadarchi/actions?query=workflow%3A%22Java+CI+with+Maven%22">
<img alt="GitHub Workflow Status" src="https://img.shields.io/github/workflow/status/Riduidel/aadarchi/Java%20CI%20with%20Maven">
	</a>
  <a href="https://github.com/Riduidel/aadarchi/releases" target="_blank"><img src="https://badge.fury.io/gh/Riduidel%2Faadarchi.svg" alt="GitHub version"></a>
<img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/m/Riduidel/aadarchi">
  <a href="https://twitter.com/Riduidel" target="_blank">
    <img alt="Twitter: Riduidel" src="https://img.shields.io/twitter/follow/Riduidel.svg?style=social" />
  </a>
</p>

> A Maven archetype allowing you to easily create your agile architecture documentation using a mix of C4, Asciidoc and PlantUML. This archetype uses [Structurizr](https://github.com/structurizr/java/) to build the architecture model, and [Agile architecture documentation](http://www.codingthearchitecture.com/2016/05/31/agile_software_architecture_documentation.html) template, all by Simon Brown.

## Install

To add Aadarchi plugin to your project you need to add it to your pom.xml.<br>
You can copy and paste this in your build's plugins section :

```xml
<plugin>
    <groupId>io.github.Riduidel.aadarchi</groupId>
    <artifactId>aadarchi-maven-plugin</artifactId>
    <version>0.1.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-model</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Don't forget to replace the value of `version` maven property by ![GitHub version](https://badge.fury.io/gh/Riduidel%2Faadarchi.svg)

## Usage

First of all, you must be aware that Aadarchi is used when the project build. Therefore you can deduct that your project need to be abble to build.

Then, Aadarchi need a file named "workspace.dsl" to run.
This file is an architecture description of your project in structurizr's language. To learn more about how this DSL works, you can find any usefull information there :
https://github.com/structurizr/dsl/blob/master/docs/language-reference.md

This "workspace.dsl" should be located in `src/architecture/resources/` (we will see later how to change this default path).

If you want, you can use this empty template :

```java
workspace "Project-Name" {

    model {
        /* here a description of your model */
    }
    
    views {
        style {
            /* here some custom views and styles */
        }
    }
}
```
You should now be able to run `mvn install` to build your project and use Aadarchi. 

If the build success, it should have created a "structurizr" file in target/`

## Go further

### ToDsl
It's possible to let Aadarchi generate your workspace.dsl automatically from the informations you got in your model.

To do so, you must enable the dsl generation when running the build. One of the solutions is to add an argument :
`mvn -Daadarchi.todsl.enabled=true install`

### ConfigProperties

Some properties can be modified ...

## Author

👤 **Nicolas Delsaux**<br>
👤 **Jason Sycz**

* Twitter: [@Riduidel](https://twitter.com/Riduidel)
* Github: [@Riduidel](https://github.com/Riduidel)

## 🤝 Contributing

Contributions, issues and feature requests are welcome!<br />Feel free to check [issues page](https://github.com/Riduidel/aadarchi/issues).

## Show your support

Give a ⭐️ if this project helped you!

