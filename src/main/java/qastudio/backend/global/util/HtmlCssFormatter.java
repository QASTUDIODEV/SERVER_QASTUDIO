package qastudio.backend.global.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlCssFormatter {

    public static String formatHtml(String html) {
        try {
            Document document = Jsoup.parse(html);
            document.outputSettings()
                    .indentAmount(4) // 들여쓰기 수준 설정
                    .prettyPrint(true); // 자동 정렬 활성화
            return document.outerHtml().replace("><", ">\n<"); // 개행 강제 추가
        } catch (Exception e) {
            return html; // 예외 발생 시 원본 HTML 반환
        }
    }

    public static String formatCss(String css) {
        return css.replace(";", ";\n"); // CSS 개행 추가
    }
}
