/* This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.fenix.library.history

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.mozilla.fenix.mvi.Action
import org.mozilla.fenix.mvi.ActionBusFactory
import org.mozilla.fenix.mvi.Change
import org.mozilla.fenix.mvi.Reducer
import org.mozilla.fenix.mvi.UIComponent
import org.mozilla.fenix.mvi.ViewState
import org.mozilla.fenix.test.Mockable

data class HistoryItem(val id: Int, val title: String, val url: String, val visitedAt: Long)

@Mockable
class HistoryComponent(
    private val container: ViewGroup,
    fragment: Fragment,
    bus: ActionBusFactory,
    override var initialState: HistoryState = HistoryState()
) :
    UIComponent<HistoryState, HistoryAction, HistoryChange>(
        fragment,
        bus.getManagedEmitter(HistoryAction::class.java),
        bus.getSafeManagedObservable(HistoryChange::class.java)
    ) {

    override val reducer: Reducer<VM<HistoryState>, HistoryChange> = { vm, change ->
        val state = vm.state.value!!
        when (change) {
            is HistoryChange.Change -> vm.copyIn(state.copy(mode = HistoryState.Mode.Normal, items = change.list))
            is HistoryChange.EnterEditMode -> vm.copyIn(state.copy(mode = HistoryState.Mode.Editing(listOf(change.item))))
            is HistoryChange.AddItemForRemoval -> {
                val mode = state.mode
                if (mode is HistoryState.Mode.Editing) {
                    val items = mode.selectedItems + listOf(change.item)
                    vm.copyIn(state.copy(mode = mode.copy(selectedItems = items)))
                } else {
                    vm.copyIn(state)
                }
            }
            is HistoryChange.RemoveItemForRemoval -> {
                val mode = state.mode
                if (mode is HistoryState.Mode.Editing) {
                    val items = mode.selectedItems.filter { it.id != change.item.id }
                    vm.copyIn(state.copy(mode = mode.copy(selectedItems = items)))
                } else {
                    vm.copyIn(state)
                }
            }
            is HistoryChange.ExitEditMode -> vm.copyIn(state.copy(mode = HistoryState.Mode.Normal))
        }
    }

    override fun initView() = HistoryUIView(container, actionEmitter, changesObservable)

    init {
        render(reducer, VM<HistoryState>()::class)
    }
}

data class HistoryState(
    val items: List<HistoryItem> = emptyList(),
    val mode: Mode = Mode.Normal
) : ViewState() {
    sealed class Mode {
        object Normal : Mode()
        data class Editing(val selectedItems: List<HistoryItem>) : Mode()
    }
}

sealed class HistoryAction : Action {
    data class Select(val item: HistoryItem) : HistoryAction()
    data class EnterEditMode(val item: HistoryItem) : HistoryAction()
    object BackPressed : HistoryAction()
    data class AddItemForRemoval(val item: HistoryItem) : HistoryAction()
    data class RemoveItemForRemoval(val item: HistoryItem) : HistoryAction()

    sealed class Delete : HistoryAction() {
        object All : Delete()
        data class One(val item: HistoryItem) : Delete()
        data class Some(val items: List<HistoryItem>) : Delete()
    }
}

sealed class HistoryChange : Change {
    data class Change(val list: List<HistoryItem>) : HistoryChange()
    data class EnterEditMode(val item: HistoryItem) : HistoryChange()
    object ExitEditMode : HistoryChange()
    data class AddItemForRemoval(val item: HistoryItem) : HistoryChange()
    data class RemoveItemForRemoval(val item: HistoryItem) : HistoryChange()
}
