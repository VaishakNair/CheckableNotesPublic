package `in`.v89bhp.checkablenotes.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheckableList(
    checkableItems: List<CheckableItem>,
    onCheckedChange: (CheckableItem, newValue: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(checkableItems,
            key = { it.id }) { checkableItem ->
            ItemCard(checkableItem, { newValue -> onCheckedChange(checkableItem, newValue) })
//            Divider()
        }

    }
}

@Composable
fun ItemCard(
    content: CheckableItem,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        Modifier
            .height(100.dp)
            .fillMaxSize(),
        elevation = 10.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = content.isChecked,
                onCheckedChange = onCheckedChange
            )
            Spacer(Modifier.width(10.dp))
            Text(
                content.message,
                style = MaterialTheme.typography.body1
            )

        }
    }
}

class CheckableItem(var id: Int, val message: String, isChecked: Boolean = false) {
    var isChecked by mutableStateOf(isChecked)

    override fun equals(other: Any?): Boolean {
        return other?.let {
            if (it is CheckableItem) {
                it.message == message
            } else {
                false
            }
        } ?: false
    }

    override fun toString(): String {
        return "Id: $id Message: $message"
    }
}

fun main() {
    val set1 = setOf(CheckableItem(1, "b"), CheckableItem(0, "a") )
//    val set2 = setOf(CheckableItem(0, "a"), CheckableItem(3, "c"))
    val set2 = emptySet<CheckableItem>()
    println("First element of set1: ${set1.first()}")
    println("First element of set2: ${set2.first()}")
    println(CheckableItem(1, "a") == set2.first())
    println("Set membership test for 'a' ${CheckableItem(1, "a") in set1}")
    val difference = set2.filter { !(set1.contains(it)) }
    println("Difference: $difference")
    println("Equality of first elements of two sets: ${set1.first() == set2.first()}")

    val differenceSet = mutableSetOf<CheckableItem>()
    set1.forEach { set2Item ->
        var found = false
        set2.forEach { set1Item ->
            if (set2Item == set1Item) {
                found = true
            }
        }
        if (!found) differenceSet.add(set2Item)
    }
    println("Set difference custom: $differenceSet")

}

fun setDifference(set1: Set<CheckableItem>, set2: Set<CheckableItem>): Set<CheckableItem> {
    val differenceSet = mutableSetOf<CheckableItem>()
    set1.forEach { set2Item ->
        var found = false
        set2.forEach { set1Item ->
            if (set2Item == set1Item) {
                found = true
            }
        }
        if (!found) differenceSet.add(set2Item)
    }
    return differenceSet
}