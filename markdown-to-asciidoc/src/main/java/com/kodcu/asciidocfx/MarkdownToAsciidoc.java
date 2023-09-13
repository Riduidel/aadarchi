package com.kodcu.asciidocfx;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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

        try (InputStream markedStream = MarkdownToAsciidoc.class.getResourceAsStream("/marked.js");
             InputStream markedExtensionStream = MarkdownToAsciidoc.class.getResourceAsStream("/marked-extension.js");
             InputStreamReader markedIn = new InputStreamReader(markedStream, "UTF-8");
             InputStreamReader markedExtensionIn = new InputStreamReader(markedExtensionStream, "UTF-8");
             BufferedReader markedReader = new BufferedReader(markedIn);
             BufferedReader markedExtensionReader = new BufferedReader(markedExtensionIn);) {

            String markedScript = markedReader.lines().collect(Collectors.joining("\n"));
            String markedExtensionScript = markedExtensionReader.lines().collect(Collectors.joining("\n"));

            engine.eval(markedScript);
            engine.eval(markedExtensionScript);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convert(String markdown) {
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
