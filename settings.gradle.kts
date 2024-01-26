pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri( "https://jitpack.io") }
        //maven { url = uri("/Users/petermernik/Work/Development/MavenLocal/Repo") }
        maven {
            url = uri("https://maven.pkg.github.com/paywings/integration")
            credentials {
                username = "PayWings-Integration"
                password = "@CheckDocumentationForValidToken"
            }
        }
    }
}

rootProject.name = "PayWingsKYCAndroidSDKSampleApp"
include(":app")
