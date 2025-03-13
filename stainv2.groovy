// Open CSV file for results
def csvFile = new File(buildFilePath(PROJECT_BASE_DIR, 'cell_detection_results.csv'))
csvFile.text = "ImageName,TotalCells,NegativeCells,PositivePercentage,PositiveArea\n"

// Loop through all images in the project
for (entry in getProject().getImageList()) {
    println("Activating image: ${entry.getImageName()}")

    // Get ImageData and set it as the current image
    def imageData = entry.readImageData()
    setBatchProjectAndImage(getProject(), imageData)

    // Create full image annotation
    createFullImageAnnotation(true)

    // Run positive cell detection
    runPlugin('qupath.imagej.detect.cells.PositiveCellDetection',
    '{"detectionImageBrightfield": "Hematoxylin OD", ' +
    '"requestedPixelSizeMicrons": 0.5, ' +
    '"backgroundRadiusMicrons": 8.0, ' +
    '"medianRadiusMicrons": 0.0, ' +
    '"sigmaMicrons": 1.5, ' +
    '"minAreaMicrons": 10.0, ' +
    '"maxAreaMicrons": 400.0, ' +
    '"threshold": 0.1, ' +
    '"maxBackground": 2.0, ' +
    '"watershedPostProcess": true, ' +
    '"excludeDAB": false, ' +
    '"cellExpansionMicrons": 8.0, ' +
    '"includeNuclei": true, ' +
    '"smoothBoundaries": true, ' +
    '"makeMeasurements": true, ' +
    '"thresholdCompartment": "Cytoplasm: DAB OD mean", ' +
    '"thresholdPositive1": 0.2, ' +
    '"thresholdPositive2": 0.4, ' +
    '"thresholdPositive3": 0.6, ' +
    '"singleThreshold": true}')

    // Analyze detection results
    def detections = getDetectionObjects()
    def cellCount = detections.size()
    def positiveCells = detections.findAll { it.getPathClass() == getPathClass('Positive') }
    def numNegative = cellCount - positiveCells.size()
    def positivePercentage = (cellCount > 0) ? (positiveCells.size() / cellCount) * 100 : 0
    def positiveArea = positiveCells.collect { it.getROI()?.getArea() ?: 0 }.sum()

    // Append results to CSV
    def data = "${entry.getImageName()},${cellCount},${numNegative},${positivePercentage.round(2)},${positiveArea}\n"
    csvFile.append(data)

    println("Processed and saved data for: ${entry.getImageName()}")
}

println("Script execution complete.")