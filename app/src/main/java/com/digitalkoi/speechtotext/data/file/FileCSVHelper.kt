package com.digitalkoi.speechtotext.data.file

import android.os.Environment
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 22/03/18.
 */

class FileCSVHelper {

  companion object {

    private val fileDir: File by lazy {
      File(Environment.getExternalStorageDirectory().absolutePath +
          File.separator + "CHISI")}
    private val filePath: String by lazy { Environment.getExternalStorageDirectory().absolutePath +
        File.separator + "CHISI" +
        File.separator + "Conversations.csv" }

    fun writeFile(
      patientId: String,
      conversation: String
    ) {
      existFile()
      val csvWriter = CSVWriter(FileWriter(filePath, true))
      val record = (gettingIdFromFile() + "," + gettingData() + "," + patientId + "," + conversation)
          .split(",").toTypedArray()
      csvWriter.writeNext(record)
      csvWriter.close()
    }

    fun readFile(): List<CSVConversation> {
      var list: List<CSVConversation>
      try {
//      val fileStream = FileInputStream(file)
//      val streamReader = InputStreamReader(fileStream, StandardCharsets.UTF_8)
        val csvReader = CSVReader(FileReader(filePath))
        list = convert(csvReader!!.readAll())
        if (list != null) return list
      } catch (e: IOException) {
        Timber.e(e)
      }
      return listOf()
    }

    private fun gettingIdFromFile(): String {
      try {
        val csvReader = CSVReader(FileReader(filePath))
        return (csvReader.readAll().size + 1).toString()
      } catch (e: IOException) {
        Timber.e(e)
      }
      return "1"
    }

    private fun gettingData(): String {
      val calendar = Calendar.getInstance()
      val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      return dateFormat.format(calendar.time)
    }

    private fun convert(csvData: MutableList<Array<String>>): List<CSVConversation> {
      var list = ArrayList<CSVConversation>()
      csvData.forEachIndexed { index, strings ->
        run {
          list.add(index, CSVConversation(strings[0], strings[1], strings[2], strings[3]))
        }
      }
      return list.toList()
    }

    private fun existFile() {
      if (!fileDir.exists()) {
        try {
          fileDir.mkdir()
        } catch (e: Exception) {
          Timber.e(e)
        }
      }

      val file = File(filePath)

      if (!file.exists()) {
        try {
          file.createNewFile()
        } catch (e: IOException) {
          Timber.e(e)
        }
      }
    }

  }
}