package ru.hihit.cobuy.ui.components.composableElems.columnItems.procductItems

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.models.ProductData
import ru.hihit.cobuy.currency.CurrencyViewModel
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.ProductModal
import ru.hihit.cobuy.ui.theme.getColorByHash
import ru.hihit.cobuy.utils.getFromPreferences


@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CompactProductItem(
    product: ProductData = ProductData(),
    onStatusChanged: (ProductData) -> Unit = {},
    onImageSelected: (ProductData) -> Unit = {},
    onEdited: (ProductData) -> Unit = {},
    @SuppressLint("ModifierParameter") placementModifier: Modifier,
    isDeleted: MutableState<Boolean>,
    dismissState: SwipeToDismissBoxState,
    currencyVm: CurrencyViewModel
) {
    var isBought by remember { mutableStateOf(product.status == ProductStatus.BOUGHT) }
    var isPlanned by remember { mutableStateOf(product.status == ProductStatus.PLANNED) }

    val context = LocalContext.current

    fun updateStatus(newStatus: ProductStatus) {
        product.status = newStatus
        isBought = newStatus == ProductStatus.BOUGHT
        isPlanned = newStatus == ProductStatus.PLANNED
        onStatusChanged(product)
    }

    LaunchedEffect(product.status) {
        isBought = product.status == ProductStatus.BOUGHT
        isPlanned = product.status == ProductStatus.PLANNED
    }

    val openModal = remember { mutableStateOf(false) }

    when {
        openModal.value ->
            ProductModal(
                product = product,
                onSubmit = {
                    onEdited(it)
                    openModal.value = false
                },
                onImageSelected = onImageSelected,
                onDismiss = { openModal.value = false },
                currencyVm = currencyVm
            )
    }

    AnimatedVisibility(
        modifier = placementModifier,
        visible = !isDeleted.value,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 150)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 150)
        )
    ) {

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            backgroundContent = { DismissCompactProductItemBackground(dismissState) }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .combinedClickable(onClick = { openModal.value = true })
                        .background(MaterialTheme.colorScheme.surface),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.03f)
                            .background(getColorByHash(product.name))
                            .fillMaxHeight()
                    ) {
                        Text("")
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.97f)
                            .fillMaxHeight()
                            .padding(
                                PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(
                                        1f,
                                        fill = false
                                    )  // Занимает доступное место, но не вытесняет кнопку
                                    .padding(end = 8.dp),  // Отступ от кнопки
                            ) {
                                Text(
                                    product.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    product.description,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                            IconButton(
                                onClick = {
                                    val newStatus =
                                        if (isBought) ProductStatus.NONE else ProductStatus.BOUGHT
                                    updateStatus(newStatus)
                                },
                                enabled = product.buyer == null || context.getFromPreferences(
                                    "user_id",
                                    -1
                                ) == product.buyer.id,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Crossfade(
                                    targetState = isBought || isPlanned,
                                    label = "Buy button anim"
                                ) { targetState ->
                                    Icon(
                                        painterResource(
                                            if (targetState) R.drawable.cart_added else R.drawable.cart_standard
                                        ),
                                        contentDescription = "Buy button",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                    }

                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
            }
        }
    }

}


@Composable
fun DismissCompactProductItemBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier)
        Icon(
            Icons.Default.Delete,
            contentDescription = "delete",
            tint = Color.White
        )
    }
}