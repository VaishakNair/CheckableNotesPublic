package `in`.v89bhp.checkablenotes.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.data.CheckableItem

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
                onCheckedChange = { newValue -> onCheckedChange(checkableItem, newValue) }
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
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = checkableItem.isChecked,
                onCheckedChange = onCheckedChange
            )
            Spacer(Modifier.width(10.dp))
            Text(
                checkableItem.message,
                style = MaterialTheme.typography.body1
            )

        }
    }
}


//@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
//@Composable
//fun ItemCardPreview() {
//    ItemCard(
//        modifier = Modifier.padding(8.dp),
//        checkableItem = CheckableItem(3, "Test", true),
//        onCheckedChange = {})
//
//}




