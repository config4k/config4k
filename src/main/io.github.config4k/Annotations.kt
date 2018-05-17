package io.github.config4k

import javax.inject.Qualifier


/**
 * Applies to binding situations where there will be multiple Config objects bound. This qualifier denotes the
 * [com.typesafe.config.Config] for the application.
 *
 * @param namespace an optional namespace, the path to the application configuration. Note: currently only used for
 * informational purposes.
 */
@Qualifier
annotation class Application(val namespace: String = "")

/**
 * Applies to binding situations where there will be multiple Config objects bound. This qualifier is the root
 * [com.typesafe.config.Config]. In a modular application the modules would base their config paths off of this root.
 *
 * This could generally be bound directly to the result of [com.typesafe.config.ConfigFactory.defaultApplication].
 * However another path could be used if "module" config is located elsewhere -- e.g., under `plugins`.
 */
@Qualifier
annotation class Root