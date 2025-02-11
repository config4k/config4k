package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions

fun String.toConfig(): Config = ConfigFactory.parseString(this.trimIndent())

private val RenderOps =
    ConfigRenderOptions
        .defaults()
        .setFormatted(false)
        .setJson(false)
        .setComments(false)
        .setOriginComments(false)

fun Config.render(): String = this.root().render(RenderOps)
