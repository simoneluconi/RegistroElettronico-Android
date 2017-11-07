package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.DataSnapshot
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Metodi
import java.util.*

class SuperAgenda(val agenda: RemoteAgenda, var completed: Boolean = false, var test: Boolean)

@Entity(tableName = "LOCAL_AGENDA")
class LocalAgenda(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") var id: Long = 0L,
        @ColumnInfo(name = "TITLE") var title: String = "",
        @ColumnInfo(name = "CONTENT") var content: String = "",
        @ColumnInfo(name = "TYPE") var type: String = "",
        @ColumnInfo(name = "DAY") var day: Long = 0,
        @ColumnInfo(name = "SUBJECT") var subject: Long = 0L,
        @ColumnInfo(name = "TEACHER") var teacher: Long = 0L,
        @ColumnInfo(name = "COMPLETED_DATE") var completed_date: Date? = Date(0),
        @ColumnInfo(name = "PROFILE") var profile: Long = 0L,
        @ColumnInfo(name = "ARCHIVED") var archived: Boolean = false
) {

    constructor(snap: DataSnapshot) : this(
            snap.child("id").getValue(Long::class.java) ?: 0,
            snap.child("title").getValue(String::class.java).orEmpty(),
            snap.child("content").getValue(String::class.java).orEmpty(),
            snap.child("type").getValue(String::class.java).orEmpty(),
            snap.child("day").getValue(Long::class.java) ?: 0,
            snap.child("subject").getValue(Long::class.java) ?: 0,
            snap.child("teacher").getValue(Long::class.java) ?: 0,
            Date(snap.child("completed_date").getValue(Long::class.java) ?: 0),
            snap.child("profile").getValue(Long::class.java) ?: 0,
            snap.child("archived").getValue(Boolean::class.java) == true
    )

    fun asMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map.put("id", id)
        map.put("title", title)
        map.put("content", content)
        map.put("type", type)
        map.put("day", day)
        map.put("subject", subject)
        map.put("teacher", teacher)
        map.put("completed_date", completed_date?.time ?: 0)
        map.put("profile", profile)
        map.put("archived", archived)
        return map
    }
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
@Entity(tableName = "REMOTE_AGENDA")
class RemoteAgenda(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("evtId") var id: Long = 0L,
        @ColumnInfo(name = "START") @Expose @SerializedName("evtDatetimeBegin") var start: Date = Date(0),
        @ColumnInfo(name = "END") @Expose @SerializedName("evtDatetimeEnd") var end: Date = Date(0),
        @ColumnInfo(name = "IS_FULL_DAY") @Expose @SerializedName("isFullDay") var isFullDay: Boolean = false,
        @ColumnInfo(name = "NOTES") @Expose @SerializedName("notes") var notes: String = "",
        @ColumnInfo(name = "AUTHOR") @Expose @SerializedName("authorName") var author: String = "",
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L
) {
    fun isTest(info: RemoteAgendaInfo?): Boolean {
        return info?.test ?: Metodi.isEventTest(this)
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

    fun getInfo(): RemoteAgendaInfo? {
        return DatabaseHelper.database.eventsDao().getInfo(id)
    }
}

class AgendaAPI(@Expose @SerializedName("agenda") val agenda: List<RemoteAgenda>) {
    fun getAgenda(profile: Profile): List<RemoteAgenda> {
        val id = profile.id
        agenda.forEach { it.profile = id }
        return agenda
    }
}

@Entity(tableName = "REMOTE_AGENDA_INFO")
class RemoteAgendaInfo(
        @ColumnInfo(name = "ID") @PrimaryKey var id: Long = 0L,
        @ColumnInfo(name = "COMPLETED") var completed: Boolean = false,
        @ColumnInfo(name = "ARCHIVED") var archived: Boolean = false,
        @ColumnInfo(name = "TEST") var test: Boolean = false
) {
    fun asMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map.put("id", id)
        map.put("completed", id)
        map.put("archived", id)
        map.put("test", id)
        return map
    }

    constructor(snap: DataSnapshot) : this(
            snap.child("id").getValue(Long::class.java) ?: 0,
            snap.child("completed").getValue(Boolean::class.java) == true,
            snap.child("archived").getValue(Boolean::class.java) == true,
            snap.child("test").getValue(Boolean::class.java) == true
    )
}