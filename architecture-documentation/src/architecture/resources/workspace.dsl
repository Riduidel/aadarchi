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
					"aadarchi.java.source" "../base/src/main/java/"
					"aadarchi.scm.path" "base"
					"aadarchi.maven.pom" "../base/pom.xml"
					"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
					"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
					"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:base"
				}
				ArchitectureEnhancer = component "ArchitectureEnhancer" "Invokes all enhancers with respect for their respective priorities" "Java, CDI"
				gitlab_scm_handler = component "gitlab-scm-handler" "" "Java, gitlab"{
					properties {
						"aadarchi.java.source" "../gitlab-scm-handler/src/main/java/"
						"aadarchi.scm.path" "gitlab-scm-handler"
						"aadarchi.maven.pom" "../gitlab-scm-handler/pom.xml"
						"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
						"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
						"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:gitlab-scm-handler"
					}
				}
				GraphEmitter = component "GraphEmitter" "Generates all graph output in the destination folder" "Java, CDI"
				cdi_config_extension_35 = component "cdi-config-extension" {
					properties {
						"aadarchi.java.source" "../cdi-config-extension/src/main/java/"
						"aadarchi.scm.path" "maven-metadata-inferer"
						"aadarchi.maven.pom" "../cdi-config-extension/pom.xml"
						"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
						"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
						"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:cdi-config-extension"
					}
				}
				maven_metadata_inferer_37 = component "maven-metadata-inferer" {
					properties {
						"aadarchi.java.source" "../maven-metadata-inferer/src/main/java/"
						"aadarchi.scm.path" "maven-metadata-inferer"
						"aadarchi.maven.pom" "../maven-metadata-inferer/pom.xml"
						"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
						"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
						"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:maven-metadata-inferer"
					}
				}
				SCMReadmeReader = component "SCMReadmeReader" "Read the project readme and renders it as asciidoc" "Java"
				ArchitectureDocumentationBuilder_15 = component "ArchitectureDocumentationBuilder" "Main component reading architecture from any provider then enhancing it" "Java, CDI"
				SCMLinkGenerator = component "SCMLinkGenerator" "Generates a link to project SCM" "Java"
				ImplicitIncludeManager_17 = component "ImplicitIncludeManager" "" "Java"
				github_scm_handler_25 = component "github-scm-handler" {
					properties {
						"aadarchi.java.source" "../github-scm-handler/src/main/java/"
						"aadarchi.scm.path" "github-scm-handler"
						"aadarchi.maven.pom" "../github-scm-handler/pom.xml"
						"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
						"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
						"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:github-scm-handler"
					}
				}
				DocumentsCollector = component "DocumentsCollector" "" "Java"
				adr_tickets_extractor_31 = component "adr-tickets-extractor" {
					properties {
						"aadarchi.java.source" "../adr-tickets-extractor/src/main/java/"
						"aadarchi.scm.path" "adr-tickets-extractor"
						"aadarchi.maven.pom" "../adr-tickets-extractor/pom.xml"
						"aadarchi.scm.project" "https://github.com/Riduidel/aadarchi-documentation-system.git"
						"aadarchi.issue.manager" "https://github.com/Riduidel/aadarchi-documentation-system/issues"
						"aadarchi.maven.coordinates" "io.github.Riduidel.aadarchi-documentation-system:adr-tickets-extractor"
					}
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