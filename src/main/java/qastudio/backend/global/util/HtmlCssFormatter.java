package qastudio.backend.global.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

public class HtmlCssFormatter {


    public static String formatHtml(String rawHtml) {
        Document document = Jsoup.parse(rawHtml);
        document.outputSettings()
                .prettyPrint(true) // 자동 개행 적용
                .escapeMode(Entities.EscapeMode.base); // 특수 문자 인코딩 유지
        return document.outerHtml();
    }


    public static String formatCss(String rawCss) {
        return rawCss.replaceAll("}", "}\n")  // CSS 블록마다 개행 추가
                .replaceAll(";", ";\n"); // 속성마다 개행 추가
    }}
