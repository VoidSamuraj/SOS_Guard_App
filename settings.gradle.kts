import java.util.Properties

// Load secret.properties file
val secretProperties = Properties()
val secretFile = file("secret.properties")
if (secretFile.exists()) {
    secretProperties.load(secretFile.inputStream())
}

// Retrieve the Mapbox token from the properties file
val mapboxToken: String = secretProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN") ?: ""
    gradle.extensions.extraProperties.set("mapboxToken", mapboxToken)
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = mapboxToken
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "awpfog"
include(":app")
 