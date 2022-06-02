workspace "agile-architecture-documentation-system" {

	model {
		Architect_1 = person "Architect" "The architect as team scribe is the writer of this kind of documentation."
		Stakeholder_2 = person "Stakeholder" "All project stakeholders are readers of this kind of documentation."
		Agile_architecture_documentation_3 = softwareSystem "Agile architecture documentation" {
			base_10 = container "base" {
				SCMReadmeReader_17 = component "SCMReadmeReader" {
				}
				maven_metadata_inferer_37 = component "maven-metadata-inferer" {
				}
				adr_tickets_extractor_31 = component "adr-tickets-extractor" {
				}
				GraphEmitter_16 = component "GraphEmitter" {
				}
				ArchitectureEnhancer_15 = component "ArchitectureEnhancer" {
				}
				github_scm_handler_25 = component "github-scm-handler" {
				}
				ImplicitIncludeManager_13 = component "ImplicitIncludeManager" {
				}
				cdi_config_extension_35 = component "cdi-config-extension" {
				}
				SCMLinkGenerator_11 = component "SCMLinkGenerator" {
				}
				gitlab_scm_handler_28 = component "gitlab-scm-handler" {
				}
				DocumentsCollector_12 = component "DocumentsCollector" {
				}
				ArchitectureDocumentationBuilder_14 = component "ArchitectureDocumentationBuilder" {
				}
			}
			asciidoc_39 = container "asciidoc" {
			}
			maven_8 = container "maven" {
			}
			archetype_6 = container "archetype" {
			}
		}
		ArchitectureEnhancer_15 -> ImplicitIncludeManager_13 "Generates includes for all enhancers" ""
		Architect_1 -> maven_8 "Generates documentation" ""
		ArchitectureEnhancer_15 -> DocumentsCollector_12 "Collects documents in source folder" ""
		ArchitectureEnhancer_15 -> adr_tickets_extractor_31 "Produces ADR reporting" ""
		SCMReadmeReader_17 -> github_scm_handler_25 "Get project readme" ""
		Architect_1 -> archetype_6 "Bootstrap a valid project" ""
		SCMReadmeReader_17 -> gitlab_scm_handler_28 "Get project readme" ""
		ArchitectureEnhancer_15 -> GraphEmitter_16 "Generates diagrams in PlantUML format" ""
		maven_8 -> ArchitectureDocumentationBuilder_14 "Invokes that Java executable during maven build" ""
		adr_tickets_extractor_31 -> github_scm_handler_25 "Read tickets from GitHub if configured so" ""
		ArchitectureEnhancer_15 -> SCMLinkGenerator_11 "Generates links to SCM sources" ""
		ArchitectureDocumentationBuilder_14 -> ArchitectureEnhancer_15 "Adds information to initial architecture description" ""
		maven_8 -> base_10 "Generates diagrams and asciidoc includes" ""
		maven_8 -> asciidoc_39 "Generates documentation as usable text in HTML/PDF/..." ""
		SCMLinkGenerator_11 -> gitlab_scm_handler_28 "Get project source link" ""
		adr_tickets_extractor_31 -> gitlab_scm_handler_28 "Read tickets from Gitlab if configured so" ""
		ArchitectureDocumentationBuilder_14 -> maven_metadata_inferer_37 "Infer most of element details from Maven infos" ""
		ArchitectureEnhancer_15 -> SCMReadmeReader_17 "Includes elements readme when they exist" ""
		SCMLinkGenerator_11 -> github_scm_handler_25 "Get project source link" ""
		ArchitectureDocumentationBuilder_14 -> cdi_config_extension_35 "Eases out some CDI code" ""
		Stakeholder_2 -> Agile_architecture_documentation_3 "Read" ""
		Architect_1 -> Agile_architecture_documentation_3 "Writes" ""
		SCMReadmeReader_17 -> github_scm_handler_25 "Get project readme" ""
		SCMReadmeReader_17 -> gitlab_scm_handler_28 "Get project readme" ""
		adr_tickets_extractor_31 -> gitlab_scm_handler_28 "Read tickets from Gitlab if configured so" ""
		adr_tickets_extractor_31 -> github_scm_handler_25 "Read tickets from GitHub if configured so" ""
		ArchitectureEnhancer_15 -> DocumentsCollector_12 "Collects documents in source folder" ""
		ArchitectureEnhancer_15 -> SCMLinkGenerator_11 "Generates links to SCM sources" ""
		ArchitectureEnhancer_15 -> SCMReadmeReader_17 "Includes elements readme when they exist" ""
		ArchitectureEnhancer_15 -> ImplicitIncludeManager_13 "Generates includes for all enhancers" ""
		ArchitectureEnhancer_15 -> GraphEmitter_16 "Generates diagrams in PlantUML format" ""
		ArchitectureEnhancer_15 -> adr_tickets_extractor_31 "Produces ADR reporting" ""
		SCMLinkGenerator_11 -> github_scm_handler_25 "Get project source link" ""
		SCMLinkGenerator_11 -> gitlab_scm_handler_28 "Get project source link" ""
		ArchitectureDocumentationBuilder_14 -> ArchitectureEnhancer_15 "Adds information to initial architecture description" ""
		ArchitectureDocumentationBuilder_14 -> cdi_config_extension_35 "Eases out some CDI code" ""
		ArchitectureDocumentationBuilder_14 -> maven_metadata_inferer_37 "Infer most of element details from Maven infos" ""
		maven_8 -> ArchitectureDocumentationBuilder_14 "Invokes that Java executable during maven build" ""
		maven_8 -> base_10 "Generates diagrams and asciidoc includes" ""
		maven_8 -> asciidoc_39 "Generates documentation as usable text in HTML/PDF/..." ""
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
        systemContext Agile_architecture_documentation_3 "SystemContext" {
            include *
            autoLayout
        }

        container Agile_architecture_documentation_3 "SystemContainers"  {
            include *
            autoLayout
        }
		
		component base_10 "baseComponents" {
            include *
            autoLayout
        }
	}

}