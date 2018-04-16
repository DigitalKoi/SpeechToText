package com.digitalkoi.speechtotext.data.file

import android.os.Environment
import android.text.TextUtils
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
      File(
          Environment.getExternalStorageDirectory().absolutePath +
              File.separator + "CHISI"
      )
    }
    private val filePath: String by lazy {
      Environment.getExternalStorageDirectory().absolutePath +
          File.separator + "CHISI" +
          File.separator + "DataLog.csv"
    }

    fun writeItemInFile(patientId: String, conversation: String) {
      existFile()
      val record =
        (gettingIdForNextItem() + "," + gettingData() + "," + patientId + "," + conversation)
            .split(",")
            .toTypedArray()
      val csvWriter = CSVWriter(FileWriter(filePath, true))
      csvWriter.writeNext(record)
      csvWriter.close()
    }

    fun readAllFile(date: String): List<CSVConversation> {
      try {
        val csvReader = CSVReader(FileReader(filePath))
        var list: List<CSVConversation> = convert(csvReader!!.readAll())
        if (list != null && TextUtils.isEmpty(date)) {
          return list
        } else if (!TextUtils.isEmpty(date)) {
          val listFromDate = ArrayList<CSVConversation>()
          list.forEach { item ->
            if (date == item.time.substring(0, 10))
              listFromDate.add(item)
          }
          return listFromDate
        }
      } catch (e: IOException) {
        Timber.e(e)
      }
      return listOf()
    }

    fun gettingItem(id: String): CSVConversation? {
      readAllFile("").forEach { item ->
        if (item.serialNumber.equals(id))
          return item
      }
      return null
    }

    fun savingItem(item: CSVConversation) {
      var list = ArrayList<CSVConversation>()
      readAllFile("").forEachIndexed { index, itemFile ->
        run {
          if (!itemFile.serialNumber.equals(item.serialNumber))
            list.add(index, itemFile)
          else
            list.add(index, item)
        }
      }
      rewritingFile(list)

    }

    fun deletingItem(item: CSVConversation) {
      var list = ArrayList<CSVConversation>()
      readAllFile("").forEachIndexed { index, itemFile ->
        run {
          if (!itemFile.serialNumber.equals(item.serialNumber))
            list.add(itemFile)
        }
      }
      rewritingFile(list)

    }

    private fun rewritingFile(list: ArrayList<CSVConversation>) {
      deleteFile()
      existFile()
      val csvWriter = CSVWriter(FileWriter(filePath, true))
      sortListId(list).forEach { item ->
        run {
          val record =
            (item.serialNumber + "," + item.time + "," + item.patientId + "," + item.conversation)
                .split(",")
                .toTypedArray()
          csvWriter.writeNext(record)
        }
      }
      csvWriter.close()
    }

    private fun gettingIdForNextItem(): String {
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

    private fun sortListId(list:ArrayList<CSVConversation>): ArrayList<CSVConversation> {
      val sortedList = ArrayList<CSVConversation>()
      list.forEachIndexed { index, item ->
        sortedList.add(
            CSVConversation(
                (index + 1).toString(),
                item.time,
                item.patientId,
                item.conversation
                )
        )
      }
      return sortedList
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

    private fun deleteFile() {
      val file = File(filePath)
      file.delete()
    }
  }
}