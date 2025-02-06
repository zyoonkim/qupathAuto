// Load the current project
def project = getProject()
if (project == null) {
    println("No project is open. Please open a QuPath project first.")
    return
}

def csvFile = new File("/Users/zyoonkim/cs/qupathAutomation/analysisProject/codeddatacopy.csv") // Update with your desired CSV path
def header = "Image,Num Detections,Num Negative,Positive %,Area µm^2\n"

// Ensure the CSV file has the header
if (!csvFile.exists()) {
    println("Creating CSV file with header")
    csvFile.write(header)
}

for (entry in project.getImageList()) {
    def imageData = entry.readImageData()
    def imageName = entry.getImageName()

    println("Processing image: ${imageName}")

    // Set color deconvolution stains
    setColorDeconvolutionStains('{"Name" : "H-DAB estimated", ' +
        '"Stain 1" : "Hematoxylin", "Values 1" : "0.8228 0.52632 0.21444", ' +
        '"Stain 2" : "DAB", "Values 2" : "0.32346 0.46972 0.82142", ' +
        '"Background" : "232 232 231"}')

    createFullImageAnnotation(true)

    // Run the cell detection plugin
    runPlugin('qupath.imagej.detect.cells.PositiveCellDetection',
    '{"detectionImageBrightfield": "Hematoxylin OD", ' +
    ' "requestedPixelSizeMicrons": 0.5, ' +
    ' "backgroundRadiusMicrons": 8.0, ' +
    ' "medianRadiusMicrons": 0.0, ' +
    ' "sigmaMicrons": 1.5, ' +
    ' "minAreaMicrons": 10.0, ' +
    ' "maxAreaMicrons": 400.0, ' +
    ' "threshold": 0.1, ' +
    ' "maxBackground": 2.0, ' +
    ' "watershedPostProcess": true, ' +
    ' "excludeDAB": false, ' +
    ' "cellExpansionMicrons": 8.0, ' +
    ' "includeNuclei": true, ' +
    ' "smoothBoundaries": true, ' +
    ' "makeMeasurements": true, ' +
    ' "thresholdCompartment": "Cytoplasm: DAB OD mean", ' +
    ' "thresholdPositive1": 0.2, ' +
    ' "thresholdPositive2": 0.4, ' +
    ' "thresholdPositive3": 0.6, ' +
    '"singleThreshold": true}')

    // Get detection objects
    def detections = getDetectionObjects()
    def cellCount = detections.size()

    // Identify positive cells
    def positiveCells = detections.findAll { it.getPathClass() == getPathClass('Positive') }
    def numNegative = cellCount - positiveCells.size()
    
    // Calculate the positive percentage
    def positivePercentage = (cellCount > 0) ? (positiveCells.size() / cellCount) * 100 : 0
    
    // Calculate the area of positive cells in µm²
    def positiveArea = positiveCells.collect { it.getROI().getArea() }.sum()

    // Log the information for debugging
    println("Detected ${cellCount} cells, ${positiveCells.size()} positive cells, negative: ${numNegative}, positive area: ${positiveArea} µm².")

    // Write the data to CSV
    def data = "${imageName},${cellCount},${numNegative},${positivePercentage},${positiveArea}\n"
    csvFile.append(data)

    println("Processed and saved data for: ${imageName}")
}

println("Script execution complete.")