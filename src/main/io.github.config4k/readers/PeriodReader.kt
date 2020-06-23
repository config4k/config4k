package io.github.config4k.readers

import com.typesafe.config.Config
import java.time.Period

internal class PeriodReader : Reader<Period>(Config::getPeriod)
