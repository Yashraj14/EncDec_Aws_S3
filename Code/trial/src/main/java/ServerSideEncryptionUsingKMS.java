import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerSideEncryptionUsingKMS {

    private String customerMasterKeyId= null;
    private String policyFile= null;
    private String bucketName= null;
    private String destFileNAme= null;


    public void serverSideEncryptionUsingKMSS3(String accessKey, String secretKey, String bucketName, String destFileNAme, String customerMasterKeyId, String policyFile, Regions clientRegion) throws Exception {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(String.valueOf(Region.getRegion(clientRegion))).build();

        this.customerMasterKeyId = customerMasterKeyId;
        this.bucketName = bucketName;
        this.policyFile= policyFile;
        this.destFileNAme= destFileNAme;
        final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
        String path = ServerSideEncryptionUsingKMS.class.getResource(policyFile).toURI().getPath();
        policyPath(bucketName, s3Client, IS_WINDOWS, path);


        byte[] plaintext = "Hello S3/KMS SSE Encryption!".getBytes(StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(plaintext.length);

        PutObjectRequest req = new PutObjectRequest(bucketName, destFileNAme,
                new ByteArrayInputStream(plaintext), metadata)
                .withSSEAwsKeyManagementParams(
                        new SSEAwsKeyManagementParams(customerMasterKeyId));
        PutObjectResult putResult = s3Client.putObject(req);
        System.out.println(putResult);

        S3Object s3object = s3Client.getObject(bucketName, destFileNAme);
        System.out.println(IOUtils.toString(s3object.getObjectContent()));
        s3Client.shutdown();
    }

    static void policyPath(String bucketName, AmazonS3 s3Client, boolean IS_WINDOWS, String path) throws IOException {
        String osAppropriatePath = IS_WINDOWS ? path.substring(1) : path;
        byte[] encoded = Files.readAllBytes(Paths.get(osAppropriatePath));

        String policy = new String(encoded).replace("bucketname",bucketName);

        System.out.println(policy);
        s3Client.setBucketPolicy(bucketName, policy);
    }
}
/*
Working code.

{
    "Version": "2012-10-17",
    "Id": "PutObjPolicy",
    "Statement": [
        {
            "Sid": "DenySSE-S3",
            "Effect": "Deny",
            "Principal": "*",
            "Action": "s3:PutObject",
            "Resource": "arn:aws:s3:::nirali-the-pikachu/*",
            "Condition": {
                "StringEquals": {
                    "s3:x-amz-server-side-encryption": "AES256"
                }
            }
        },
        {
            "Sid": "RequireKMSEncryption",
            "Effect": "Deny",
            "Principal": "*",
            "Action": "s3:PutObject",
            "Resource": "arn:aws:s3:::nirali-the-pikachu/*",
            "Condition": {
                "StringNotLikeIfExists": {
                    "s3:x-amz-server-side-encryption-aws-kms-key-id": "arn:aws:kms:us-east-2:753230342932:key/*"
                }
            }
        }
    ]
}

 */