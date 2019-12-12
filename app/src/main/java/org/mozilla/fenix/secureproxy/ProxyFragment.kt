/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.secureproxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_proxy.*
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.components

class ProxyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_proxy, container, false)
    }

    override fun onResume() {
        super.onResume()

        val secureProxyFeature = context!!.components.services.secureProxy

        secureProxySwitch.isChecked = secureProxyFeature.config.enabled
        secureProxySwitch.setOnCheckedChangeListener { _, isChecked ->
            secureProxyFeature.config.enabled = isChecked
        }
    }
}
