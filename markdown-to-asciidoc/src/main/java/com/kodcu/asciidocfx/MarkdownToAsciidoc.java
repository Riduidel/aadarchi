package com.kodcu.asciidocfx;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by usta on 15.03.2015.
 */
public class MarkdownToAsciidoc {

    private static final ScriptEngineManager engineManager = new ScriptEngineManager();
    private static final ScriptEngine engine = engineManager.getEngineByName("graal.js");

    static {

        try {
        	String markedScript = IOUtils.toString(MarkdownToAsciidoc.class.getResource("/marked.js"), "UTF-8");
        	String markedExtensionScript = IOUtils.toString(MarkdownToAsciidoc.class.getResource("/marked-extension.js"), "UTF-8");

            engine.eval(markedScript);
            engine.eval(markedExtensionScript);

        } catch (Exception e) {
        	throw new UnsupportedOperationException("Unable to load marked scripts", e);
        }
    }

    /**
     * Convert the input string, considered as Markdown, into Asciidoc.
     * @see https://github.com/oracle/graaljs/blob/master/docs/user/NashornMigrationGuide.md#multithreading
     * @param markdown
     * @return
     */
    public static synchronized String convert(String markdown) {
        try {
            engine.put("markdown", markdown);
            return (String) engine.eval("markdownToAsciidoc(markdown)");
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    public static String convert(Path markdownPath) {
        try {
            List<String> markdownContent = Files.readAllLines(markdownPath);
            String markdown = String.join("\n", markdownContent);
            engine.put("markdown", markdown);
            return (String) engine.eval("markdownToAsciidoc(markdown)");
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    public static void convert(Path markdownPath, Path asciidocPath, StandardOpenOption... openOption) {

        try {
            String markdownContent = convert(markdownPath);
            Files.write(asciidocPath, markdownContent.getBytes(Charset.forName("UTF-8")), openOption);
        } catch (IOException e) {
            throw new ConversionException(e);
        }
    }

    public static void convert(File markdownFile, File asciidocFile, StandardOpenOption... openOption) {
        convert(markdownFile.toPath(), asciidocFile.toPath(), openOption);
    }

    public static void convert(Path markdownPath, Consumer<String> resultCallback) {

        CompletableFuture.runAsync(() -> {
            resultCallback.accept(convert(markdownPath));
        });

    }

    public static String convert(File markdownFile) {
        return convert(markdownFile.toPath());
    }

    public static void convert(File markdownFile, Consumer<String> resultCallback) {
        CompletableFuture.runAsync(() -> {
            resultCallback.accept(convert(markdownFile));
        });
    }

    public static void convert(String markdown, Consumer<String> resultCallback) {
        CompletableFuture.runAsync(() -> {
            resultCallback.accept(convert(markdown));
        });
    }

    public static void convert(String markdown, Path asciidocPath, StandardOpenOption... openOption) {
        try {
            String markdownContent = convert(markdown);
            Files.write(asciidocPath, markdownContent.getBytes(Charset.forName("UTF-8")), openOption);
        } catch (IOException e) {
            throw new ConversionException(e);
        }
    }

    public static void convert(String markdown, File asciidocFile, StandardOpenOption... openOption) {
        convert(markdown, asciidocFile.toPath(), openOption);
    }
}
