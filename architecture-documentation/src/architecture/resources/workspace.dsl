workspace "aadarchi-documentation-system" {
	model {
		person_architect = person "Architect" "The architect as team scribe is the writer of this kind of documentation."
		person_stakeholder = person "Stakeholder" "All project stakeholders are readers of this kind of documentation."
		aadarchi = softwareSystem "Aadarchi" "auto-include" {
			properties {
				"aadarchi.tickets.project" "aadarchi-documentation-system"
				"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system"
				"aadarchi.tickets.adr.label" "decision"
				"aadarchi.maven.pom" "../pom.xml"
			}
			maven = container "maven" "The maven build engine" "java, maven"
			aadarchi_maven_plugin = container "aadarchi-maven-plugin" "" "java, maven-plugin"{
				properties {
					"aadarchi.sequence.generator.with" "true"
				}
				maven -> this "Invokes this plugin during build to generate data"
			}
			aadarchi_base = container "base" "" "Java, CDI" "auto-include" {
				properties {
					"aadarchi.sequence.generator.with" "true"
				}
			}
			asciidoc_39 = container "asciidoc" "The asciidoctor engine" "java, jruby" {
				aadarchi_maven_plugin -> this
			}
			archetype_6 = container "archetype" "" "maven"
			architecture_documentation = container "architecture-documentation" "" "java, maven, structurizr, asciidoc"
		}
		person_architect -> archetype_6 "Bootstrap a valid project"
		person_architect -> maven "Generates documentation"
		person_stakeholder -> architecture_documentation "Read generated documentation"
		archetype_6 -> architecture_documentation "Generates documentation project"
	}
	views {
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
		systemContext "aadarchi" "SystemContext" "Illustration of aadarchi-documentation usage" {
			include *
		}
		container "aadarchi" "system_containers_source" "Agile architecture containers" {
			include *
		}
		component "aadarchi_base" "base_components_source" "Agile architecture base components view" {
			include *
		}
		filtered "system_containers_source" "Element,Relationship,auto-include" "system_containers_source" "Agile architecture containers" {
		    include *
		}
		filtered "base_components_source" "Element,Relationship,auto-include" "base_components_source" "Agile architecture base components view" {
        	include *
        }
	}
}