package com.sharpdroid.registroelettronico.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.util.TypedValue
import android.widget.Toast
import com.github.sundeepk.compactcalendarview.domain.Event
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.api.spaggiari.v2.Spaggiari
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.database.pojos.LocalAgendaPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*
import java.io.File as JavaFile

object Metodi {
    var month_year = SimpleDateFormat("MMMM yyyy", Locale.ITALIAN)
    var complex = SimpleDateFormat("EEEE d MMMM yyyy", Locale.ITALIAN)
    private val mainLooper = Looper.getMainLooper()
    private val handler = Handler(mainLooper)

    fun getMessaggioVoto(Obb: Float, media: Float, nVoti: Int): String {
        // Calcolo
        if (Obb > 10 || media > 10)
            return "Errore" // Quando l'obiettivo o la media sono > 10
        if (Obb >= 10 && media < Obb)
            return "Obiettivo irraggiungibile" // Quando l'obiettivo è 10 (o più) e la media è < 10 (non si potrà mai raggiungere)
        val array = doubleArrayOf(0.75, 0.5, 0.25, 0.0)
        var index = 0
        var sommaVotiDaPrendere: Float
        val votiMinimi = DoubleArray(5)
        var diff: Double
        var diff2: Double
        var resto = 0.0
        var parteIntera: Double
        var parteDecimale: Double
        try {
            do {
                index += 1
                sommaVotiDaPrendere = Obb * (nVoti + index) - media * nVoti
            } while (sommaVotiDaPrendere / index > 10)
            var i = 0
            while (i < index) {
                votiMinimi[i] = sommaVotiDaPrendere / index + resto
                resto = 0.0
                parteIntera = Math.floor(votiMinimi[i])
                parteDecimale = (votiMinimi[i] - parteIntera) * 100
                if (parteDecimale != 25.0 && parteDecimale != 50.0 && parteDecimale != 75.0) {
                    var k = 0
                    do {
                        diff = votiMinimi[i] - (parteIntera + array[k])
                        k++
                    } while (diff < 0)
                    votiMinimi[i] = votiMinimi[i] - diff
                    resto = diff
                }
                if (votiMinimi[i] > 10) {
                    diff2 = votiMinimi[i] - 10
                    votiMinimi[i] = 10.0
                    resto += diff2
                }
                i += 1
            }
            // Stampa
            var toReturn: StringBuilder
            if (votiMinimi[0] <= 0)
                return "Puoi stare tranquillo" // Quando i voti da prendere sono negativi
            if (votiMinimi[0] <= Obb)
                toReturn = StringBuilder("Non prendere meno di " + votiMinimi[0])
            else {
                toReturn = StringBuilder("Devi prendere almeno ")
                votiMinimi.filter { it != 0.0 }
                        .forEach { toReturn.append(it).append(", ") }
                toReturn = StringBuilder(toReturn.substring(0, toReturn.length - 2))
            }
            return toReturn.toString()
        } catch (e: Exception) {
            return "Obiettivo irraggiungibile"
        }

    }

    fun capitalizeEach(input: String, doSingleLetters: Boolean = false): String {
        val reg = if (doSingleLetters) {
            Regex("([a-z])\\w*")
        } else {
            Regex("([a-z])\\w+")
        }
        return reg.replace(input.toLowerCase()) { matchResult -> matchResult.value.substring(0, 1).toUpperCase() + matchResult.value.substring(1).toLowerCase() }
    }

    fun dp(dp: Number) = (dp.toFloat() * Resources.getSystem().displayMetrics.density).toInt()

