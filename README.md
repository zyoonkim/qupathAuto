# 🧬 QuPath Automation Project Documentation

## 1. Project Overview

### 1.1 Problem Statement

Running QuPath bioimaging analysis is time-consuming and inefficient for large datasets due to the need to process each image manually.

### 1.2 Project Objective

Develop an automation solution to streamline the QuPath analysis process for all images in a given project.

### 1.3 Solution Approach

Utilize QuPath's scripting capabilities to run predefined Groovy scripts in batch mode. Create a system that applies the same detection and analysis parameters to all images in a project and exports the results to a structured CSV file.

## 2. Technical Specifications

### 2.1 Tech Stack 🧰

- **QuPath**: Primary tool for histological image annotation and detection.
- **Groovy/Java**: Scripting language used inside QuPath.
- **Python**: For auxiliary file handling (e.g., CSV-to-Excel conversion).
- **VS Code**: IDE for editing scripts.

## 3. Implementation Challenges

### 3.1 Documentation Issues 📚

- Sparse and outdated documentation on Groovy scripting and QuPath.
- Limited guidance on file handling and project-relative paths.

### 3.2 Automation Hurdles 🔁

- Manual image loading.
- Inconsistent clearing of prior annotations.
- Default script behavior required manual execution per image.

## 4. Usage Guidelines

### 4.1 QuPath Download 💾

- Go to [QuPath](https://qupath.github.io) and click the appropriate Download button for your OS.
- If macOS blocks the app, go to System Settings > Privacy & Security and click "Open Anyway" to bypass the block.

### 4.2 File Setup 📁

- Create a folder titled after the project, e.g., `HepcidinOnAAABySex`.
- Inside this folder, create another subfolder.
- Launch QuPath and select "Create Project," pointing to the inner folder.
- Add your image files by dragging them over the QuPath app.

### 4.3 Running the Code ▶️

- Copy `stainv2.groovy` into the scripts folder that QuPath automatically generates.
- Open any image in the project.
- Use `Cmd + [` (Mac) or `Ctrl + [` (Windows/Linux) to open the Script Editor.
- Click "Open" and select `stainv2.groovy`.
- Modify the output file path in the script (line 2). Example:
  ```groovy
  def csvFile = new File('/absolute/path/to/cell_detection_results.csv')
  ```
  **Tip:** Right-click your folder in Finder or File Explorer, choose "Get Info" or "Properties," and copy the full path.
- Click "Run".
- Your CSV output will be saved in this format:
  ```
  ImageName, TotalCells, NegativeCells, PositivePercentage, PositiveArea
  ```

### 4.4 Additional Features

#### Clear CSV File 🧹

You can use the Python script below to reset the output file:

```python
csv_file_path = '/absolute/path/to/cell_detection_results.csv'
with open(csv_file_path, 'w') as file:
    file.truncate(0)
print(f"{csv_file_path} has been wiped clean.")
```

#### Convert CSV to Excel 📊

Use the `csvtoxl.py` script:

```python
import pandas as pd
csv_file_path = "./cell_detection_results.csv"
excel_file_path = "./cell_detection_results.xlsx"
try:
    df = pd.read_csv(csv_file_path)
    df.to_excel(excel_file_path, index=False, engine='openpyxl')
    print(f"Excel file successfully created at {excel_file_path}")
except Exception as e:
    print(f"An error occurred: {e}")
```

**Note:** Before running, install dependencies in a virtual environment:

```sh
pip install pandas openpyxl
# or
pip3 install pandas openpyxl
# (If your computer has Python3 downloaded instead of Python)
```

## 5. Behind the Project

Developed by Zachary Yoon-Kim.

Project completed as part of a Groovy scripting-based automation solution for histological image analysis on "Disparate Impact of the Iron Transport Pathway by Sex".

## Project Structure

- **analysisProject/**: Main QuPath project folder
  - `scripts/`: Contains all Groovy scripts for QuPath automation
  - `data/`: Project data files
  - `imgs/`: Project images
  - `classifiers/`: Classifier files
  - `venv/`: Python virtual environment (if used)
  - `project.qpproj`: QuPath project file
- **results/**: Output files (CSV, Excel)
- **utils/**: Python utility scripts for CSV/Excel manipulation

## Requirements

- QuPath
- Python (for utility scripts)
  - Required packages: `pandas`, `openpyxl`

## Notes

- All Groovy scripts are kept for history/documentation. Those other than `stainv2.groovy` were testing or older iterations, so make sure you run the QuPath software with `stainv2.groovy`.
- Python utilities are in the `utils/` directory.
