import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.dokka)
    alias(libs.plugins.signing)
}

android {
    namespace = "com.tanishranjan.cropkit"
    compileSdk = 35
    version = "1.0.0"

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// --- Helper to Load Properties Safely ---
// Load properties from user's gradle.properties
val userGradleProperties = Properties()
val userGradlePropertiesFile = file("${System.getProperty("user.home")}/.gradle/gradle.properties")
if (userGradlePropertiesFile.exists()) {
    try {
        FileInputStream(userGradlePropertiesFile).use { fis ->
            userGradleProperties.load(fis)
        }
    } catch (e: Exception) {
        logger.warn("Could not load user gradle.properties file: ${e.message}")
    }
}

// Helper function to get property, preferring user properties, then project properties
fun findMyProperty(propertyName: String): String? {
    return userGradleProperties.getProperty(propertyName) ?: project.findProperty(propertyName)
        ?.toString()
}

// Helper function for mandatory properties
fun findMandatoryProperty(propertyName: String): String = findMyProperty(propertyName)
    ?: throw IllegalArgumentException(
        "Missing mandatory Gradle property: '$propertyName'. Please define it in your user-level gradle.properties (${userGradlePropertiesFile.path}) or project-level gradle.properties."
    )

// --- Fetch Required Values ---
val centralUsernameProp = findMandatoryProperty("centralUsername")
val centralPasswordProp = findMandatoryProperty("centralPassword")
val gpgPassphraseProp = findMandatoryProperty("gpg.passphrase")
val gpgArmoredKeyFilePath = findMandatoryProperty("gpg.armoredKeyFile")
val gpgKeyIdProp =
    findMandatoryProperty("gpg.keyId") // Optional for signing plugin if using key content directly
val gitTokenProp = findMandatoryProperty("git.token")

// --- Configure Publishing (POM Details, etc.) ---
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.tanish-ranjan"
            artifactId = "cropkit"
            version = project.version.toString()

            afterEvaluate {
                from(components.getByName("release"))
            }

            val sourceJar by tasks.registering(Jar::class) {
                archiveClassifier.set("sources")
                from(android.sourceSets.getByName("main").java.srcDirs)
            }
            artifact(sourceJar)

            val javadocJar by tasks.registering(Jar::class) {
                dependsOn(tasks.dokkaHtml)
                archiveClassifier.set("javadoc")
                from(tasks.dokkaHtml.flatMap { it.outputDirectory })
            }
            artifact(javadocJar)

            pom {
                name.set("Crop Kit")
                description.set("Crop Kit is a Jetpack Compose library that allows you to easily crop images with customizable crop shapes such as Rectangle, Square, and Circle. It provides a simple yet flexible crop composable to seamlessly integrate into your Android applications. Features include image flipping, rotation, and gridlines to enhance the cropping experience.")
                url.set("https://github.com/Tanish-Ranjan/crop-kit")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("Tanish-Ranjan")
                        name.set("Tanish Ranjan")
                        email.set("tanishranjan4@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Tanish-Ranjan/crop-kit.git")
                    developerConnection.set("scm:git:ssh://github.com/Tanish-Ranjan/crop-kit.git")
                    url.set("https://github.com/Tanish-Ranjan/crop-kit")
                }
            }
        }
    }
}

// --- Configure Gradle Signing Plugin ---
signing {
    // Prefer using the key file path
    val gpgKeyContent = file(gpgArmoredKeyFilePath).readText() // Read key from file
    useInMemoryPgpKeys(gpgKeyIdProp, gpgKeyContent, gpgPassphraseProp)

    sign(publishing.publications["release"])

    isRequired = gradle.taskGraph.hasTask("publishReleasePublicationToMavenLocal") ||
            gradle.taskGraph.hasTask("publishReleasePublicationToMavenRepository") ||
            gradle.taskGraph.hasTask("jreleaserFullRelease")
}


// --- Configure JReleaser Gradle Plugin ---
jreleaser {
    // Define properties accessible via {{prop.*}} in jreleaser.yml
    // The keys here ("prop.central.username" etc.) MUST match the {{prop.*}} usage in YAML.
    extraProperties["prop.central.username"] = centralUsernameProp
    extraProperties["prop.central.password"] = centralPasswordProp
    extraProperties["prop.git.token"] = gitTokenProp
}