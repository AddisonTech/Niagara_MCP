/*
 * Standalone root build for niagaramcp. Replaces the parent Niagara dev
 * workspace the upstream project was built from.
 */

plugins {
  id("com.tridium.niagara")
  id("com.tridium.vendor")
  id("com.tridium.niagara-signing")
  id("com.tridium.convention.niagara-home-repositories")
}

vendor {
  defaultVendor("niagaramcp")
  defaultModuleVersion(providers.gradleProperty("moduleVersion").get())
}

subprojects {
  repositories {
    mavenCentral()
  }
}
