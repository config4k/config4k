package io.github.config4k

import com.google.inject.*
import com.typesafe.config.Config
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object QualifiersTest : Spek({
    val injector = withConfig("""
        |acme.frontend {
        |   port = 8080
        |}
        |com.acme.endpoints.auth {
        |   timeout = 10s
        |}
    """.trimMargin()) {
        Guice.createInjector(object : AbstractModule() {
            @Provides
            @Singleton
            @Root
            fun providesRootConfig(): Config = this@withConfig

            @Provides
            @Singleton
            @Application
            fun providesApplicationConfig(): Config = this@withConfig.getConfig("acme.frontend")
        })
    }

    context("the binding annotations") {
        describe("the root annotation") {
            it("should correctly return the root object") {
                class RootService @Inject constructor (@Root val config: Config)
                with(injector.getInstance(RootService::class.java)) {
                    assert(config.hasPath("acme.frontend"))
                    assert(config.hasPath("com.acme.endpoints.auth"))
                }
            }
            it("should correctly return the Application object") {
                class FrontendApplication @Inject constructor (@Application val config: Config)
                with(injector.getInstance(FrontendApplication::class.java)) {
                    assert(!config.hasPath("acme.frontend"))
                    assert(!config.hasPath("com.acme.endpoints.auth"))
                    assert(config.extract<Int>("port") == 8080)
                }
            }
        }
    }
})