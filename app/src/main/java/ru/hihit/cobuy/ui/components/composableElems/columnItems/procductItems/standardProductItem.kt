package ru.hihit.cobuy.ui.components.composableElems.columnItems.procductItems

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.NewProductModal
import ru.hihit.cobuy.ui.theme.getColorByHash
import ru.hihit.cobuy.utils.getFromPreferences

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun StandardProductItem(
    product: ProductData = ProductData(),
    onStatusChanged: (ProductData) -> Unit = {},
    onImageSelected: (ProductData) -> Unit = {},
    onEdited: (ProductData) -> Unit = {},
    @SuppressLint("ModifierParameter") placementModifier: Modifier,
    isDeleted: MutableState<Boolean>,
    dismissState: SwipeToDismissBoxState
) {
    var isBought by remember { mutableStateOf(product.status == ProductStatus.BOUGHT) }
    var isPlanned by remember { mutableStateOf(product.status == ProductStatus.PLANNED) }

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
            NewProductModal(
                product = product,
                onSubmit = {
                    onEdited(it)
                    openModal.value = false
                },
                onImageSelected = onImageSelected,
                onDismiss = { openModal.value = false }
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
            backgroundContent = {
                val color = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Color.Transparent
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                    else -> Color.Transparent
                }

                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(PaddingValues(bottom = 8.dp)),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),

                    ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }) {
            Card(
                Modifier
                    .height(200.dp)
                    .padding(PaddingValues(bottom = 8.dp)),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp),
            ) {
                Row(
                    Modifier.combinedClickable(
                        onClick = { openModal.value = true },
                        onLongClick = { })
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.1f)
                            .background(getColorByHash(product.name))
                            .fillMaxHeight()
                    ) {
                        Text("")
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .padding(
                                PaddingValues(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier

                        ) {
                            Text(product.name, style = MaterialTheme.typography.titleLarge)
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(0.3F),
                                color = MaterialTheme.colorScheme.surfaceTint
                            )
                            Text(
                                product.description,
                                maxLines = 3,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(0.5F),
                                text = product.price.toString() + "₽", //FIXME Валюту надо выбирать в настройках
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)
                            )

                            val context = LocalContext.current
                            if (product.buyer == null || context.getFromPreferences("user_id", -1) == product.buyer.id) {
                                Column {
                                    val buyButtonColor = if (isBought) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
                                    val planButtonColor = if (isPlanned) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val newStatus = if (isBought) ProductStatus.NONE else ProductStatus.BOUGHT
                                            updateStatus(newStatus)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = buyButtonColor),
                                        border = if (isBought) null else BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceTint),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(12.dp)
                                    ) {
                                        Text(if (isBought) stringResource(id = R.string.bought_word) else stringResource(id = R.string.buy_word))
                                    }

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val newStatus = if (isPlanned) ProductStatus.NONE else ProductStatus.PLANNED
                                            updateStatus(newStatus)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = planButtonColor),
                                        border = if (isPlanned) null else BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceTint),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(12.dp)
                                    ) {
                                        Text(if (isPlanned) stringResource(id = R.string.planned_word) else stringResource(id = R.string.plan_word))
                                    }
                                }
                            } else {
                                val statusWord = if (product.status == ProductStatus.BOUGHT) stringResource(id = R.string.bought_by_word) else stringResource(id = R.string.planned_by_word)
                                Text(
                                    text = statusWord + ' ' + product.buyer.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}