def logger = java.util.logging.Logger.getLogger("verify")
File logFile = new File(basedir, "build.log")
if(logFile.exists()) {
    logger.info("""howdy, there is a log file! So read its content, and search for all lines containing GenerateDiagramsStep.
Is there one line containing project version? And if so, does it contains the "project-version" value?""")
    def lines = logFile.readLines()
    // Now filter that collection
    def inStep = lines.findAll { it.contains "GenerateDiagramsStep" }
    def projectVersion = inStep.findAll { it.contains "project version" }
    assert projectVersion.size==1
    def message = projectVersion[0].substring(projectVersion[0].indexOf(")")+1)
    logger.info """Found message "${message}". Does it contains ${pluginVersion} """
    assert message.contains(pluginVersion)
} else {
    throw new RuntimeException("can't analyze log file if it doesn't exist")
}
