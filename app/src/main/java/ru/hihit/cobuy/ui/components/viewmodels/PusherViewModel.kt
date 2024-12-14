package ru.hihit.cobuy.ui.components.viewmodels

import androidx.lifecycle.ViewModel
import ru.hihit.cobuy.App

abstract class PusherViewModel : ViewModel() {

    protected val className: String = this.javaClass.simpleName
    protected val pusherService = App.getPusherService()

}