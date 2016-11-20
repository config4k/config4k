package io.github.config4k.readers

import com.typesafe.config.Config
import java.time.Duration


internal class DurationReader : Reader<Duration>(Config::getDuration)