    fun sp(sp: Number) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), Resources.getSystem().displayMetrics).toInt()

    fun calculateScholasticCredits(year: Int, average: Float): Int {
        when (year) {
            3, 4 -> if (average == 6f)
                return 3
            else if (average > 6 && average <= 7)
                return 4
            else if (average > 7 && average <= 8)
                return 5
            else if (average > 8 && average <= 9)
                return 6
            else if (average > 9 && average <= 10) return 7

            5 -> if (average == 6f)
                return 4
            else if (average > 6 && average <= 7)
                return 5
            else if (average > 7 && average <= 8)
                return 6
            else if (average > 8 && average <= 9)
                return 7
            else if (average > 9 && average <= 10) return 8

            else -> return 0
        }

        return 0
    }

    fun getMediaColor(media: Float?, voto_obiettivo: Float) = getMarkColor(media!!, voto_obiettivo)

    fun getMarkColor(voto: Float, voto_obiettivo: Float): Int {
        return if (voto == 0f)
            R.color.intro_blue
        else if (voto >= voto_obiettivo)
            R.color.greenmaterial
        else if (voto < 5)
            R.color.redmaterial
        else if (voto >= 5 && voto < 6)
            R.color.orangematerial
        else
            R.color.lightgreenmaterial
    }

    fun getPossibleSubjectTarget(media: Double): Int {
        return if (media < 6)
            6
        else {
            Math.ceil(media).toInt()
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody?, file: JavaFile): Boolean {
        if (body == null) return false
        try {
            body.byteStream().use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val fileReader = ByteArray(4096)
                    while (true) {
                        val read = inputStream.read(fileReader)
                        if (read == -1)
                            break
                        outputStream.write(fileReader, 0, read)
                    }
                    outputStream.flush()
                    return true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    private fun getFileNameFromHeaders(headers: Headers): String {
        var contentd = headers.get("Content-Disposition") ?: return ""
        contentd = contentd.replace("attachment; filename=", "")
        contentd = contentd.replace("\"".toRegex(), "")
        contentd = contentd.trim { it <= ' ' }
        return contentd
    }

    fun sortMarksByDate(marks: List<Grade>): List<Grade> {
        Collections.sort(marks) { (_, _, o1), (_, _, o2) -> o1.compareTo(o2) }
        return marks
    }

    fun isEventTest(event: SuperAgenda) = isEventTest(event.agenda)

    fun isEventTest(event: RemoteAgenda): Boolean {
        val title = event.notes.toLowerCase()
        return (title.contains("compito") || title.endsWith("compito") || title.endsWith("verifica") || title.contains("verifica ")
                || title.contains("interrogazione scritta") || title.contains("prova ") || title.contains("test ") || title.endsWith("test") || title.contains("verifiche orali"))
    }

    fun isLessonTest(lesson: Lesson): Boolean {
        val title = lesson.mType.toLowerCase()
        return (title.contains("compito") || title.endsWith("compito") || title.endsWith("verifica") || title.contains("verifica ")
                || title.contains("interrogazione scritta") || title.contains("prova ") || title.contains("test ") || title.endsWith("test") || title.contains("verifiche orali"))
    }

    fun convertEvents(events: List<Any>): List<Event> {
        val list = mutableListOf<Event>()
        for (event in events) {
            if (event is SuperAgenda)
                list.add(Event(if (event.test) Color.parseColor("#FF9800") else Color.WHITE, event.agenda.start.time))
            else if (event is LocalAgendaPOJO) {
                list.add(Event(if (event.event.type.equals("verifica", ignoreCase = true)) Color.parseColor("#FF9800") else Color.WHITE, event.event.day))
            }
        }
        return list
    }

    fun deleteUser(account: Long) {
        DatabaseHelper.database.profilesDao().delete(account)
        DatabaseHelper.database.absencesDao().delete(account)
        DatabaseHelper.database.communicationsDao().delete(account)
        DatabaseHelper.database.foldersDao().deleteFiles(account)
        DatabaseHelper.database.foldersDao().deleteFolders(account)
        DatabaseHelper.database.gradesDao().delete(account)
        DatabaseHelper.database.lessonsDao().delete(account)
        DatabaseHelper.database.eventsDao().deleteRemote(account)
        DatabaseHelper.database.subjectsDao().delete(account)
    }

    fun fetchDataOfUser(c: Context?) {
        if (c == null) return

        val p = Profile.getProfile(c)
        updateSubjects(p)
        //updateLessons(p);
        //updateFolders(p);
        //updateAgenda(p);
        //updateAbsence(p);
        //updateBacheca(p);
        //updateNote(p);
        //updateMarks(p);
    }

    fun updateMarks(c: Context?) {
        if (c == null) return

        updateMarks(Profile.getProfile(c))
    }

    fun updateMarks(p: Profile?) {
        if (p == null) return

        handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_MARKS_START, null) }
        Spaggiari(p).api().getGrades().subscribe({
            DatabaseHelper.database.gradesDao().delete(p.id)
            DatabaseHelper.database.gradesDao().insert(it.getGrades(p))
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_MARKS_OK, null) }
        }) {
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_MARKS_KO, null) }
            it.printStackTrace()
        }
    }

    fun updateSubjects(p: Profile?) {
        if (p == null) return

        handler.post {
            NotificationManager.instance.postNotificationName(EventType.UPDATE_SUBJECTS_START, null)
            NotificationManager.instance.postNotificationName(EventType.UPDATE_TEACHERS_START, null)
        }

        Spaggiari(p).api().getSubjects().subscribe({
            val allTeachers = mutableListOf<Teacher>()
            DatabaseHelper.database.subjectsDao().deleteSubjects(p.id)

            for (subject in it.subjects) {
                allTeachers.addAll(subject.teachers)
                for (t in subject.teachers) {
                    val obj = SubjectTeacher(subject.id, t.id, p.id)
                    DatabaseHelper.database.subjectsDao().deleteSingle(p.id, subject.id, t.id)
                    DatabaseHelper.database.subjectsDao().insert(obj)
                }
            }

            DatabaseHelper.database.subjectsDao().insert(allTeachers)
            DatabaseHelper.database.subjectsDao().insert(it.subjects)

            handler.post {
                NotificationManager.instance.postNotificationName(EventType.UPDATE_SUBJECTS_OK, null)
                NotificationManager.instance.postNotificationName(EventType.UPDATE_TEACHERS_OK, null)
            }
        }) {
            handler.post {
                NotificationManager.instance.postNotificationName(EventType.UPDATE_TEACHERS_KO, null)
                NotificationManager.instance.postNotificationName(EventType.UPDATE_SUBJECTS_KO, null)
            }
            it.printStackTrace()
        }
    }

    fun updateLessons(c: Context?) {
        if (c == null) return

        updateLessons(Profile.getProfile(c))
    }

    fun updateLessons(p: Profile?) {
        if (p == null) return

        val dates = getStartEnd("yyyyMMdd")
        handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_LESSONS_START, null) }
        Spaggiari(p).api().getLessons(dates[0], dates[1]).subscribe({
            DatabaseHelper.database.lessonsDao().delete(p.id)
            DatabaseHelper.database.lessonsDao().insert(it.getLessons(p))
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_LESSONS_OK, null) }
        }) {
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_LESSONS_KO, null) }
            it.printStackTrace()
        }
    }

    fun updateFolders(c: Context?) {
        if (c == null) return

        updateFolders(Profile.getProfile(c))
    }

    fun updateFolders(p: Profile?) {
        if (p == null) return

        handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_FOLDERS_START, null) }
        Spaggiari(p).api().getDidactics().subscribe({
            val files = mutableListOf<File>()

            DatabaseHelper.database.foldersDao().deleteFiles(p.id)
            DatabaseHelper.database.foldersDao().deleteFolders(p.id)
            DatabaseHelper.database.subjectsDao().insert(it.didactics)

            //collect folders and files
            for (teacher in it.didactics) {
                for (folder in teacher.folders) {
                    folder.teacher = teacher.id
                    folder.profile = p.id
                    if (folder.name == "Uncategorized")
                        folder.name = "Altri materiali"
                    folder.id = DatabaseHelper.database.foldersDao().insert(folder)

                    for (file in folder.files) {
                        file.folder = folder.id
                        file.teacher = teacher.id
                        file.profile = p.id
                        files.add(file)
                    }
                }
                teacher.folders = emptyList()
            }

            DatabaseHelper.database.foldersDao().insertFiles(files) //update otherwise will clean any additional info (path...)

            //Download informations if not file
            for (f in DatabaseHelper.database.foldersDao().getNoFiles(p.id)) {
                if (f.type == "link")
                    Spaggiari(p).api().getAttachmentUrl(f.id)
                            .subscribe({ downloadURL -> DatabaseHelper.database.foldersDao().insert(FileInfo(f.objectId, downloadURL.item.link)) }, { it.printStackTrace() })
                else
                    Spaggiari(p).api().getAttachmentTxt(f.id)
                            .subscribe { downloadTXT -> DatabaseHelper.database.foldersDao().insert(FileInfo(f.objectId, downloadTXT.item.text)) }
            }
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_FOLDERS_OK, null) }
        }) {
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_FOLDERS_KO, null) }
            it.printStackTrace()
        }
    }

    fun updateAgenda(c: Context?) {
        if (c == null) return

        updateAgenda(Profile.getProfile(c))
    }

    fun updateAgenda(p: Profile?) {
        if (p == null) return

        val dates = getStartEnd("yyyyMMdd")
        handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_AGENDA_START, null) }
        Spaggiari(p).api().getAgenda(dates[0], dates[1]).subscribe({
            val apiAgenda = it.getAgenda(p)
            DatabaseHelper.database.eventsDao().deleteRemote(p.id)
            DatabaseHelper.database.eventsDao().insertRemote(apiAgenda)
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_AGENDA_OK, null) }
        }) {
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_AGENDA_KO, null) }
            it.printStackTrace()
        }
    }

    fun updateAbsence(c: Context?) {
        if (c == null) return

        updateAbsence(Profile.getProfile(c))
    }

    fun updateAbsence(p: Profile?) {
        if (p == null) return

        handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_ABSENCES_START, null) }
        Spaggiari(p).api().getAbsences().subscribe({
            DatabaseHelper.database.absencesDao().delete(p.id)
            DatabaseHelper.database.absencesDao().insert(it.getEvents(p))
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_ABSENCES_OK, null) }
        }) {
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_ABSENCES_KO, null) }
            it.printStackTrace()
        }
    }

    fun updateBacheca(c: Context?) {
        if (c == null) return

        updateBacheca(Profile.getProfile(c))
    }

    private fun updateBacheca(p: Profile?) {
        if (p == null) return

        handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_BACHECA_START, null) }
        Spaggiari(p).api().getBacheca().subscribe({
            val list = it.getCommunications(p).toMutableList()
            val toRemove = mutableListOf<Communication>()

            for (communication in list) {
                if (communication.cntStatus == "deleted") {
                    toRemove.add(communication)
                    continue
                }

                val info = DatabaseHelper.database.communicationsDao().getInfo(communication.myId)
                if (info == null || !communication.isRead || info.content.isEmpty()) {
                    println("REQUEST - " + communication.title)
                    Spaggiari(p).api().readBacheca(communication.evtCode, communication.id).subscribe { readResponse ->
                        communication.isRead = true
                        DatabaseHelper.database.communicationsDao().insert(communication)

                        val downloadedInfo = readResponse.item
                        downloadedInfo.id = communication.myId
                        downloadedInfo.content = if (downloadedInfo.content.isEmpty())
                            info?.content ?: ""
                        else
                            downloadedInfo.content
                        DatabaseHelper.database.communicationsDao().insert(downloadedInfo)
                    }
                }
            }
            list.removeAll(toRemove)

            DatabaseHelper.database.communicationsDao().delete(p.id)
            DatabaseHelper.database.communicationsDao().insert(list)
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_BACHECA_OK, null) }
        }) {
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_BACHECA_KO, null) }
            it.printStackTrace()
        }
    }

    fun updateNote(c: Context?) {
        if (c == null) return

        updateNote(Profile.getProfile(c))
    }

    fun updateNote(p: Profile?) {
        if (p == null) return

        handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_NOTES_START, null) }
        Spaggiari(p).api().getNotes().subscribe({
            DatabaseHelper.database.notesDao().delete(p.id)
            DatabaseHelper.database.notesDao().insert(it.getNotes(p))
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_NOTES_OK, null) }
        }) {
            handler.post { NotificationManager.instance.postNotificationName(EventType.UPDATE_NOTES_KO, null) }
            it.printStackTrace()
        }
    }


    fun downloadAttachment(c: Context?, communication: Communication) {
        if (c == null) return

        downloadAttachment(c, communication, Profile.getProfile(c))
    }

    private fun downloadAttachment(c: Context?, communication: Communication, p: Profile?) {
        if (p == null || c == null) return
        val dir = JavaFile(
                Environment.getExternalStorageDirectory().toString() +
                        JavaFile.separator +
                        "Registro Elettronico" + JavaFile.separator + "Circolari")

        handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_START, arrayOf(communication.myId)) }
        Spaggiari(p).api().getBachecaAttachment(communication.evtCode, communication.id).subscribe({
            if (it.isSuccessful) {
                val filename = getFileNameFromHeaders(it.headers())
                if (!dir.exists()) dir.mkdirs()
                val fileDir = JavaFile(dir, filename)

                var communicationInfo = DatabaseHelper.database.communicationsDao().getInfo(communication.myId)
                if (communicationInfo == null)
                    communicationInfo = CommunicationInfo()
                communicationInfo.id = communication.myId

                if (fileDir.exists() || writeResponseBodyToDisk(it.body(), fileDir)) {
                    communicationInfo.path = fileDir.absolutePath
                    DatabaseHelper.database.communicationsDao().update(communicationInfo)
                    handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_OK, arrayOf(communication.myId)) }
                } else {
                    handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_KO, arrayOf(communication.myId)) }
                }
            } else {
                handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_KO, arrayOf(communication.myId)) }
            }
        }, {
            handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_KO, arrayOf(communication.myId)) }
            it.printStackTrace()
        })
    }

    fun downloadFile(f: File, p: Profile?) {
        if (p == null) return

        val dir = JavaFile(
                Environment.getExternalStorageDirectory().toString() +
                        JavaFile.separator +
                        "Registro Elettronico" + JavaFile.separator + "Didattica")

        handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_START, arrayOf(f.objectId)) }
        Spaggiari(p).api().getAttachmentFile(f.id.toInt().toLong()).subscribe({
            if (it.isSuccessful) {
                val filename = getFileNameFromHeaders(it.headers())
                if (!dir.exists()) dir.mkdirs()
                val fileDir = JavaFile(dir, filename)

                val info = FileInfo(f.objectId, fileDir.absolutePath)
                val id = DatabaseHelper.database.foldersDao().insert(info)
                if (id > 0 && writeResponseBodyToDisk(it.body(), fileDir))
                    handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_OK, arrayOf(f.objectId)) }
                else
                    handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_KO, arrayOf(f.objectId)) }
            } else {
                handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_KO, arrayOf(f.objectId)) }
            }

        }, {
            it.printStackTrace()
            handler.post { NotificationManager.instance.postNotificationName(EventType.DOWNLOAD_FILE_KO, arrayOf(f.objectId)) }
        })
    }

    fun getStartEnd(format: String): Array<String> {
        val from = Calendar.getInstance()
        val to = Calendar.getInstance()

        if (from.get(Calendar.MONTH) >= Calendar.SEPTEMBER) { // Prima di gennaio
            to.add(Calendar.YEAR, 1)
        } else {
            from.add(Calendar.YEAR, -1)
        }
        from.set(Calendar.DAY_OF_MONTH, 1)
        from.set(Calendar.MONTH, Calendar.SEPTEMBER)

        to.set(Calendar.DAY_OF_MONTH, 31)
        to.set(Calendar.MONTH, Calendar.AUGUST)

        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        return arrayOf(simpleDateFormat.format(from.time), simpleDateFormat.format(to.time))
    }

    fun capitalizeFirst(a: String) = a.substring(0, 1).toUpperCase() + a.substring(1)

    fun loginFeedback(error: Throwable, c: Context) {
        error.printStackTrace()
        if (error is HttpException) {
            if (error.code() == 422)
                Toast.makeText(c, R.string.credenziali, Toast.LENGTH_LONG).show()
            else if (error.code() == 400) {
                Toast.makeText(c, "Bad request", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(c, c.getString(R.string.login_msg_failer, error.localizedMessage), Toast.LENGTH_LONG).show()
        }
    }

    fun openFile(context: Context, file: JavaFile, bar: Snackbar) {
        val mime = URLConnection.guessContentTypeFromName(file.toString())
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file), mime)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            bar.show()
        }
    }

    fun openLink(context: Context, url: String, snackbar: Snackbar) {
        var newUrl = url
        if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) newUrl = "http://" + newUrl

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
        try {
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            snackbar.show()
        }

    }

    fun eventToString(e: SuperAgenda) =
            capitalizeFirst(complex.format(e.agenda.start)) + "\n" + capitalizeFirst(e.agenda.notes)

    fun getAccountImage(nome: String): Bitmap {
        val src = Bitmap.createBitmap(255, 255, Bitmap.Config.ARGB_8888)
        src.eraseColor(Color.parseColor("#03A9F4"))
        val nomef = StringBuilder()
        val lett = nome.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (s in lett) {
            nomef.append(s.substring(0, 1).toUpperCase())
        }
        val cs = Canvas(src)
        val tPaint = Paint()
        val reduce = tPaint.measureText(nomef.toString())
        tPaint.textSize = 100 - reduce
        tPaint.color = Color.WHITE
        tPaint.style = Paint.Style.FILL
        tPaint.textAlign = Paint.Align.CENTER
        val x = (src.width / 2).toFloat()
        val height = (src.height / 2 + 33).toFloat()
        cs.drawText(nomef.toString(), x, height, tPaint)
        return src
    }
}
