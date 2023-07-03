package `in`.v89bhp.checkablenotes.ui.note

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import `in`.v89bhp.checkablenotes.R
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.nameischeckedequals
import `in`.v89bhp.checkablenotes.ui.dialogs.ConfirmationDialog
import `in`.v89bhp.checkablenotes.ui.theme.green
import `in`.v89bhp.checkablenotes.ui.theme.light_green
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Note(
    fileName: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    noteViewModel: NoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    val context = LocalContext.current

    LaunchedEffect(true) {// Load note (if any) during composition. Ignored during recompositions
        noteViewModel.loadNote(fileName)
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    onBackPressed(fileName, noteViewModel) {}
                }

                else -> {// Do nothing for other events.

                }
            }

        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(id = R.string.app_name))
            },
            actions = {
                IconButton(onClick = { noteViewModel.openDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_note)
                    )
                }
                val context = LocalContext.current
                IconButton(onClick = { noteViewModel.showSharesheet(context) }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_note)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackPressed(
                        fileName,
                        noteViewModel,
                        navigateBack
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
    }) { contentPadding ->


        val titles = listOf("Note", "Checkable List")
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()

        Column(modifier = modifier.padding(contentPadding)) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {

                            BadgedBox(
                                badge = {
                                    if (index == 1 && noteViewModel.pendingItemsCount > 0) {
                                        Badge {

                                            Text(
                                                noteViewModel.pendingItemsCount.toString(),
                                                modifier = Modifier.semantics {
                                                    contentDescription =
                                                        "${noteViewModel.pendingItemsCount} pending items"
                                                }
                                            )
                                        }
                                    }
                                }) {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }


                        }
                    )
                }
            }

            HorizontalPager(
                pageCount = titles.size,
                state = pagerState
            ) { page ->
                if (page == 0) { // Tab 1

                    val scrollState = rememberScrollState()
                    val imeState = rememberImeState()
                    val textStyle = LocalTextStyle.current

                    LaunchedEffect(imeState.value) {
                        if (imeState.value) {
                            val newlineCount = getNewlineCount(noteViewModel.text)
                            if (newlineCount > 2) {
                                scrollState.scrollTo(
                                    newlineCount *
                                            getFontSizeInPixels(
                                                textStyle.fontSize.value,
                                                context
                                            ).toInt()
                                )
                                Log.i("Note.kt", "Scroll state value: ${scrollState.value}")
                            } else {
                                scrollState.scrollTo(0)
                            }
                        }
                    }
                    TextField(
                        value = noteViewModel.text,
                        onValueChange = {
                            noteViewModel.text = it
                            Log.i("Note.kt", "Cursor position ${it.selection.start}")
                            noteViewModel.updateList(it)
                        },
                        label = { Text("Enter Items Line by Line:") },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                } else { // Tab 2
                    CheckableList(checkableItems = noteViewModel.list,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        onCheckedChange = { checkableItem, newValue ->
                            noteViewModel.onCheckedChange(checkableItem, newValue)
                        })
                }
            }



            BackHandler(true) {
                onBackPressed(
                    fileName,
                    noteViewModel,

                    navigateBack
                )
            }
        }
        if (noteViewModel.openDeleteDialog) {
            ConfirmationDialog(title = R.string.delete_note,
                text = R.string.delete_this_note,
                onConfirmation = { confirmed ->
                    if (confirmed) {
                        noteViewModel.deleteNotes(listOf(fileName))
                        navigateBack()
                    }
                    noteViewModel.openDeleteDialog = false
                })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckableList(
    checkableItems: List<CheckableItem>,
    onCheckedChange: (CheckableItem, newValue: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(checkableItems,
            key = { it.id }) { checkableItem ->
            ItemCard(
                checkableItem = checkableItem,
                onCheckedChange = { newValue -> onCheckedChange(checkableItem, newValue) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
fun ItemCard(
    checkableItem: CheckableItem,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier

) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (checkableItem.isChecked) light_green
            else MaterialTheme.colorScheme.surfaceVariant
        )

    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = checkableItem.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = green)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                checkableItem.name,
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}

fun onBackPressed(
    fileName: String,
    noteViewModel: NoteViewModel,
    navigateBack: () -> Unit
) {
    if (noteViewModel.text.text.trim() == "") {// Note is empty. Delete the existing note (if any)
        noteViewModel.deleteNotes(listOf(fileName))
    } else {
        noteViewModel.loadedNote?.let { loadedNote -> // Not a new note:
            if (loadedNote.text.text != noteViewModel.text.text || !(loadedNote.list nameischeckedequals noteViewModel.list)) {// Note has been updated (either text or checkable list):
                noteViewModel.saveNote(
                    fileName,
                    noteViewModel.text,
                    noteViewModel.list
                )
            }
        } ?: noteViewModel.saveNote( // New note:
            fileName,
            noteViewModel.text,
            noteViewModel.list
        ) // New note. Save it.

    }
    navigateBack()
}

@Composable
fun rememberImeState(): State<Boolean> {
    val imeState = remember {
        mutableStateOf(false)
    }

    val view = LocalView.current
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            imeState.value = isKeyboardOpen
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return imeState
}

fun getNewlineCount(tfv: TextFieldValue): Int {
    // 'tvf.selection.start' contains the index in the input string where the cursor is currently present. So
    // Count newlines upto that position:
    val newlineCount = tfv.text.slice(0 until tfv.selection.start).count { it == '\n' }
    Log.i("Note.kt", "Newline count: $newlineCount")
    return newlineCount
}

fun getFontSizeInPixels(fontSizeSP: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        fontSizeSP,
        context.resources.displayMetrics
    );
}

