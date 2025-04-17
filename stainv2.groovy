// Open CSV file for results
def csvFile = new File('/Users/zyoonkim/cs/qupathAutomation/cell_detection_results.csv')
csvFile.text = "ImageName,TotalCells,NegativeCells,PositivePercentage,PositiveArea\n"

// Loop through all images in the project
for (entry in getProject().getImageList()) {
    println("Activating image: ${entry.getImageName()}")

    // Load image data
    def imageData = entry.readImageData()
    setBatchProjectAndImage(getProject(), imageData)

    // Clear previous annotations/detections
    clearAllObjects()

    // Create annotation over full image
    createFullImageAnnotation(true)

    // ✅ Set the image type first (must be brightfield-compatible)
    setImageType('BRIGHTFIELD_H_DAB')

    // ✅ Now set stain vector (H-DAB)
    setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.701190 0.29049", "Stain 2" : "DAB", "Values 2" : "0.26917 0.568240 0.77759", "Background" : "255 255 255"}')

    // Run positive cell detection
    runPlugin('qupath.imagej.detect.cells.PositiveCellDetection',
    '{"detectionImageBrightfield": "Optical density sum", ' +
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
    '"thresholdCompartment": "Nucleus: DAB OD mean", ' +
    '"thresholdPositive1": 0.2, ' +
    '"thresholdPositive2": 0.4, ' +
    '"thresholdPositive3": 0.6, ' +
    '"singleThreshold": true}')

    // Analyze detection results
    def detections = getDetectionObjects()
    def cellCount = detections.size()
    def positiveCells = detections.findAll { it.getPathClass()?.toString()?.contains('Positive') }
    def numNegative = cellCount - positiveCells.size()
    def positivePercentage = (cellCount > 0) ? (positiveCells.size() / cellCount) * 100 : 0
    def positiveArea = positiveCells*.getROI().findAll { it != null }*.getArea().sum()

    // Append results to CSV
    def data = "${entry.getImageName()},${cellCount},${numNegative},${positivePercentage.round(2)},${positiveArea}\n"
    csvFile.append(data)

    println("Processed and saved data for: ${entry.getImageName()}")
}

println("Script execution complete.")