package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.Utils.Metodi
import java.util.*

data class SuperAgenda(val agenda: RemoteAgenda, var completed: Boolean, var test: Boolean)

data class LocalAgenda(
        var title: String,
        var content: String,
        var type: String,
        var day: Date,
        var subject: Subject,
        var teacher: Teacher,
        var completed_date: Date?,
        var profile: Long,
        var archived: Boolean
) : SugarRecord() {
    constructor() : this("", "", "", Date(), Subject(), Teacher(), null, -1L, false)
}

/*
{
    "agenda": [
        {
            "evtId": 4217,
            "evtCode": "AGNT",
            "evtDatetimeBegin": "2017-09-20T00:00:00+02:00",
            "evtDatetimeEnd": "2017-09-20T23:59:59+02:00",
            "isFullDay": true,
            "notes": "Ai genitori, agli studenti e agli insegnanti delle classi 3^Dsa e 4^Fsa.\n\nMercoledì 20 settembre gli studenti delle due classi inizieranno il percorso di\nalternanza scuola/lavoro presso le Gallerie di Palazzo Leoni Montanari di\nVicenza.\nSi prevede un impegno di 40/50 di ore, suddivise tra formazione  e\nattività  di stage.\nLa formazione sarà curata dai responsabili dell’area educazione del museo\n(Agata Keran) insieme ai docenti di Ca’ Foscari (Giuseppe Barbieri e\nSilvia Burini, curatori dell’esposizione) e sarà realizzata entro il 15\nottobre, con una serie di incontri pomeridiani alle Gallerie d’Italia -\nPalazzo Leoni Montanari .\nLo stage Studenti/ciceroni alle Gallerie d’Italia - Palazzo Leoni\nMontanari, sarà effettuato di domenica, nel periodo tra il 20 ottobre 2017\ne il 15 aprile 2018, nel corso del quale 4 studenti al mattino\n(10.00-14.00) e 4 studenti al pomeriggio (14.00-18.00) svolgeranno le\nattività di illustrazione della mostra e dei contenuti multimediali ai\nvisitatori.\n\nProgrammazione incontri\n\nDalle icone all'arte contemporanea.\nA cura di Agata Keran, area educazione delle Gallerie e Alessia Cavallaro,\nUniversità Ca' Foscari (durata complessiva di 6 ore)\n      Linguaggio espressivo dell'icona, dall'arte bizantina alle icone russe\ndella collezione Intesa Sanpaolo: 2 ore (a cura di Agata Keran)\nmercoledì 20 settembre, ore 14.30/16.30\n      Tempo e storia nell'arte dell'icona: la rappresentazione di menologio\n(calendario liturgico dei santi): 2 ore (a cura di Agata Keran)\nvenerdì 22 settembre, ore 14.30/16.30\n      Icona, simbolo, astrazione: Kandinskij, Malevic, Chagall: 2 ore (a cura di\nAlessia Cavallaro)\nvenerdì 29 settembre, ore 14.30/16.30.\n\nI mediatori culturali: cenni sulla didattica dell'arte e sull'esperienza\nperformativa e divulgativa.\nA cura di Giuseppe Barbieri\nmercoledì 27 settembre (2 ore), ore 14.30/16.30\n\nI contenuti della mostra.\nA cura di Silvia Burini e Giuseppe Barbieri, curatori della mostra, Università Ca'\nFoscari, con interventi dell'artista, dell'architetto allestitore e dei responsabili\ndel display multimediale (durata complessiva di 8 ore)\ngiovedì 5 ottobre (2 ore), ore 14.30/16.30\nvenerdì 13 ottobre (2 ore), ore 14.30/16.30\nlunedì 16 ottobre (2 ore), ore 14.30/16.30\nmartedì 17 ottobre (2 ore), (nel pomeriggio in orario da confermare\nsulla base della disponibilità dell'artista ad incontrare i ragazzi.\nSerata inaugurale della mostra)\n\n\nProgetto di alternanza scuola-lavoro per il Liceo Quadri Vicenza\nDal 20 ottobre 2017 al 15 aprile 2018, le Gallerie d’Italia – Palazzo\nLeoni Montanari ospiteranno la mostra temporanea Grisha Bruskin – Icone\nsovietiche, un progetto espositivo che ricorda in modo originale il\ncentenario della Rivoluzione d’ottobre, rileggendo la narrazione\nmetaforica di una monumentale opera d’arte, il dittico Fundamental’nyj\nLeksikon di Bruskin, da tempo riconosciuto come il più grande e originale\ndegli artisti russi viventi.\nOltre alla memoria dell’importante ricorrenza, la mostra trova la sua\nprofonda ragione nell’accostamento con la collezione di antiche icone\nrusse, custodita alle Gallerie. Il loro linguaggio simbolico si pone come\nuna delle fonti d’ispirazione alla base della visionarietà onirica\ndell’artista contemporaneo.\nIn occasione dell’evento espositivo si desidera attivare un percorso\ndedicato alle scuole secondarie di secondo grado, nell’ambito del progetto\ndi alternanza scuola-lavoro, per offrire ai ragazzi un’esperienza\nintensiva di formazione e operatività museale, connessa al piano\ndivulgativo della mostra.\nLa proposta si articola in tre momenti:\n•        la prima parte di carattere teorico è dedicata alla conoscenza del\npatrimonio artistico coinvolto nel progetto espositivo (16 ore);\n•        la seconda parte di carattere attivo/interattivo prevede una formazione\n“sul campo”, funzionale al racconto delle opere in esposizione (4 ore);\n•        la terza parte prevede lo stage degli studenti mediatori culturali (20\nore).",
            "authorName": "CARLOTTI PAOLA",
            "classDesc": "4FSA",
            "subjectDesc": null
        }
    ]
}
 */
