def projectDir = "/path/to/your/project/images"
def csvFile = new File("/Users/zyoonkim/cs/qupathAutomation/UROPQuPath/codeddata/annotation_summary.csv")
def header = "Image,Cell Count,Positive Cell Count,Measurements\n"

if (!csvFile.exists()) {
    csvFile.write(header)
}

new File(projectDir).eachFile { file ->
    if (file.name.endsWith(".jpg") || file.name.endsWith(".png") || file.name.endsWith(".tiff")) {
        println("Processing image: ${file.name}")
        
        setImageType('BRIGHTFIELD_H_DAB')
        setColorDeconvolutionStains('{"Name" : "H-DAB estimated", ' +
        '"Stain 1" : "Hematoxylin", "Values 1" : "0.8228 0.52632 0.21444", ' +
        '"Stain 2" : "DAB", "Values 2" : "0.32346 0.46972 0.82142", ' +
        '"Background" : "232 232 231"}')

        openImage(file.absolutePath)
        createFullImageAnnotation(true)

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
        
        def measurements = getMeasurementNames()
        def cellCount = getCellCount()
        def positiveCells = getPositiveCellCount()

        def data = "${file.name},${cellCount},${positiveCells},${measurements.join(',')}\n"
        csvFile.append(data)
        
        println("Processed image: ${file.name}")
    }
}

println("All images processed and results written to CSV!")