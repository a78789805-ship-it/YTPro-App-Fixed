import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class TestClient {
    public static void main(String[] args) throws Exception {
        String jsCode = new String(Files.readAllBytes(Paths.get("ytpro_fresh.js")), StandardCharsets.UTF_8);
        
        jsCode = jsCode
            .replace("left:2px;", "transform:translateX(0px);")
            .replace("e.children[0].style.left=\"2px\"", "e.children[0].style.transform=\"translateX(0px)\"")
            .replace("e.children[0].style.left=\"auto\",e.children[0].style.right=\"2px\"", "e.children[0].style.transform=\"translateX(-20px)\"")
            .replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};transform:translateX(-20px);`:`background:${a[2]};`");

        System.out.println("Has translateX(0px): " + jsCode.contains("translateX(0px)"));
        System.out.println("Has translateX(-20px): " + jsCode.contains("translateX(-20px)"));
        System.out.println("Has left=\\\"2px\\\": " + jsCode.contains("left=\"2px\""));
        System.out.println("Has right=\\\"2px\\\": " + jsCode.contains("right=\"2px\""));
    }
}
