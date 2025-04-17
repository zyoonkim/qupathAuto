// Set the name of the image you want to test
def targetImageName = "actin-4609.00-obj10x.jpg"

// Try to find that image in the project
def entry = getProject().getImageList().find { it.getImageName() == targetImageName }

if (entry == null) {
    println "❌ Image not found: ${targetImageName}"
    return
}

println "🔍 Testing image: ${entry.getImageName()}"

// Load and set the image
def imageData = entry.readImageData()
setBatchProjectAndImage(getProject(), imageData)

// Clear previous detections and annotations
clearAllObjects()

// Annotate the full image
createFullImageAnnotation(true)

// Set the image type and stain vector
setImageType('BRIGHTFIELD_H_DAB')
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


// Get results
def detections = getDetectionObjects()
def cellCount = detections.size()
def positiveCells = detections.findAll { it.getPathClass()?.toString()?.contains('Positive') }
def numNegative = cellCount - positiveCells.size()
def positivePercentage = (cellCount > 0) ? (positiveCells.size() / cellCount) * 100 : 0
def positiveArea = positiveCells*.getROI().findAll { it != null }*.getArea().sum()

// Print results
println "\n📊 ---- Detection Results for ${entry.getImageName()} ----"
println "Total cells:        ${cellCount}"
println "Negative cells:     ${numNegative}"
println "Positive %:         ${positivePercentage.round(2)}"
println "Total positive area: ${positiveArea.round(2)}"