package com.example.uitvolunteermap.core.common

fun List<String>.removeAtOrKeep(index: Int): List<String> {
    return if (index in indices) filterIndexed { i, _ -> i != index } else this
}
