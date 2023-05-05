package `in`.v89bhp.checkablenotes

import `in`.v89bhp.checkablenotes.data.CheckableItem

//fun main() {
//    val set1 = setOf(CheckableItem(1, "b"), CheckableItem(0, "a") )
////    val set2 = setOf(CheckableItem(0, "a"), CheckableItem(3, "c"))
//    val set2 = emptySet<CheckableItem>()
//    println("First element of set1: ${set1.first()}")
//    println("First element of set2: ${set2.first()}")
//    println(CheckableItem(1, "a") == set2.first())
//    println("Set membership test for 'a' ${CheckableItem(1, "a") in set1}")
//    val difference = set2.filter { !(set1.contains(it)) }
//    println("Difference: $difference")
//    println("Equality of first elements of two sets: ${set1.first() == set2.first()}")
//
//    val differenceSet = mutableSetOf<CheckableItem>()
//    set1.forEach { set2Item ->
//        var found = false
//        set2.forEach { set1Item ->
//            if (set2Item == set1Item) {
//                found = true
//            }
//        }
//        if (!found) differenceSet.add(set2Item)
//    }
//    println("Set difference custom: $differenceSet")
//
//}


/**
 * Kotlin set difference implementation is not returning expected results.
 * So implemented a custom version.
 */
fun setDifference(set1: Set<CheckableItem>, set2: Set<CheckableItem>): Set<CheckableItem> {
    val differenceSet = mutableSetOf<CheckableItem>()
    set1.forEach { set1Item ->
        var found = false
        set2.forEach { set2Item ->
            if (set1Item == set2Item) {
                found = true
            }
        }
        if (!found) differenceSet.add(set1Item)
    }
    return differenceSet
}