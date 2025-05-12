# Wipe the contents of a CSV file
csv_file_path = '/Users/zyoonkim/cs/qupathAutomation/cell_detection_results.csv'

# Open the file in write mode to clear it
with open(csv_file_path, 'w') as file:
    file.truncate(0)

print(f"{csv_file_path} has been wiped clean.")