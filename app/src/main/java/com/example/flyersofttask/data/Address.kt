package com.example.flyersofttask.data

import androidx.room.*
import com.google.gson.Gson
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType

private fun getRuby(str: String): String {
    val format = HanyuPinyinOutputFormat().apply {
        toneType = HanyuPinyinToneType.WITHOUT_TONE
        vCharType = HanyuPinyinVCharType.WITH_V
    }
    val ruby = str.map {
        val pinyins = PinyinHelper.toHanyuPinyinStringArray(it, format)
        if (pinyins == null || pinyins.isEmpty()) {
            it.lowercase()
        } else if (pinyins[0] == "none") {
            ""
        } else {
            pinyins[0]
        }
    }.joinToString("")
    if (ruby == "") {
        return "#$str"
    } else if (!(ruby.first() in 'a'..'z')) {
        return "#$ruby"
    } else {
        return ruby
    }
}

data class CustomItem(
    val tag: String,
    val value: String
)

class Converters {
    @TypeConverter
    fun listToJson(value: List<CustomItem>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<CustomItem>::class.java).toList()
}

@Entity(indices = [Index("ruby")])
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val ruby: String = getRuby(name),
    val customs: List<CustomItem> = listOf()
)