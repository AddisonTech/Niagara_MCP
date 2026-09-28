/*
 * Standalone build settings for niagaramcp.
 *
 * Tridium's Gradle plugins are resolved from the Niagara install being built
 * against ($niagara_home/etc/m2/repository). Set niagara_home per machine, e.g.
 * in the user-home gradle.properties or with -Pniagara_home=... (see BUILDING.md).
 * Nothing machine-specific belongs in this file.
 */

import com.tridium.gradle.plugins.settings.LocalSettingsExtension
import com.tridium.gradle.plugins.settings.MultiProjectExtension

pluginManagement {
  val niagaraHome: Provider<String> = providers.gradleProperty("niagara_home").orElse(
    providers.systemProperty("niagara_home").orElse(
      providers.environmentVariable("NIAGARA_HOME").orElse(
        providers.environmentVariable("niagara_home")
      )
    )
  )

  val gradlePluginHome: String = providers.gradleProperty("gradlePluginHome").orElse(
    providers.environmentVariable("GRADLE_PLUGIN_HOME").orElse(
      niagaraHome.map { "$it/etc/m2/repository" }
    )
  ).orNull ?: throw InvalidUserDataException(
    "niagara_home is not set. Add niagara_home=<path to Niagara install> to " +
      "~/.gradle/gradle.properties, pass -Pniagara_home=..., or set NIAGARA_HOME. See BUILDING.md."
  )

  val gradlePluginRepoUrl = "file:///${gradlePluginHome.replace('\\', '/')}"

  // Plugin versions ship with each Niagara release; defaults in gradle.properties.
  val gradlePluginVersion: String = providers.gradleProperty("niagaraGradlePluginVersion").get()
  val settingsPluginVersion: String = providers.gradleProperty("niagaraSettingsPluginVersion").get()

  repositories {
    maven(url = gradlePluginRepoUrl)
    gradlePluginPortal()
  }

  plugins {
    id("com.tridium.settings.multi-project") version (settingsPluginVersion)
    id("com.tridium.settings.local-settings-convention") version (settingsPluginVersion)

    id("com.tridium.niagara") version (gradlePluginVersion)
    id("com.tridium.vendor") version (gradlePluginVersion)
    id("com.tridium.niagara-module") version (gradlePluginVersion)
    id("com.tridium.niagara-signing") version (gradlePluginVersion)
    id("com.tridium.bajadoc") version (gradlePluginVersion)
    id("com.tridium.niagara-jacoco") version (gradlePluginVersion)
    id("com.tridium.niagara-annotation-processors") version (gradlePluginVersion)

    id("com.tridium.convention.niagara-home-repositories") version (gradlePluginVersion)
  }
}

plugins {
  // Discovers <name>/<name>.gradle.kts subprojects (niagaramcp-rt).
  id("com.tridium.settings.multi-project")

  // Applies local/my-settings.gradle(.kts) when present (local/ is gitignored).
  id("com.tridium.settings.local-settings-convention")
}

configure<LocalSettingsExtension> {
  loadLocalSettings()
}

configure<MultiProjectExtension> {
  findProjects()
}

rootProject.name = "niagaramcp"
