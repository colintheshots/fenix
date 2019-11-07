/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home

import android.view.View
import androidx.annotation.StringRes
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings

/**
 * Sets up the secure proxy toggle button on the [HomeFragment].
 */
class SecureProxyButtonView(
    button: View,
    onClick: () -> Unit
) {

    init {
        button.contentDescription =
            button.context.getString(getContentDescription(button.context.components.services.secureProxy.config.enabled))
        button.setOnClickListener {
            onClick.invoke()
        }
    }

    companion object {

        /**
         * Returns the appropriate content description depending on the secure proxy mode.
         */
        @StringRes
        private fun getContentDescription(mode: Boolean) = when (mode) {
            true -> R.string.content_description_secure_proxy_button
            false -> R.string.content_description_disable_secure_proxy_button
        }
    }
}
