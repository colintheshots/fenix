/* This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.library.bookmarks

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.mozilla.fenix.mvi.Action
import org.mozilla.fenix.mvi.ActionBusFactory
import org.mozilla.fenix.mvi.Change
import org.mozilla.fenix.mvi.Reducer
import org.mozilla.fenix.mvi.UIComponent
import org.mozilla.fenix.mvi.UIView
import org.mozilla.fenix.mvi.ViewState

class SignInComponent(
    private val container: ViewGroup,
    fragment: Fragment,
    bus: ActionBusFactory,
    override var initialState: SignInState = SignInState()
) : UIComponent<SignInState, SignInAction, SignInChange>(
    fragment,
    bus.getManagedEmitter(SignInAction::class.java),
    bus.getSafeManagedObservable(SignInChange::class.java)
) {

    override val reducer: Reducer<VM<SignInState>, SignInChange> = { vm, change ->
        val state = vm.state.value!!
        when (change) {
            SignInChange.SignedIn -> vm.copyIn(state.copy(signedIn = true))
            SignInChange.SignedOut -> vm.copyIn(state.copy(signedIn = false))
        }
    }

    override fun initView(): UIView<SignInState, SignInAction, SignInChange> =
        SignInUIView(container, actionEmitter, changesObservable)

    init {
        render(reducer, VM<SignInState>()::class)
    }
}

data class SignInState(val signedIn: Boolean = false) : ViewState()

sealed class SignInAction : Action {
    object ClickedSignIn : SignInAction()
}

sealed class SignInChange : Change {
    object SignedIn : SignInChange()
    object SignedOut : SignInChange()
}
