package org.mozilla.fenix.search.awesomebar
/* This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mozilla.components.browser.search.SearchEngine
import org.mozilla.fenix.mvi.Action
import org.mozilla.fenix.mvi.ActionBusFactory
import org.mozilla.fenix.mvi.Change
import org.mozilla.fenix.mvi.Reducer
import org.mozilla.fenix.mvi.UIComponent
import org.mozilla.fenix.mvi.ViewState

data class AwesomeBarState(
    val query: String = "",
    val showShortcutEnginePicker: Boolean = false,
    val suggestionEngine: SearchEngine? = null
) : ViewState()

sealed class AwesomeBarAction : Action {
    data class URLTapped(val url: String) : AwesomeBarAction()
    data class SearchTermsTapped(val searchTerms: String, val engine: SearchEngine? = null) : AwesomeBarAction()
    data class SearchShortcutEngineSelected(val engine: SearchEngine) : AwesomeBarAction()
}

sealed class AwesomeBarChange : Change {
    data class SearchShortcutEngineSelected(val engine: SearchEngine) : AwesomeBarChange()
    data class SearchShortcutEnginePicker(val show: Boolean) : AwesomeBarChange()
    data class UpdateQuery(val query: String) : AwesomeBarChange()
}

class AwesomeBarComponent(
    private val container: ViewGroup,
    fragment: Fragment,
    bus: ActionBusFactory,
    override var initialState: AwesomeBarState = AwesomeBarState()
) : UIComponent<AwesomeBarState, AwesomeBarAction, AwesomeBarChange>(
    fragment,
    bus.getManagedEmitter(AwesomeBarAction::class.java),
    bus.getSafeManagedObservable(AwesomeBarChange::class.java)
) {
    override val reducer: Reducer<VM<AwesomeBarState>, AwesomeBarChange> = { vm, change ->
        val state = vm.state.value!!
        when (change) {
            is AwesomeBarChange.SearchShortcutEngineSelected ->
                vm.copyIn(state.copy(suggestionEngine = change.engine, showShortcutEnginePicker = false))
            is AwesomeBarChange.SearchShortcutEnginePicker ->
                vm.copyIn(state.copy(showShortcutEnginePicker = change.show))
            is AwesomeBarChange.UpdateQuery -> vm.copyIn(state.copy(query = change.query))
        }
    }

    override fun initView() = AwesomeBarUIView(container, actionEmitter, changesObservable)

    init {
        render(reducer, VM<AwesomeBarState>()::class)
    }
}
