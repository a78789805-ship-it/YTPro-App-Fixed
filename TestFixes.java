import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class TestFixes {
    public static void main(String[] args) throws Exception {
        String jsCode = new String(Files.readAllBytes(Paths.get("ytpro.js")), StandardCharsets.UTF_8);
        
        jsCode = jsCode
            .replace("Disclaimer", "تنبيه")
            .replace("if(isD)var a=[\"#000\",\"#717171\",\"#fff\"];else a=[\"#fff\",\"#909090\",\"#151515\"];", "if(isD)var a=[\"#fff\",\"#555\",\"#25D366\"];else a=[\"#fff\",\"#ccc\",\"#25D366\"];")
            .replace("#ssprodivI div span{\ndisplay:block;\nheight:23px;\nwidth:40px;\nborder-radius:40px;\nright:10px;\nposition:absolute;\nbackground:#151515;\n}", "#ssprodivI div span{\ndisplay:inline-flex;\nheight:26px;\nwidth:46px;\nborder-radius:26px;\nright:auto;\nposition:relative;\nbackground:#ccc;\ntransition:background 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);\ncursor:pointer;\nflex-shrink:0;\nmargin-right:8px;\n}")
            .replace("#ssprodivI div span b{\ndisplay:block;\nheight:19px;\nwidth:19px;\nposition:absolute;\nright:2px;\ntop:2px;\nborder-radius:50px;\nbackground:#fff;\n}", "#ssprodivI div span b{\nheight:22px;\nwidth:22px;\nbackground:#fff;\nborder-radius:50%;\nposition:absolute;\nright:2px;\ntop:2px;\ntransition:transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1), background 0.3s;\nbox-shadow:0 1px 3px rgba(0,0,0,0.3);\n}")
            .replace("#ssprodivI div{\nheight:10px;\nwidth:calc(100% - 20px);\npadding:10px;\nfont-size:1.45rem;\ntext-align:left;\ndisplay:flex;\nalign-items:center;\nposition:relative;\nmargin-top:3px;\n}", "#ssprodivI div{\nmin-height:20px;\nwidth:calc(100% - 20px);\npadding:10px;\nfont-size:1.45rem;\ntext-align:left;\ndisplay:flex;\njustify-content:space-between;\nalign-items:center;\nposition:relative;\nmargin-top:3px;\ngap:15px;\n}")
            .replace("left:2px;", "transform:translateX(0px);")
            .replace("e.children[0].style.left=\"2px\"", "e.children[0].style.transform=\"translateX(0px)\"")
            .replace("e.children[0].style.left=\"auto\",e.children[0].style.right=\"2px\"", "e.children[0].style.transform=\"translateX(-20px)\"")
            .replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};transform:translateX(-20px);`:`background:${a[2]};`")
            .replace("by Prateek Chaubey", "عبدالرحمن المخلافي 🔥")
            .replace("You can find the source code at <a href=\"https://www.youtube.com/redirect?q=https://github.com/prateek-chaubey/YTPRO\" style=\"font-family:monospace;\" > https://github.com/prateek-chaubey/YTPRO</a>", "");

        System.out.println("Contains reversed RTL translation: " + jsCode.contains("translateX(-20px)"));
        System.out.println("Contains user name: " + jsCode.contains("عبدالرحمن المخلافي 🔥"));
        System.out.println("Source code string removed: " + !jsCode.contains("https://github.com/prateek-chaubey/YTPRO"));
        
        Files.write(Paths.get("ytpro_test.js"), jsCode.getBytes(StandardCharsets.UTF_8));
    }
}
