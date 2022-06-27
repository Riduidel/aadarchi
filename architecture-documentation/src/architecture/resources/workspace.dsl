workspace "aadarchi-documentation-system" {
	model {
		person_architect = person "Architect" "The architect as team scribe is the writer of this kind of documentation."
		person_stakeholder = person "Stakeholder" "All project stakeholders are readers of this kind of documentation."
		aadarchi = softwareSystem "Agile architecture documentation" {
			properties {
				"aadarchi.tickets.project" "aadarchi-documentation-system"
				"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system"
				"aadarchi.tickets.adr.label" "decision"
			}
			maven = container "maven" "The maven build engine" "Java, maven"
			aadarchi_base = container "base" "" "Java" {
				properties {
					"aadarchi.sequence.generator.with" "true"
					"aadarchi.java.source" "../base/src/main/java/"
					"aadarchi.scm.path" "base"
					"aadarchi.maven.pom" "../base/pom.xml"
					"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
					"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
					"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:base"
				}
			}
			asciidoc_39 = container "asciidoc" "The asciidoctor engine" "Java"
			archetype_6 = container "archetype" "" "maven"{
				properties {
					"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
					"aadarchi.scm.path" "archetype"
					"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
					"aadarchi.maven.pom" "../archetype/pom.xml"
					"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:archetype"
				}
			}
		}
		person_architect -> aadarchi "Writes" ""
		person_stakeholder -> aadarchi "Read" ""
		person_architect -> archetype_6 "Bootstrap a valid project" ""
		maven -> aadarchi_base "Generates diagrams and asciidoc includes" ""
		maven -> asciidoc_39 "Generates documentation as usable text in HTML/PDF/..." ""
		person_architect -> maven "Generates documentation" ""
		maven -> aadarchi_base "Generates diagrams and asciidoc includes" ""
		maven -> asciidoc_39 "Generates documentation as usable text in HTML/PDF/..." ""
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
		container "aadarchi" "system_containers" "Agile architecture containers" {
			include *
		}
		component "aadarchi_base" "base_components" "Agile architecture base components view" {
			include *
		}
	}
}