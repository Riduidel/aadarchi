workspace "aadarchi-documentation-system" {
	model {
		person_user = person "User" "A user of this software system."
		mySystem = softwareSystem "My System" "TODO change both name and description of software system" {
			myContainer = container "My Container" "TODO change both name and description of container" {
				myComponent = component "My Component" "TODO change both name and description of component" {
					person_user -> this "Connects to application through this component"
				}
			}
		}
	}
	views {
		systemContext "mySystem" "SystemContext" "Illustration of mySystem usage" {
			include *
		}
		container "mySystem" "system_containers" "mySystem containers" {
			include *
		}
		component "myContainer" "container_components" "myContainer components" {
			include *
		}
	}
}