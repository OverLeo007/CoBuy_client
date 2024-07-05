package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import ru.hihit.cobuy.R
import ru.hihit.cobuy.models.Group
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GroupScreen(
    navHostController: NavHostController,
    vm: GroupViewModel
) {
    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    val openModal = remember {
        mutableStateOf(false)
    }

    when {
        openModal.value -> EditModal(
            onDismissRequest = { openModal.value = false },
            group = Group.default() // TODO: pass group from vm
        )
    }

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
                                openModal.value = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(vm.groupIconUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Group ${vm.groupId} Avatar",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = "Group ${vm.groupId}")
                        }
                    }
                },
                navHostController = navHostController,
            )

            SwipeRefreshImpl(
                swipeState = swipeRefreshState,
                onRefresh = {
                    Toast.makeText(context, "Обновляем списки", Toast.LENGTH_SHORT).show()
                    isRefreshing = true
                }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ListItem(listName = "Выполненный список покупок", fillPercents = 1F)
                    }
                    items(56) {
                        ListItem(
                            listName = "Список покупок $it",
                            fillPercents = Random.nextFloat(),
                            onClick = {
                                Toast.makeText(context, "Open list $it", Toast.LENGTH_SHORT).show()
                                navHostController.navigate(Route.List + "/${it}")
                            }
                        )
                    }
                }
            }
        }
        AddButton(
            onClick = { Toast.makeText(context, "List added", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}

@Composable
fun ListItem(
    listName: String,
    fillPercents: Float,
    onClick: () -> Unit = {}
) {
    Box(
        Modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = listName,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (fillPercents == 1F) 0.5f else 1F)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { fillPercents },
                    modifier = Modifier.fillMaxWidth(0.8F),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (fillPercents == 1F) 0.5f else 1F),
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "${(fillPercents * 100).roundToInt()}%",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (fillPercents == 1F) 0.5f else 1F)
                )
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun EditModal(
    onDismissRequest: () -> Unit = {},
    group: Group = Group.default()
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8F),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box() {
                Column {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Редактирование",
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    onDismissRequest()
                                },
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.arrow_back_ios_24px),
                                    contentDescription = "Back",
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    onDismissRequest()
                                },
                            ) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.Transparent
                                )
                            }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            )

                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(group.avaUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Group ${group.name} Avatar",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                text = group.name,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Icon(
                                painterResource(id = R.drawable.edit_square_24px),
                                contentDescription = "Modify name",
                                modifier = Modifier
                                    .sizeIn(maxHeight = MaterialTheme.typography.titleLarge.fontSize.value.dp),
                                tint = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                        Spacer(modifier = Modifier.size(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(PaddingValues(start = 12.dp, end = 8.dp))
                        ) {
                            Text(
                                text = "Участники",
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                            IconButton(onClick = { /*TODO click on add user icon*/ }) {
                                Icon(
                                    painterResource(id = R.drawable.person_add_24px),
                                    contentDescription = "Modify name",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                        LazyColumn {
                            items(56) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(
                                            PaddingValues(
                                                start = 24.dp,
                                                end = 8.dp
                                            )
                                        )
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Text(
                                        text = "Райан гослинг",
//                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = { /*TODO*/ }
                                        ) {
                                            Icon(
                                                painterResource(id = R.drawable.star_unfilled_24px),
                                                contentDescription = "is_admin",
                                                tint = MaterialTheme.colorScheme.onTertiary
                                            )
                                        }
                                        IconButton(
                                            onClick = { /*TODO*/ }
                                        ) {
                                            Icon(
                                                painterResource(id = R.drawable.person_remove_24px),
                                                contentDescription = "delete",
                                                tint = MaterialTheme.colorScheme.onTertiary
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                            }

                        }

                    }
                }
            }
        }
    }

}