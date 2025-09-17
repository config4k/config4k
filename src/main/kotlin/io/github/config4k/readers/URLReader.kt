package io.github.config4k.readers

import java.net.URI
import java.net.URL

internal class URLReader : Reader<URL>({ config, path -> URI(config.getString(path)).toURL() })