@Table
data class RemoteAgenda(
        @Expose @SerializedName("evtId") @Unique val id: Long,
        @Expose @SerializedName("evtDatetimeBegin") val start: Date,
        @Expose @SerializedName("evtDatetimeEnd") val end: Date,
        @Expose @SerializedName("isFullDay") val isFullDay: Boolean,
        @Expose @SerializedName("notes") val notes: String,
        @Expose @SerializedName("authorName") val author: String,
        var profile: Long
) {

    constructor() : this(0, Date(), Date(), false, "", "", -1L)

    fun getInfo(): RemoteAgendaInfo? {
        return SugarRecord.findById(RemoteAgendaInfo::class.java, id)
    }

    fun isTest(): Boolean {
        val info = getInfo()
        return if (info != null) {
            info.test
        } else {
            Metodi.isEventTest(this)
        }
    }

    companion object {
        fun getSuperAgenda(id: Long): List<SuperAgenda> {
            val completed: MutableList<RemoteAgendaInfo> = SugarRecord.find(RemoteAgendaInfo::class.java, "ARCHIVED=0 AND COMPLETED=1") ?: mutableListOf()
            val archived: MutableList<RemoteAgendaInfo> = SugarRecord.find(RemoteAgendaInfo::class.java, "ARCHIVED=1") ?: mutableListOf()
            val events = SugarRecord.find(RemoteAgenda::class.java, "PROFILE=? ", id.toString())

            return events.filter { a -> !archived.any { it.id == a.id } }.map { agenda -> SuperAgenda(agenda, completed.any { it.id == agenda.id }, agenda.isTest()) }
        }

        fun getAgenda(id: Long, date: Date): List<SuperAgenda> {
            val completed: MutableList<RemoteAgendaInfo> = SugarRecord.find(RemoteAgendaInfo::class.java, "ARCHIVED=0 AND COMPLETED=1") ?: mutableListOf()
            val archived: MutableList<RemoteAgendaInfo> = SugarRecord.find(RemoteAgendaInfo::class.java, "ARCHIVED=1") ?: mutableListOf()
            val events = SugarRecord.find(RemoteAgenda::class.java, "PROFILE=? AND START BETWEEN ? AND ? AND END BETWEEN ? AND ?", id.toString(), date.time.toString(), (date.time + 24 * 3600 * 1000).toString(), date.time.toString(), (date.time + 24 * 3600 * 1000).toString())

            return events.filter { a -> !archived.any { it.id == a.id } }.map { agenda ->
                SuperAgenda(
                        agenda,
                        completed.any { it.id == agenda.id },
                        agenda.isTest())
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is RemoteAgenda) return false
        return notes == other.notes && author == other.author
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + isFullDay.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + profile.hashCode()
        return result
    }
}

data class AgendaAPI(@Expose @SerializedName("agenda") val agenda: List<RemoteAgenda>) {
    fun getAgenda(profile: Profile): List<RemoteAgenda> {
        val id = profile.id
        agenda.forEach { it.profile = id }
        return agenda
    }
}

@Table
data class RemoteAgendaInfo(
        @Unique val id: Long,
        var completed: Boolean,
        var archived: Boolean,
        var test: Boolean
) {
    constructor() : this(0, false, false, false)
}