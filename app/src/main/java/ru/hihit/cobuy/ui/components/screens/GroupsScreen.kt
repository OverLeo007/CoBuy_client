package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import ru.hihit.cobuy.R
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    navHostController: NavHostController,
    vm: GroupsViewModel = GroupsViewModel()
) {
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
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
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Groups")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navHostController.navigate(Route.Settings)
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
            HorizontalDivider()

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    Toast.makeText(context, "Обновляем группы", Toast.LENGTH_SHORT).show()
                    isRefreshing = true
                },
                indicator = { state, trigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = trigger,
                        scale = true
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(56) {
                        GroupItem(
                            imageUrl = "https://sun125-1.userapi.com/s/v1/ig2/AIxZdnOPgs7aVJZn24luWz84Fg1aa2iyzU6GbG-qp1065HTamsIBsBnINypL_PRcXVNEKZP6yZc_9oWq5UciHnW-.jpg?size=50x0&quality=96&crop=0,0,984,984&ava=1",
                            groupName = "Прогеры",
                            peopleCount = 69,
                            listCount = 1488
                        )

                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { Toast.makeText(context, "Group added", Toast.LENGTH_SHORT).show() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }

    }
}


@Composable
fun GroupItem(
    imageUrl: String,
    groupName: String,
    peopleCount: Int,
    listCount: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
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
    HorizontalDivider()
}