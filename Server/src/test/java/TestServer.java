import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestServer {
    static final int expectedPort = 8081;

    @Test
    public void correctParsePort() {
        int portFromParse = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\dzaga\\IdeaProjects\\Chat\\Server\\src\\main\\resources\\settings.txt"))) {
            String str = br.readLine();
            String[] split = str.split("=");
            portFromParse = Integer.parseInt(split[1].trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(expectedPort, portFromParse);
    }
}
