package `in`.v89bhp.checkablenotes.ui.note

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import `in`.v89bhp.checkablenotes.R
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.ui.dialogs.ConfirmationDialog
import `in`.v89bhp.checkablenotes.ui.home.ItemsCount
import `in`.v89bhp.checkablenotes.ui.theme.black
import `in`.v89bhp.checkablenotes.ui.theme.blue
import `in`.v89bhp.checkablenotes.ui.theme.dark_grey
import `in`.v89bhp.checkablenotes.ui.theme.light_blue
import `in`.v89bhp.checkablenotes.ui.theme.light_grey
import `in`.v89bhp.checkablenotes.ui.theme.light_grey_2
import `in`.v89bhp.checkablenotes.ui.theme.white
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun Note(
    fileName: String,
    navigateBack: () -> Unit,
    showOnePane: Boolean,
    modifier: Modifier = Modifier,
    noteViewModel: NoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    val context = LocalContext.current

    LaunchedEffect(true) {// Load note (if any) during composition. Ignored during recompositions
        noteViewModel.loadNote(context, fileName)
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    onBackPressed(context, fileName, noteViewModel) {}
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

                if (!showOnePane) {
                    ItemsCount(
                        modifier = Modifier.size(width = 70.dp, height = 22.dp),
                        completedItemsCount = noteViewModel.completedItemsCount,
                        pendingItemsCount = noteViewModel.pendingItemsCount,
                        backgroundColor = light_grey
                    )
                }

                IconButton(onClick = { noteViewModel.openDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_note),
                        tint = white
                    )
                }
                IconButton(onClick = { noteViewModel.showSharesheet(context) }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_note),
                        tint = white
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackPressed(
                        context,
                        fileName,
                        noteViewModel,
                        navigateBack
                    )
                }) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = white)
                            .padding(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = black
                        )
                    }
                }
            })
    }) { contentPadding ->

        if (showOnePane) {
            OnePane(
                noteViewModel = noteViewModel,
                fileName = fileName,
                navigateBack = navigateBack,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            TwoPane(
                noteViewModel = noteViewModel,
                fileName = fileName,
                navigateBack = navigateBack,
                modifier = Modifier.padding(contentPadding)
            )
        }

        if (noteViewModel.openDeleteDialog) {
            ConfirmationDialog(title = R.string.delete_note,
                text = R.string.delete_this_note,
                onConfirmation = { confirmed ->
                    if (confirmed) {
                        noteViewModel.deleteNotes(context, listOf(fileName))
                        navigateBack()
                    }
                    noteViewModel.openDeleteDialog = false
                })
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun OnePane(
    noteViewModel: NoteViewModel,
    fileName: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val titles = listOf("Note", "Checkable List")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val keyboardController = LocalSoftwareKeyboardController.current
    if (pagerState.currentPage == 1) {
        keyboardController?.hide()
    }
    Column(
        modifier = modifier
//            .padding(contentPadding)
            .background(Brush.verticalGradient(listOf(black, light_grey_2)))
    ) {

        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            titles.forEachIndexed { index, title ->
                FilledTonalButton(
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = white,
                        containerColor = if (pagerState.currentPage == index) blue else dark_grey
                    )
                ) {
                    Text(text = title)
                }
            }

            ItemsCount(
                completedItemsCount = noteViewModel.completedItemsCount,
                pendingItemsCount = noteViewModel.pendingItemsCount,
                backgroundColor = light_grey
            )


        }

        HorizontalPager(
            pageCount = titles.size,
            state = pagerState
        ) { page ->
            if (page == 0) { // Note tab
                NoteTab(noteViewModel = noteViewModel)


            } else { // Checkable list tab
                CheckableListTab(noteViewModel = noteViewModel)
            }
        }


        BackHandler(true) {
            if (pagerState.currentPage != 0) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            } else {
                onBackPressed(
                    context,
                    fileName,
                    noteViewModel,
                    navigateBack
                )
            }
        }
    }
}

@Composable
fun TwoPane(
    noteViewModel: NoteViewModel,
    fileName: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(modifier = modifier.fillMaxSize()) {
        NoteTab(
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            noteViewModel = noteViewModel
        )

        CheckableListTab(
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 8.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
            noteViewModel = noteViewModel
        )
    }

    BackHandler(true) {
        onBackPressed(
            context,
            fileName,
            noteViewModel,
            navigateBack
        )

    }

}

@Composable
fun NoteTab(
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier.padding(16.dp)
) {
    Column(
        modifier = modifier
            .background(color = light_grey, shape = RoundedCornerShape(16.dp))
    ) {

        TitleTextField(noteViewModel = noteViewModel)
        NoteTextField(noteViewModel = noteViewModel)
    }
}

@Composable
fun CheckableListTab(
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier.padding(16.dp)
) {
    Box(
        modifier = modifier
            .background(color = light_grey, shape = RoundedCornerShape(16.dp))
    ) {
        CheckableList(checkableItems = noteViewModel.list,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            onCheckedChange = { checkableItem, newValue ->
                noteViewModel.onCheckedChange(checkableItem, newValue)
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTextField(noteViewModel: NoteViewModel, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val textStyle = LocalTextStyle.current
    val context = LocalContext.current
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(imeState.value) {
        if (imeState.value && isFocused) { // Keyboard is opened and note text field has focus:
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
    OutlinedTextField(
        value = noteViewModel.text,
        onValueChange = {
            noteViewModel.text = it
            Log.i("Note.kt", "Cursor position ${it.selection.start}")
            noteViewModel.updateList(it)
        },
        placeholder = { Text(text = stringResource(R.string.enter_items_line_by_line)) },
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .onFocusChanged { isFocused = it.isFocused },
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = white,
            unfocusedTextColor = black,
            focusedTextColor = black
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTextField(noteViewModel: NoteViewModel, modifier: Modifier = Modifier) {
    OutlinedTextField(
        maxLines = 1,
        singleLine = true,
        value = noteViewModel.title,
        onValueChange = {
            noteViewModel.title = it

        },
        placeholder = { Text(text = stringResource(R.string.title)) },
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = white,
            focusedTextColor = black,
            unfocusedTextColor = black
        )
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckableList(
    checkableItems: List<CheckableItem>,
    onCheckedChange: (CheckableItem, newValue: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (checkableItems.isEmpty()) {
        Text(
            text = stringResource(R.string.no_items),
            modifier = modifier
        )
    } else {
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
            containerColor = if (checkableItem.isChecked) light_blue
            else MaterialTheme.colorScheme.surfaceVariant
        )

    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            Checkbox(
                checked = checkableItem.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = black, checkmarkColor = light_blue)
            )

            Text(
                checkableItem.name,
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}

fun onBackPressed(
    context: Context,
    fileName: String,
    noteViewModel: NoteViewModel,
    navigateBack: () -> Unit
) {
    noteViewModel.handleBackPress(context = context, fileName = fileName)
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

