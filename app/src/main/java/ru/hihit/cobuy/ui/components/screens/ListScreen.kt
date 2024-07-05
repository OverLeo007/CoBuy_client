package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.theme.getColorByHash

@Composable
fun ListScreen(
    navHostController: NavHostController,
    vm: ListViewModel
) {
    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)


    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000L)
            isRefreshing = false
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                Toast
                                    .makeText(context, "Open group edit", Toast.LENGTH_SHORT)
                                    .show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "List ${vm.listId}")
                        }
                    }
                },
                navHostController = navHostController,
            )

            SwipeRefreshImpl(
                swipeState = swipeRefreshState,
                onRefresh = {
                    Toast.makeText(context, "Обновляем товары", Toast.LENGTH_SHORT).show()
                    isRefreshing = true
                }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
                ) {
                    items(10) {
                        var isPlanned by remember { mutableStateOf(false) }
                        var isBought by remember { mutableStateOf(false) }
                        GoodItem(
                            id = it,
                            title = "Good $it",
                            description = "Lorem Ipsum $it - это текст-\"рыба\", часто используемый в печати и вэб-дизайне. Lorem Ipsum является стандартной \"рыбой\" для текстов на латинице с начала XVI века. В то время некий безымянный печатник создал большую коллекцию размеров и форм шрифтов, используя Lorem Ipsum для распечатки образцов. Lorem Ipsum не только успешно пережил без заметных изменений пять веков, но и перешагнул в электронный дизайн. Его популяризации в новое время послужили публикация листов Letraset с образцами Lorem Ipsum в 60-х годах и, в более недавнее время, программы электронной вёрстки типа Aldus PageMaker, в шаблонах которых используется Lorem Ipsum.\",",
                            onBuy = {
                                isBought = !isBought
                                isPlanned = false
                            },
                            onPlan = {
                                isBought = false
                                isPlanned = !isPlanned
                            },
                            onDelete = {
                                Toast.makeText(
                                    context,
                                    "Good $it deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            isBought = isBought,
                            isPlanned = isPlanned

                        )
                    }

                }
            }
        }
        AddButton(
            onClick = { Toast.makeText(context, "Good added", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}


@Composable
@Preview
fun GoodItem(
    id: Int = 0,
    title: String = "title",
    description: String = "Lorem Ipsum - это текст-\"рыба\", часто используемый в печати и вэб-дизайне. Lorem Ipsum является стандартной \"рыбой\" для текстов на латинице с начала XVI века. В то время некий безымянный печатник создал большую коллекцию размеров и форм шрифтов, используя Lorem Ipsum для распечатки образцов. Lorem Ipsum не только успешно пережил без заметных изменений пять веков, но и перешагнул в электронный дизайн. Его популяризации в новое время послужили публикация листов Letraset с образцами Lorem Ipsum в 60-х годах и, в более недавнее время, программы электронной вёрстки типа Aldus PageMaker, в шаблонах которых используется Lorem Ipsum.",
    isBought: Boolean = false,
    isPlanned: Boolean = true,
    onBuy: (isBought: Boolean) -> Unit = {},
    onPlan: (isPlanned: Boolean) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        Modifier
            .height(200.dp)
            .padding(PaddingValues(bottom = 8.dp)),
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row {
            Box(
                modifier = Modifier
                    .weight(0.1f)
                    .background(getColorByHash(title))
                    .fillMaxHeight()
            ) {
                Text("")
            }
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .padding(PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp))
            ) {
                Column(
                    modifier = Modifier

                ) {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.3F),
                        color = MaterialTheme.colorScheme.surfaceTint
                    )
                    Text(
                        description,
                        maxLines = 3,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.End
                ) {
                    val buyButtonColor = if (isBought) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
                    val planButtonColor = if (isPlanned) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background

                    Button(
                        modifier = Modifier.padding(PaddingValues(end = 8.dp)),
                        onClick = { onPlan(isPlanned) },
                        colors = ButtonDefaults.buttonColors(containerColor = planButtonColor),
                        border = if (isPlanned) null else BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceTint),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 1.dp, bottom = 1.dp)
                    ) {
                        Text(if (isPlanned) "Planned" else "Plan")
                    }
                    Button(
                        onClick = { onBuy(isBought) },
                        colors = ButtonDefaults.buttonColors(containerColor = buyButtonColor),
                        border = if (isBought) null else BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceTint),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 1.dp, bottom = 1.dp)
                    ) {
                        Text(if (isBought) "Bought" else "Buy")
                    }
                }
            }
        }
    }

}