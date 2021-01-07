import java.nio.file.Files;
import java.nio.file.Paths;

public class BucketUtils {

    private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );

    public static String readFileFromResources(String fileName) throws Exception {
        String path = ServerSideEncryptionUsingKMS.class.getResource(fileName).toURI().getPath();
        String osAppropriatePath = IS_WINDOWS ? path.substring(1) : path;
        byte[] encoded = Files.readAllBytes(Paths.get(osAppropriatePath));
        return new String(encoded);
    }

    public void createBucketS3() throws Exception{

    }
}