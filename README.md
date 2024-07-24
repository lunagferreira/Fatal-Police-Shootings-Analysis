# Fatal Police Shootings Analysis

## Overview

This project analyzes data on fatal police shootings in the United States. It was created to process and extract insights from the data using Scala for data processing and analysis. The tasks include generating year-wise data files, processing gender data, and creating a detailed Excel summary report.

## Dataset

The project utilizes the fatal police shootings data, which includes detailed records of each incident, such as the date, manner of death, whether the victim was armed, age, gender, race, city, state, and more.

### Files Included:
- `fatal-police-shootings-data.csv`

## Features

1. **Year-wise Data Files**: Generates separate files for each year, containing the deaths that occurred in that year.
2. **Texas 2023 Crimes**: Creates a DataFrame with crimes committed in Texas in 2023, modifies gender data to full words, and replaces null gender values with "N/A".
3. **Excel Summary Report**: Writes an Excel file with three sheets:
   - **Sheet 1**: Summary of deaths by state.
   - **Sheet 2**: Breakdown by state, city, and type of death.
   - **Sheet 3**: Further breakdown including the gender of the deceased.

## Technologies Used

- **Scala**: For data processing and analysis.
- **Apache Spark**: For handling large-scale data processing.
- **Pandas**: For data manipulation and preprocessing.
- **Openpyxl**: For writing Excel files.
