package com.example.pushuptrainer_mobile.core.uikit.text

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Формы русского слова при числе: 1 день, 2 дня, 5 дней.
 *
 * Склонение выбирается по правилам русского языка, а не по языку системы,
 * поэтому тексты правильные и на устройствах с другим языком интерфейса.
 */
enum class RussianPluralForm {
    /** 1, 21, 101 — «день». */
    One,

    /** 2–4, 22–24 — «дня». */
    Few,

    /** 0, 5–20, 25–30 — «дней». */
    Many,
    ;

    companion object {
        fun of(count: Int): RussianPluralForm {
            val mod10 = count % 10
            val mod100 = count % 100
            return when {
                mod10 == 1 && mod100 != 11 -> One
                mod10 in 2..4 && mod100 !in 12..14 -> Few
                else -> Many
            }
        }
    }
}

/**
 * Строка в нужной форме для числа [count]. Число подставляется в строку как `%1$d`.
 */
@Composable
fun russianPluralString(
    count: Int,
    one: StringResource,
    few: StringResource,
    many: StringResource,
): String {
    val resource = when (RussianPluralForm.of(count)) {
        RussianPluralForm.One -> one
        RussianPluralForm.Few -> few
        RussianPluralForm.Many -> many
    }
    return stringResource(resource, count)
}
