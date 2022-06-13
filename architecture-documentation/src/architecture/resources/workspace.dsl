workspace "agile-architecture-documentation-system" {
	model {
		person_architect = person "Architect" "The architect as team scribe is the writer of this kind of documentation."
		person_stakeholder = person "Stakeholder" "All project stakeholders are readers of this kind of documentation."
		aadarchi = softwareSystem "Agile architecture documentation" {
			properties {
				"agile.architecture.tickets.project" "agile-architecture-documentation-system"
				"agile.architecture.issue.manager" "https://github.com/Riduidel/agile-architecture-documentation-system"
				"agile.architecture.tickets.adr.label" "decision"
			}
			maven = container "maven" "The maven build engine" "Java, maven"
			aadarchi_base = container "base" "" "Java" {
				properties {
					"agile.architecture.java.source" "file:../base/src/main/java/"
					"agile.architecture.scm.path" "base"
					"agile.architecture.maven.pom" "file:../base/pom.xml"
					"agile.architecture.scm.project" "https://github.com/Riduidel/agile-architecture-documentation-system.git"
					"agile.architecture.issue.manager" "${issues.url}"
					"agile.architecture.maven.coordinates" "io.github.Riduidel.agile-architecture-documentation-system:base"
				}
				ArchitectureEnhancer = component "ArchitectureEnhancer" "Invokes all enhancers with respect for their respective priorities" "Java, CDI"
				gitlab_scm_handler = component "gitlab-scm-handler" "" "Java, gitlab"{
					properties {
						"agile.architecture.java.source" "file:../gitlab-scm-handler/src/main/java/"
						"agile.architecture.scm.path" "gitlab-scm-handler"
						"agile.architecture.maven.pom" "file:../gitlab-scm-handler/pom.xml"
						"agile.architecture.scm.project" "https://github.com/Riduidel/agile-architecture-documentation-system.git"
						"agile.architecture.issue.manager" "${issues.url}"
						"agile.architecture.maven.coordinates" "io.github.Riduidel.agile-architecture-documentation-system:gitlab-scm-handler"
					}
				}
				GraphEmitter = component "GraphEmitter" "Generates all graph output in the destination folder" "Java, CDI"
				cdi_config_extension_35 = component "cdi-config-extension" {
					properties {
						"agile.architecture.java.source" "file:../cdi-config-extension/src/main/java/"
						"agile.architecture.scm.path" "maven-metadata-inferer"
						"agile.architecture.maven.pom" "file:../cdi-config-extension/pom.xml"
						"agile.architecture.scm.project" "https://github.com/Riduidel/agile-architecture-documentation-system.git"
						"agile.architecture.issue.manager" "${issues.url}"
						"agile.architecture.maven.coordinates" "io.github.Riduidel.agile-architecture-documentation-system:cdi-config-extension"
					}
				}
				maven_metadata_inferer_37 = component "maven-metadata-inferer" {
					properties {
						"agile.architecture.java.source" "file:../maven-metadata-inferer/src/main/java/"
						"agile.architecture.scm.path" "maven-metadata-inferer"
						"agile.architecture.maven.pom" "file:../maven-metadata-inferer/pom.xml"
						"agile.architecture.scm.project" "https://github.com/Riduidel/agile-architecture-documentation-system.git"
						"agile.architecture.issue.manager" "${issues.url}"
						"agile.architecture.maven.coordinates" "io.github.Riduidel.agile-architecture-documentation-system:maven-metadata-inferer"
					}
				}
				SCMReadmeReader = component "SCMReadmeReader" "Read the project readme and renders it as asciidoc" "Java"
				ArchitectureDocumentationBuilder_15 = component "ArchitectureDocumentationBuilder" "Main component reading architecture from any provider then enhancing it" "Java, CDI"
				SCMLinkGenerator = component "SCMLinkGenerator" "Generates a link to project SCM" "Java"
				ImplicitIncludeManager_17 = component "ImplicitIncludeManager" "" "Java"
				github_scm_handler_25 = component "github-scm-handler" {
					properties {
						"agile.architecture.java.source" "file:../github-scm-handler/src/main/java/"
						"agile.architecture.scm.path" "github-scm-handler"
						"agile.architecture.maven.pom" "file:../github-scm-handler/pom.xml"
						"agile.architecture.scm.project" "https://github.com/Riduidel/agile-architecture-documentation-system.git"
						"agile.architecture.issue.manager" "${issues.url}"
						"agile.architecture.maven.coordinates" "io.github.Riduidel.agile-architecture-documentation-system:github-scm-handler"
					}
				}
				DocumentsCollector = component "DocumentsCollector" "" "Java"
				adr_tickets_extractor_31 = component "adr-tickets-extractor" {
					properties {
						"agile.architecture.java.source" "file:../adr-tickets-extractor/src/main/java/"
						"agile.architecture.scm.path" "adr-tickets-extractor"
						"agile.architecture.maven.pom" "file:../adr-tickets-extractor/pom.xml"
						"agile.architecture.scm.project" "https://github.com/Riduidel/agile-architecture-documentation-system.git"
						"agile.architecture.issue.manager" "${issues.url}"
						"agile.architecture.maven.coordinates" "io.github.Riduidel.agile-architecture-documentation-system:adr-tickets-extractor"
					}
				}
			}
			asciidoc_39 = container "asciidoc" "The asciidoctor engine" "Java"
			archetype_6 = container "archetype" "" "maven"{
				properties {
					"agile.architecture.scm.project" "https://github.com/Riduidel/agile-architecture-documentation-system.git"
					"agile.architecture.scm.path" "archetype"
					"agile.architecture.issue.manager" "${issues.url}"
					"agile.architecture.maven.pom" "file:../archetype/pom.xml"
					"agile.architecture.maven.coordinates" "io.github.Riduidel.agile-architecture-documentation-system:archetype"
				}
			}
		}
		ArchitectureEnhancer -> ImplicitIncludeManager_17 "Generates includes for all enhancers" ""
		person_architect -> aadarchi "Writes" ""
		adr_tickets_extractor_31 -> github_scm_handler_25 "Read tickets from GitHub if configured so" ""
		ArchitectureDocumentationBuilder_15 -> maven_metadata_inferer_37 "Infer most of element details from Maven infos" ""
		ArchitectureEnhancer -> SCMReadmeReader "Includes elements readme when they exist" ""
		person_stakeholder -> aadarchi "Read" ""
		ArchitectureEnhancer -> SCMLinkGenerator "Generates links to SCM sources" ""
		adr_tickets_extractor_31 -> gitlab_scm_handler "Read tickets from Gitlab if configured so" ""
		person_architect -> archetype_6 "Bootstrap a valid project" ""
		ArchitectureDocumentationBuilder_15 -> cdi_config_extension_35 "Eases out some CDI code" ""
		SCMLinkGenerator -> gitlab_scm_handler "Get project source link" ""
		maven -> aadarchi_base "Generates diagrams and asciidoc includes" ""
		ArchitectureEnhancer -> DocumentsCollector "Collects documents in source folder" ""
		maven -> ArchitectureDocumentationBuilder_15 "Invokes that Java executable during maven build" ""
		maven -> asciidoc_39 "Generates documentation as usable text in HTML/PDF/..." ""
		SCMReadmeReader -> gitlab_scm_handler "Get project readme" ""
		person_architect -> maven "Generates documentation" ""
		SCMLinkGenerator -> github_scm_handler_25 "Get project source link" ""
		ArchitectureEnhancer -> adr_tickets_extractor_31 "Produces ADR reporting" ""
		ArchitectureDocumentationBuilder_15 -> ArchitectureEnhancer "Adds information to initial architecture description" ""
		ArchitectureEnhancer -> GraphEmitter "Generates diagrams in PlantUML format" ""
		SCMReadmeReader -> github_scm_handler_25 "Get project readme" ""
		maven -> ArchitectureDocumentationBuilder_15 "Invokes that Java executable during maven build" ""
		maven -> aadarchi_base "Generates diagrams and asciidoc includes" ""
		maven -> asciidoc_39 "Generates documentation as usable text in HTML/PDF/..." ""
		ArchitectureEnhancer -> DocumentsCollector "Collects documents in source folder" ""
		ArchitectureEnhancer -> SCMLinkGenerator "Generates links to SCM sources" ""
		ArchitectureEnhancer -> SCMReadmeReader "Includes elements readme when they exist" ""
		ArchitectureEnhancer -> ImplicitIncludeManager_17 "Generates includes for all enhancers" ""
		ArchitectureEnhancer -> GraphEmitter "Generates diagrams in PlantUML format" ""
		ArchitectureEnhancer -> adr_tickets_extractor_31 "Produces ADR reporting" ""
		SCMReadmeReader -> github_scm_handler_25 "Get project readme" ""
		SCMReadmeReader -> gitlab_scm_handler "Get project readme" ""
		ArchitectureDocumentationBuilder_15 -> ArchitectureEnhancer "Adds information to initial architecture description" ""
		ArchitectureDocumentationBuilder_15 -> cdi_config_extension_35 "Eases out some CDI code" ""
		ArchitectureDocumentationBuilder_15 -> maven_metadata_inferer_37 "Infer most of element details from Maven infos" ""
		SCMLinkGenerator -> github_scm_handler_25 "Get project source link" ""
		SCMLinkGenerator -> gitlab_scm_handler "Get project source link" ""
		adr_tickets_extractor_31 -> gitlab_scm_handler "Read tickets from Gitlab if configured so" ""
		adr_tickets_extractor_31 -> github_scm_handler_25 "Read tickets from GitHub if configured so" ""
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
		systemContext "aadarchi" "SystemContext" "Illustration of agile-architecture-documentation usage" {
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