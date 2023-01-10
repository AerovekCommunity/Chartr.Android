package org.aerovek.chartr.data.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.deserialize(jsonArray: String) =
    this.fromJson<T>(jsonArray, object : TypeToken<T>() {}.type)!!