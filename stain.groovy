def project = getProject() 
def outputDir = new File("/Users/zyoonkim/cs/qupathAutomation/UROPQuPath/codeddata/annotation_summary.csv")  

// Ensure the output directory exists
if (!outputDir.exists()) {
    outputDir.mkdirs()
}

// Open CSV writer for the overall summary
def summaryCsv = new File(outputDir, "annotation_summary.csv")
summaryCsv.withWriter { writer ->
    // Write header for the CSV file
    writer.writeLine("Image,Num Detections,Num Negative,Positive %,Area µm^2")

    for (entry in project.getImageList()) {  
        def imageData = entry.readImageData()  // Get image data
        def hierarchy = imageData.getHierarchy()  // Access the hierarchy (annotations)

        // Clear existing annotations and objects
        clearAnnotations()
        clearAllObjects()

        // Create new annotations using classifiers
        createAnnotationsFromPixelClassifier("Tissue - VVG (v1)", 0.0, 0.0, "INCLUDE_IGNORED")
        selectAnnotations()
        createAnnotationsFromPixelClassifier("Pixel - VVG (v1)", 0.0, 0.0, "SELECT_NEW")

        // Run plugin and resolve hierarchy
        runPlugin('qupath.imagej.superpixels.SLICSuperpixelsPlugin', '{..}')
        resolveHierarchy()

        // Remove unnecessary annotations
        def firstAnnotation = getAnnotationObjects().findAll { it.getLevel() == 1 }
        removeObjects(firstAnnotation, true)

        def ignoreAnnotation = getAnnotationObjects().findAll { it.getPathClass() == getPathClass("Ignore*") }
        removeObjects(ignoreAnnotation, true)

        // Fetch fresh annotations from the updated hierarchy
        def updatedAnnotations = hierarchy.getAnnotationObjects()

        // Initialize counters for positive and negative detections
        int numPositive = 0
        int numNegative = 0
        double totalArea = 0.0

        // Loop through updated annotations and count positive/negative
        updatedAnnotations.each { annotation ->
            def pathClass = annotation.getPathClass()
            if (pathClass == getPathClass("Positive")) {
                numPositive++
            } else if (pathClass == getPathClass("Negative")) {
                numNegative++
            }
            
            // Safely add up the area (in µm²) if ROI exists
            if (annotation.getROI() != null) {
                totalArea += annotation.getROI().getArea()
            }
        }

        // Calculate positive percentage
        double positivePercent = (numPositive + numNegative > 0) ? (numPositive / (numPositive + numNegative)) * 100 : 0.0

        // Write the data for this image to the CSV file
        writer.writeLine("${entry.getImageName()},${updatedAnnotations.size()},${numNegative},${String.format('%.2f', positivePercent)},${String.format('%.2f', totalArea)}")

        // Optionally print the image name and number of annotations
        print entry.getImageName() + '\t' + updatedAnnotations.size()
    }
}