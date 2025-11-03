import groovy.json.JsonOutput
import org.gradle.api.artifacts.ProjectDependency

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.cocoapods) apply false
    alias(libs.plugins.mockkery) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

tasks.register("exportModuleDeps") {
    doLast {
        val allModuleNames = rootProject.subprojects.map { it.name }.toSet()
        val directDependencies = mutableMapOf<String, MutableSet<String>>()

        rootProject.subprojects.forEach { project ->
            val projectDeps = mutableSetOf<String>()
            project.configurations
                .matching { it.name.contains("implementation", ignoreCase = true) }
                .forEach { configuration ->
                    configuration.dependencies.forEach { dependency ->
                        if (dependency is ProjectDependency && allModuleNames.contains(dependency.dependencyProject.name)) {
                            projectDeps.add(dependency.dependencyProject.name)
                        }
                    }
                }
            directDependencies[project.name] = projectDeps
        }

        val reverseDependencies = mutableMapOf<String, MutableSet<String>>()
        directDependencies.keys.forEach { module -> reverseDependencies[module] = mutableSetOf() }
        directDependencies.forEach { (module, deps) ->
            deps.forEach { dep -> reverseDependencies[dep]?.add(module) }
        }

        fun collectAllDependents(
            module: String,
            visited: MutableSet<String> = mutableSetOf()
        ): Set<String> {
            if (!visited.add(module)) return emptySet()
            val directDependents = reverseDependencies[module] ?: emptySet()
            return directDependents + directDependents.flatMap { collectAllDependents(it, visited) }
        }

        val modulesWithDependents =
            directDependencies.keys.associateWith { collectAllDependents(it) }

        println(JsonOutput.toJson(modulesWithDependents))
    }
}