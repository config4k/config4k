package io.github.config4k.readers

import com.typesafe.config.Config
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

internal class JavaDurationReader : Reader<JavaDuration>(Config::getDuration)

internal class DurationReader : Reader<Duration>({ config, name -> config.getDuration(name).toKotlinDuration() })
