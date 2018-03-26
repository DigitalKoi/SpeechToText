package com.digitalkoi.speechtotext.data.file

import com.opencsv.bean.CsvBindByName
import java.util.Date

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 22/03/18.
 */

data class CSVConversation(
  @CsvBindByName
  val serialNumber: String,
  @CsvBindByName
  val time: String,
  @CsvBindByName
  val patientId: String,
  @CsvBindByName
  val conversation: String
)