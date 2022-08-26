package io.github.config4k.readers

import java.net.URL

internal class URLReader : Reader<URL>({ config, path -> URL(config.getString(path)) })
