// Load the current project
def project = getProject()
if (project == null) {
    println("No project is open. Please open a QuPath project first.")
    return
}

def csvFile = new File("/path/to/your/output.csv") // Update with your desired CSV path
def header = "Image,Cell Count,Positive Cell Count,Measurements\n"

// Ensure the CSV file has the header
if (!csvFile.exists()) {
    println("Creating CSV file with header")
    csvFile.write(header)
}

for (entry in project.getImageList()) {
    def imageData = entry.readImageData()
    def imageName = entry.getImageName()
    
    println("Processing image: ${imageName}")

    // Set image type and color deconvolution stains
    setImageType('BRIGHTFIELD_H_DAB')
    setColorDeconvolutionStains('{"Name" : "H-DAB estimated", ' +
        '"Stain 1" : "Hematoxylin", "Values 1" : "0.8228 0.52632 0.21444", ' +
        '"Stain 2" : "DAB", "Values 2" : "0.32346 0.46972 0.82142", ' +
        '"Background" : "232 232 231"}')

    setImageData(imageData)
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

    // Collect measurements and cell counts
    def cellCount = getCellCount()
    def positiveCells = getPositiveCellCount()
    def measurements = getMeasurementList().collect { it.name }

    // Write the data to CSV
    def data = "${imageName},${cellCount},${positiveCells},${measurements.join(';')}\n"
    csvFile.append(data)

    println("Processed and saved data for: ${imageName}")
}

println("Script execution complete.")