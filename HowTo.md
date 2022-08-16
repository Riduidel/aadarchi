<h1 align="center">How to Aadarchi ?</h1>


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

Aadarchi needs a description of your architecture, which is usually done in a workspace.dsl file (but can also be done in pure Java code).

This file is an architecture description of your project in structurizr's domain-specific language. To learn more about how this DSL works, you can find all usefull informations there :
[workspace.dsl language reference](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md)

This "workspace.dsl" should be located in `src/architecture/resources/` (we will see later how to change this default path).

If you want, you can use this empty template :

```java
workspace {

        model {
            user = person "User" "A user of my software system."
            softwareSystem = softwareSystem "Software System" "My software system."
    
            user -> softwareSystem "Uses"
        }

        views {
            systemContext softwareSystem "SystemContext" {
                include *
                autoLayout
            }
        
            styles {
                element "Software System" {
                    background #1168bd
                    color #ffffff
                }
                element "Person" {
                    shape person
                    background #08427b
                    color #ffffff
                }
            }
        }

}
```
You should now be able to run `mvn install` to build your project and use Aadarchi. 

If the build success, it should have created a "structurizr" file in `target/`

## Go further

### ConfigProperties

Some properties can be modified ... where ? how ? only god knows...
