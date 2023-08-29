package com.kodcu.asciidocfx;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by usta on 15.03.2015.
 */
public class App {

    public static void main(String[] args) {

        String result = MarkdownToAsciidoc.convert("# Merhaba Dünya");

        MarkdownToAsciidoc.convert("# Merhaba Dünya", r -> {
            System.out.println(r);
        });

        Path outputPath = Paths.get("E:\\output.asciidoc");
        MarkdownToAsciidoc.convert("# Merhaba Dünya", outputPath);

    }
}
