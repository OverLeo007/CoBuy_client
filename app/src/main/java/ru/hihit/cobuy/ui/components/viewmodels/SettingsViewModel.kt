package ru.hihit.cobuy.ui.components.viewmodels

import android.icu.util.Currency
import androidx.lifecycle.ViewModel
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.models.UserData
import ru.hihit.cobuy.utils.clearPreferences
import ru.hihit.cobuy.utils.getUserDataFromPreferences

class SettingsViewModel() : ViewModel() {
    val user: UserData = App.getContext().getUserDataFromPreferences()


    fun onLogout() {
        App.getContext().clearPreferences()
    }

}


object SettingKeys {
    const val NOTIFICATION_LEVEL = "notification_level"
    const val NOTIFICATION_LEVEL_DESC = "notification_level_description"
    const val NOTIFICATION_LEVEL_NONE = "notification_level_none"
    const val NOTIFICATION_LEVEL_IMPORTANT = "notification_level_important"
    const val NOTIFICATION_LEVEL_ALL = "notification_level_all"

    const val SHOW_COMPLETED_LISTS = "show_completed_lists"

    const val PRODUCT_CARD_TYPE = "product_card_type"
    const val PRODUCT_CARD_TYPE_DESC = "product_card_type_description"
    const val PRODUCT_CARD_TYPE_STANDARD = "product_card_type_standard"
    const val PRODUCT_CARD_TYPE_SHOPPING_LIST = "product_card_type_shopping_list"
    const val PRODUCT_CARD_TYPE_PICTURE = "product_card_type_picture"

    const val THEME = "theme"
    const val THEME_DESC = "theme_description"
    const val THEME_LIGHT = "theme_light"
    const val THEME_DARK = "theme_dark"
    const val THEME_SYSTEM = "theme_system"

    const val CURRENCY = "currency"
    const val CURRENCY_DESC = "currency_description"
//    const val currency: Currency

}