package ru.hihit.cobuy.ui.components.composableElems.columnItems.procductItems

import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.ui.components.viewmodels.SettingKeys

@Composable
fun UniversalListItem(
    currentItemType: String,
    product: ProductData,
    onStatusChanged: (ProductData) -> Unit,
    onEdited: (ProductData) -> Unit,
    onImageSelected: (ProductData) -> Unit,
    isDeleted: MutableState<Boolean>,
    placementModifier: Modifier,
    dismissState: SwipeToDismissBoxState
) {

    when (currentItemType) {
        SettingKeys.PRODUCT_CARD_TYPE_STANDARD -> StandardProductItem(
            product = product,
            onStatusChanged = onStatusChanged,
            onEdited = onEdited,
            onImageSelected = onImageSelected,
            isDeleted = isDeleted,
            dismissState = dismissState,
            placementModifier = placementModifier
        )
        SettingKeys.PRODUCT_CARD_TYPE_SHOPPING_LIST -> CompactProductItem(
            product = product,
            onStatusChanged = onStatusChanged,
            onEdited = onEdited,
            onImageSelected = onImageSelected,
            isDeleted = isDeleted,
            dismissState = dismissState,
            placementModifier = placementModifier
        )
        else -> StandardProductItem(
            product = product,
            onStatusChanged = onStatusChanged,
            onEdited = onEdited,
            onImageSelected = onImageSelected,
            isDeleted = isDeleted,
            dismissState = dismissState,
            placementModifier = placementModifier
        )
    }

}