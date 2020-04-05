import org.jsoup.*

def writeTemplate(dir, sourceUrl, title, index) {
    def paddedIndex = index.toString().padLeft(2, '0')
    String idStr = sourceUrl.substring(sourceUrl.lastIndexOf('/') + 1);
    def filename = "${paddedIndex}-${idStr}"
    File target = new File("${dir}/${filename}.adoc")
    File html = new File("${dir}/${filename}.html")
    // Now get file !
    def doc = Jsoup.connect(sourceUrl).get()
    def content = doc.body().select("div#content").first()
    log.info "File to write is ${html.absolutePath}"
    // Write that content to an html temporary file
    html.parentFile.mkdirs()
    html.write content.html()
    // Call pandoc to transform it into asciidoc
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = "pandoc --from=html --to=asciidoc --output=${target} ${html}".execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000)
    // Delete the html file
    html.delete()
    return target.name
}

def downloadAgileArchitectureTemplates(pageUrl, templatesDir) {
    log.info "Opening Simon Brown root page for agile architecture at "+pageUrl
    def doc = Jsoup.connect(pageUrl).get()

    log.info "Downloaded document titled " + doc.title()

    // First download the page text as index.adoc
    def content = doc.body().select(".contentItemBody").first()
    // Then download each link
    new File("${templatesDir}/index.adoc").withWriter('utf-8') { writer ->
        writer.writeLine """
:toc:
:toclevels: 5

= ${project.name}

        """
        def list = content.select("ol > li > a").eachWithIndex { node, index ->
            def filename = writeTemplate(templatesDir, node.attr("href"), node.text(), index+1)
            writer.writeLine "include::${filename}[[leveloffset=+0]]\n"
        }
    }
}

downloadAgileArchitectureTemplates(
    project.properties["pageUrl"],
    project.properties["templatesDir"])