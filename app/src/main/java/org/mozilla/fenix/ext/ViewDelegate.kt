/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.ext

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.reflect.KProperty

class ViewDelegate<T : View?>(private val view: T?, lifecycleOwner: LifecycleOwner) :
    LifecycleObserver {

    private var value: T? = null
    private var owner: LifecycleOwner? = lifecycleOwner

    init {
        owner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        value = view
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        value = null
    }

    operator fun setValue(thisRef: T, property: KProperty<*>, t: T) {
        value = t
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value ?: throw UninitializedPropertyAccessException(
            "View has been collected or was null: ${property.name}"
        )
    }
}
