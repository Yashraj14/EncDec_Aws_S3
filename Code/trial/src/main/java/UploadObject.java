import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UploadObject {
    private String bucketName= null;
    private String stringObjKeyName= null;
    private String fileObjKeyName= null;
    private String fileName= null;
    private String policyFile= null;
    private Regions clientRegion= null;
    public void uploadObjectS3(String bucketName, String stringObjKeyName, String fileObjKeyName, String fileName, String policyFile, Regions clientRegion) throws URISyntaxException, IOException {
        //Regions clientRegion = Regions.DEFAULT_REGION;
        // Here I'm getting US_WEST_2 as DEFAULT_REGION. But our bucket is in US_EAST_2 region.
        // Need to solve following error.
        // com.amazonaws.services.s3.model.AmazonS3Exception: The bucket is in this region: us-east-2. Please use this region to retry the request (Service: Amazon S3; Status Code: 301; Error Code: PermanentRedirect; Request ID: 2B48109E7F50BBF2; S3 Extended Request ID: hvcUdoebg/VrEscW5+cj3XNghjlFMngMGlm3Z81nzlvNV3a9G4id4/Jg6/MRhdz6ECFMfoAzXMA=), S3 Extended Request ID: hvcUdoebg/VrEscW5+cj3XNghjlFMngMGlm3Z81nzlvNV3a9G4id4/Jg6/MRhdz6ECFMfoAzXMA=
        this.clientRegion = clientRegion;
        this.bucketName = bucketName;
        this.stringObjKeyName = stringObjKeyName;
        this.fileObjKeyName = fileObjKeyName;
        this.fileName = fileName;
        this.policyFile= policyFile;

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();

            final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
            String path = UploadObject.class.getResource(policyFile).toURI().getPath();
            policyPath(bucketName, s3Client, IS_WINDOWS, path);
            
            // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
            request.setMetadata(metadata);
            s3Client.putObject(request);
            s3Client.shutdown();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }
    static void policyPath(String bucketName, AmazonS3 s3Client, boolean IS_WINDOWS, String path) throws IOException {
        String osAppropriatePath = IS_WINDOWS ? path.substring(1) : path;
        byte[] encoded = Files.readAllBytes(Paths.get(osAppropriatePath));

        String policy = new String(encoded).replace("bucketname",bucketName);

        System.out.println(policy);
        s3Client.setBucketPolicy(bucketName, policy);
    }
}
