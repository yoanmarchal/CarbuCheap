package com.ym.carbucheap.data.model

enum class FuelType(
    val displayName: String,
    val apiName: String,
    val prixField: String,
    val majField: String
) {
    GAZOLE("Gazole", "Gazole", "gazole_prix", "gazole_maj"),
    SP95("SP95", "SP95", "sp95_prix", "sp95_maj"),
    SP98("SP98", "SP98", "sp98_prix", "sp98_maj"),
    E85("E85", "E85", "e85_prix", "e85_maj"),
    E10("E10", "E10", "e10_prix", "e10_maj"),
    GPLC("GPLc", "GPLc", "gplc_prix", "gplc_maj");
}

