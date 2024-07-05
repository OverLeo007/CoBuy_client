package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import ru.hihit.cobuy.R
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel
import kotlin.random.Random

@Composable
fun GroupsScreen(
    navHostController: NavHostController,
    vm: GroupsViewModel
) {
    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(Toast.LENGTH_SHORT.toLong())
            isRefreshing = false
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Groups")
                    }
                },
                navHostController = navHostController,
                isBackArrow = false
            )

            SwipeRefreshImpl(
                swipeState = swipeRefreshState,
                onRefresh = {
                    Toast.makeText(context, "Обновляем группы", Toast.LENGTH_SHORT).show()
                    isRefreshing = true
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
//                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(5) {
                        GroupItem(
                            imageUrl = "https://sun125-1.userapi.com/s/v1/ig2/AIxZdnOPgs7aVJZn24luWz84Fg1aa2iyzU6GbG-qp1065HTamsIBsBnINypL_PRcXVNEKZP6yZc_9oWq5UciHnW-.jpg?size=50x0&quality=96&crop=0,0,984,984&ava=1",
                            groupName = "Прогеры №$it",
                            peopleCount = Random.nextInt(1, 10),
                            listCount = Random.nextInt(1, 10),
                            onClick = {
                                Toast.makeText(context, "Open group $it", Toast.LENGTH_SHORT).show()
                                navHostController.navigate(Route.Group + "/${it}")
                            }
                        )

                    }
                }
            }
        }

        AddButton(
            onClick = { Toast.makeText(context, "Group added", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}


@Composable
fun GroupItem(
    imageUrl: String,
    groupName: String,
    peopleCount: Int,
    listCount: Int,
    onClick: () -> Unit = {}
) {
    Box(
        Modifier
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Group $groupName Avatar",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.height(48.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = groupName, overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painterResource(id = R.drawable.group_24px),
                        contentDescription = "People Icon",
                        modifier = Modifier
                            .sizeIn(maxHeight = MaterialTheme.typography.bodySmall.fontSize.value.dp)
                            .padding(PaddingValues(end = 4.dp)),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = "$peopleCount",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painterResource(id = R.drawable.list_alt_24px),
                        contentDescription = "List Icon",
                        modifier = Modifier
                            .sizeIn(maxHeight = MaterialTheme.typography.bodySmall.fontSize.value.dp)
                            .padding(PaddingValues(end = 4.dp)),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = "$listCount",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onTertiary)
                    )
                }
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
}