```scala
// Databricks notebook source

// DBTITLE 1,Libraries
// Importing the required libraries
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import java.io.FileOutputStream
import org.apache.spark.sql.DataFrame

// Initializing Spark session
val spark = SparkSession.builder.appName("FatalPoliceShootings").getOrCreate()

// Path to the CSV file
val filePath = "dbfs:/FileStore/fatal-police-shootings/fatal_police_shootings_data.csv"

// Loading the CSV file into a DataFrame
val df = spark.read.option("header", "true").option("inferSchema", "true").csv(filePath)

// Showing the schema and first few rows 
df.printSchema()
df.show(5)

// DBTITLE 1,Year Column
// Extracting the year from the date column
val dfWithYear = df.withColumn("year", year(col("date")))

dfWithYear.show(2, 50, true)

// DBTITLE 1,Distinct Years
// Getting distinct years
val years = dfWithYear.select("year").distinct().collect().map(_.getInt(0))

// Printing the distinct years
println("Distinct years:")
years.foreach(println)

// Writing data for each year to separate files
years.foreach { year =>
  val yearlyData = dfWithYear.filter(col("year") === year)
  
  println(s"Data for $year:")
  yearlyData.show(2, 50, true)
  
  yearlyData.write.mode("overwrite").option("header", "true").csv(s"dbfs:/FileStore/fatal-police-shootings/output/year_$year")
}

// DBTITLE 1,Filtering by 2022 and Texs
// Filtering for crimes committed in 2022 and in the state of Texas
val dfTexas2022 = dfWithYear.filter(col("year") === 2022 && col("state") === "TX")

dfTexas2022.show(2, 50, true)

// DBTITLE 1,Replacing Gender
// Replacing gender abbreviations with full words
val dfTexas2022WithGender = dfTexas2022.withColumn("gender", when(col("gender") === "M", "Male")
    .when(col("gender") === "F", "Female")
    .otherwise("N/A")
)

dfTexas2022WithGender.show(2, 50, true)

// DBTITLE 1,1. Deaths by state
// 1. Summary of deaths by state
val deathsByState = df.groupBy("state").count().withColumnRenamed("count", "deaths")

deathsByState.show()

// DBTITLE 1,2. Breakdown by state, city, and type of death
// 2. Breakdown by state, city, and type of death
val breakdownByStateCityType = df.groupBy("state", "city", "manner_of_death").count().withColumnRenamed("count", "deaths")

breakdownByStateCityType.show()

// DBTITLE 1,3. Breakdown by state, city, type of death, and gender
// 3. Breakdown by state, city, type of death, and gender
val breakdownByStateCityTypeGender = df.groupBy("state", "city", "manner_of_death", "gender").count().withColumnRenamed("count", "deaths")

breakdownByStateCityTypeGender.show()

// DBTITLE 1,Saving to CSV Files
// File paths for the output CSV files
val summaryByStateCsvPath = "dbfs:/FileStore/fatal-police-shootings/summary_by_state.csv"
val breakdownByStateCityTypeCsvPath = "dbfs:/FileStore/fatal-police-shootings/breakdown_by_state_city_type.csv"
val breakdownByStateCityTypeGenderCsvPath = "dbfs:/FileStore/fatal-police-shootings/breakdown_by_state_city_type_gender.csv"

// Saving the summaries to different CSV files
deathsByState.write.mode("overwrite").option("header", "true").csv(summaryByStateCsvPath)

breakdownByStateCityType.write.mode("overwrite").option("header", "true").csv(breakdownByStateCityTypeCsvPath)

breakdownByStateCityTypeGender.write.mode("overwrite").option("header", "true").csv(breakdownByStateCityTypeGenderCsvPath)

// DBTITLE 1,Reading Files
// Reading each CSV file
val summaryByStateDf = spark.read.option("header", "true").csv(summaryByStateCsvPath)

val breakdownByStateCityTypeDf = spark.read.option("header", "true").csv(breakdownByStateCityTypeCsvPath)

val breakdownByStateCityTypeGenderDf = spark.read.option("header", "true").csv(breakdownByStateCityTypeGenderCsvPath)

// DBTITLE 1,Showing Final Data
// Showing the data
summaryByStateDf.show(50)
breakdownByStateCityTypeDf.show(50)
breakdownByStateCityTypeGenderDf.show(50)
