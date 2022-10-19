<h1 align="center">Welcome to Aadarchi 👋</h1>
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

> A Maven archetype allowing you to easily create your agile architecture documentation using a mix of C4, Asciidoc and PlantUML. This archetype uses [Structurizr](https://github.com/structurizr/java/) to build the architecture model, and [Agile architecture documentation](https://web.archive.org/web/20210518020154/http://www.codingthearchitecture.com/2016/05/31/agile_software_architecture_documentation.html) template, all by Simon Brown.

## Install

You can use the archetype by running this maven-friendly 😅command.
Current version is ![GitHub version](https://badge.fury.io/gh/Riduidel%2Faadarchi.svg)

```sh
mvn archetype:generate -DarchetypeVersion=0.0.1 -Daadarchi-version=0.0.1 -DarchetypeGroupId=io.github.Riduidel.aadarchi -DarchetypeArtifactId=archetype 
```

This will ask you a few questions and generate the project.
Finally, don't forget to replace the value of `aadarchi-version` maven property by ![GitHub version](https://badge.fury.io/gh/Riduidel%2Faadarchi.svg)

## Usage

Once the archetype has been run, you'll have a project with a Structurizr `workspace.dsl` file in `src/architecture/resources` (conform to [Structurizr DSL syntax](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md), which means that file can be viewed using [Structurizr-lite](https://dev.to/simonbrown/getting-started-with-structurizr-lite-27d0))
and asciidoc files following Agile architecture documentation template in `src/docs/asciidoc`.

### Generating architecture documentation
Running `mvn install` will 

1. compile and run Java code to have C4 model-compatible diagrams generated by PlantUML
1. generate AsciiDoc HTML and PDF files

### Faster edit loop
A faster developer feedback loop can be achieved using our own aadarchi-maven-plugin (already configured in the archetype POM).
You can run `mvn -Plivereload` when working on documents.
This will watch the folders 
`src/docs/asciidoc` (if it exists), 
`src/slides/asciidoc` (if it exists), 
`src/main/java` (if it exists), 
and `src/architecture/resources` (if it exists)
and run a `mvn package` when any of these folders have changes in.

Visit [http://localhost:35729/docs/html/](http://localhost:35729/docs/html/) to view your generated slides in HTML form.
Visit [http://localhost:35729/slides/html/](http://localhost:35729/slides/html/) to view your generated slides in HTML form.

If you have installed the [livereload browser extension](http://livereload.com/extensions/) (but not the livereload desktop application, which job is handled by the maven build), any change in the project will be immedialety  visible in browser, allowing you to work in a pleasant environment (well, I hope)

### Best practices
* Define systems, containers and components options **only** through structurizr properties. 
The useful method for that is `ModelItem#addProperty(String, String)`. 
Don't try to load properties from other means, cause it'll introduce incoherence.
* Try to stay close to describe=>extend=>generate. In other words, first describe architecture in `Architecture` class (or `workspace.dsl` if you prefer the Structurizr DSL syntax). 
Then use available extension points (provided by CDI) to add additional infos.

#### describe=>extend=>generate
What are we talking about here ?
In fact, the simplest way to have a good model, from what we've already tested, is to

1. Create a valid and complete model, by either describing all elements or finding them (using enhancers like `MavenDetailsInfererEnhancer`)
2. Extend that model by adding associated resources (that's typically the case of the `SCMLinkGenerator` and `SCMReadmeReader`)
3. Generate the good resources, like the views (using the archetype provided `ViewsGenerator`) and the document includes

#### Writing an Enhancer
Since we're talking about the `Enhancer` interface, this is the main interface allowing us to have an extendable architecture model.
So how to write an `Enhancer` ? 
First, choose what to enhance: model or views ? 
Both of them have dedicated subinterfaces (`ModelEnhancer` and `ViewEnhancer`).
There even is a `ModelElementAdapter` that will ease things out for model enhancers, since it's the interface you may extend.
So, once you've chosen what to extend, choose when this enhancer will run by setting a priority.
This priority defines the order in which the enhacer will run, and all running enhancers are displayed ordered by priority at start of generation.
Now, you'll have to implement the visiting methods, for which you can find numerous examples in our code.
Don't forget to take a look at the `isParallel()` method, which may fasten things a lot, since it can allow the enhancer to be run using parallel features of Java system executor services.

## Developing
There are not many things to do (except improving the archetype source).
However, if you want to improve things, 
please run `mvn verify` which will create a project from the archetype and 
run `mvn package` which will trigger Java class compilation and run and Asciidoc documentation generation.

### Releasing
Can be performed only on a machine having Nicolas Delsaux GPG key allowing to sign to maven central (not yet enabled on GitHub).

Don't forget to activate the `-Prelease` profile, which enable all the good things (Sonatype staging, signing, ...)

## Architecture
Way more details are available in the [architecture documentation (which uses this system, obviously)](https://riduidel.github.io/aadarchi/).

## Author

👤 **Nicolas Delsaux**

* Twitter: [@Riduidel](https://twitter.com/Riduidel)
* Github: [@Riduidel](https://github.com/Riduidel)

## 🤝 Contributing

Contributions, issues and feature requests are welcome!<br />Feel free to check [issues page](https://github.com/Riduidel/aadarchi/issues).

## Show your support

Give a ⭐️ if this project helped you!

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_
