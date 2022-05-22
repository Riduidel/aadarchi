def logger = java.util.logging.Logger.getLogger("verify")
File logFile = new File(basedir, "build.log")
if(logFile.exists()) {
    logger.info("""howdy, there is a log file! So read its content, and search for all lines containing GenerateDiagramsStep.
Is there one line containing project version? And if so, does it contains the "project-version" value?""")
    def lines = logFile.readLines()
    // Now filter that collection
    def inStep = lines.findAll { it.contains "ArchitectureEnhancer" }
    assert inStep.size>1
    // We have log lines, but do we also have generated content ?
    File structurizrOutput = new File(basedir, "target/structurizr")
    assert structurizrOutput.exists() && structurizrOutput.isDirectory()
    File enhancements = new File(structurizrOutput, "enhancements")
    assert enhancements.exists() && enhancements.isDirectory()
} else {
    throw new RuntimeException("can't analyze log file if it doesn't exist")
}
