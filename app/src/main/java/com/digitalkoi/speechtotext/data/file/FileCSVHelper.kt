package com.digitalkoi.speechtotext.data.file

import android.os.Environment
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar

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
      var list: List<CSVConversation> = listOf()
      try {
//      val fileStream = FileInputStream(file)
//      val streamReader = InputStreamReader(fileStream, StandardCharsets.UTF_8)
        val csvReader = CSVReader(FileReader(filePath))
        convert(csvReader.readAll())
      } catch (e: IOException) {
        Timber.e(e)
      }
      return list
    }

    private fun gettingIdFromFile(): String {
      try {
        val csvReader = CSVReader(FileReader(filePath))
        val toString = csvReader.readAll().size + 1
        return toString.toString()
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
      TODO(
          "not implemented"
      ) //To change body of created functions use File | Settings | File Templates.
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