/* This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.mvi

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.reflect.KClass

abstract class UIComponent<S : ViewState, A : Action, C : Change>(
    val owner: Fragment,
    protected val actionEmitter: Observer<A>,
    protected val changesObservable: Observable<C>
) {

    abstract var initialState: S
    abstract val reducer: Reducer<VM<S>, C>

    open val uiView: UIView<S, A, C> by lazy { initView() }

    abstract fun initView(): UIView<S, A, C>
    open fun getContainerId() = uiView.containerId
    /**
     * Render the ViewState to the View through the Reducer
     */
    fun render(reducer: Reducer<VM<S>, C>, clazz: KClass<out VM<S>>): Disposable =
        internalRender(reducer, clazz)
            .subscribe(uiView.updateView())

    fun internalRender(reducer: Reducer<VM<S>, C>, clazz: KClass<out VM<S>>): Observable<S> {
        val model = ViewModelProviders.of(owner).get(clazz.java)
        return changesObservable
            .scan(model.copyIfNotNull(initialState), reducer)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .map { it.state.value!! }
            .observeOn(AndroidSchedulers.mainThread())
    }

    class VM<S> : ViewModel() {
        val state = MutableLiveData<S>()

        fun copyIn(copy: S): VM<S> {
            state.value = copy
            return this
        }

        fun copyIfNotNull(copy: S): VM<S> {
            if (state.value == null) {
                state.value = copy
            }
            return this
        }
    }
}
