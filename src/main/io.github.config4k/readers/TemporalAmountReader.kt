package io.github.config4k.readers

import com.typesafe.config.Config
import java.time.temporal.TemporalAmount

internal class TemporalAmountReader : Reader<TemporalAmount>(Config::getTemporal)
