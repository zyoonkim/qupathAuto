import pandas as pd

# set file paths
csv_file_path = "/Users/zyoonkim/cs/qupathAutomation/UROPQuPath/codeddata/annotation_summary.csv"
excel_file_path = "/Users/zyoonkim/cs/qupathAutomation/UROPQuPath/codeddata/annotation_summary.xlsx"

try:
 
    df = pd.read_csv(csv_file_path)
    
    
    df.to_excel(excel_file_path, index=False, engine='openpyxl')

    print(f"Excel file successfully created at {excel_file_path}")
except Exception as e:
    print(f"An error occurred: {e}")