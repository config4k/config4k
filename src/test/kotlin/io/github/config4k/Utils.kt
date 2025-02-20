package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueFactory

fun String.toConfig(): Config = ConfigFactory.parseString(this.trimIndent())

fun Map<String, Any>.toConfigValue(): ConfigValue = ConfigValueFactory.fromMap(this)

private val RenderOps =
    ConfigRenderOptions
        .defaults()
        .setFormatted(false)
        .setJson(false)
        .setComments(false)
        .setOriginComments(false)

fun Config.render(): String = this.root().render(RenderOps)
