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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerSideEncryptionUsingKMS {

    public static void main(String[] args) throws Exception {
        BasicAWSCredentials creds = new BasicAWSCredentials(AwsParameters.accessKey, AwsParameters.secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(String.valueOf(Region.getRegion(Regions.US_EAST_2))).build();

        String customerMasterKeyId = AwsParameters.customerMasterKeyId;
        String bucket = AwsParameters.bucket;

        String policy = BucketUtils.readFileFromResources(AwsParameters.policyFile).replace("bucketname",bucket);

        System.out.println(policy);
        s3Client.setBucketPolicy(bucket, policy);


        byte[] plaintext = "Hello S3/KMS SSE Encryption!".getBytes(StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(plaintext.length);

        PutObjectRequest req = new PutObjectRequest(bucket, AwsParameters.destFileNAme,
                new ByteArrayInputStream(plaintext), metadata)
                .withSSEAwsKeyManagementParams(
                        new SSEAwsKeyManagementParams(customerMasterKeyId));
        PutObjectResult putResult = s3Client.putObject(req);
        System.out.println(putResult);

        S3Object s3object = s3Client.getObject(bucket, AwsParameters.destFileNAme);
        System.out.println(IOUtils.toString(s3object.getObjectContent()));
        s3Client.shutdown();
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