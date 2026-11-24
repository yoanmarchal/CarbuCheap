package com.ym.carbucheap.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp

private const val LICENCE_OUVERTE_URL = "https://www.etalab.gouv.fr/licence-ouverte-open-licence/"
private const val ODBL_URL = "https://opendatacommons.org/licenses/odbl/1-0/"

private data class DataSource(
    val title: String,
    val text: String,
    val links: List<Pair<String, String>> // label → url
)

private val SOURCES = listOf(
    DataSource(
        title = "Prix des carburants",
        text = "Ministère de l'Économie — prix des carburants en France, flux instantané v2.",
        links = listOf(
            "data.economie.gouv.fr" to "https://data.economie.gouv.fr/explore/dataset/prix-des-carburants-en-france-flux-instantane-v2/",
            "Licence Ouverte v2.0" to LICENCE_OUVERTE_URL
        )
    ),
    DataSource(
        title = "Bornes de recharge",
        text = "Fichier consolidé des bornes de recharge pour véhicules électriques (IRVE), via ODRE.",
        links = listOf(
            "odre.opendatasoft.com" to "https://odre.opendatasoft.com/explore/dataset/bornes-irve/",
            "Licence Ouverte v2.0" to LICENCE_OUVERTE_URL
        )
    ),
    DataSource(
        // Attribution wording required by the dataset's licence terms — keep verbatim.
        title = "Noms des stations-service",
        text = "Noms et enseignes des stations : Chiffrex (chiffrex.fr), d'après OpenStreetMap – " +
            "© les contributeurs d'OpenStreetMap, ODbL",
        links = listOf(
            "Jeu de données" to "https://www.data.gouv.fr/datasets/referentiel-des-noms-et-enseignes-de-stations-service-enrichi-par-openstreetmap",
            "Licence ODbL 1.0" to ODBL_URL
        )
    )
)

@Composable
fun SourcesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        },
        title = { Text("Sources et licences") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SOURCES.forEach { source ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = source.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.semantics { heading() }
                        )
                        Text(text = source.text, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = linksText(source.links, MaterialTheme.colorScheme.primary),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    )
}

private fun linksText(links: List<Pair<String, String>>, color: Color): AnnotatedString =
    buildAnnotatedString {
        val style = TextLinkStyles(SpanStyle(color = color, textDecoration = TextDecoration.Underline))
        links.forEachIndexed { index, (label, url) ->
            if (index > 0) append("  ·  ")
            withLink(LinkAnnotation.Url(url, style)) { append(label) }
        }
    }
