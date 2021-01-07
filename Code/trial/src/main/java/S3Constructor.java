import com.amazonaws.regions.Regions;

public class S3Constructor {
    public static void main(String[] args) throws Exception{

        Credentials credentials= Credentials.getCred();
        String accessKey= credentials.accessKey;
        String secretKey= credentials.secretKey;


        CreateBucket createBucketUploadObject= new CreateBucket();
        createBucketUploadObject.createBucketS3("test-upload-object", Regions.US_EAST_2);
        String bucketNameUploadObject= createBucketUploadObject.getBucketName();

        UploadObject uploadObject= new UploadObject();
        uploadObject.uploadObjectS3(bucketNameUploadObject, "ThisIsTest", "testFileUploadObject", "C:\\Users\\Aneri Patel\\Documents\\rajDemo.txt", "upload-object-policy", Regions.US_EAST_2);

        CreateBucket createBucketSSEwithKMS= new CreateBucket();
        createBucketSSEwithKMS.createBucketS3("test-sse-with-kms", Regions.US_EAST_2);
        String bucketNameSSEwithKMS= createBucketSSEwithKMS.getBucketName();

        ServerSideEncryptionUsingKMS serverSideEncryptionUsingKMS= new ServerSideEncryptionUsingKMS();
        serverSideEncryptionUsingKMS.serverSideEncryptionUsingKMSS3(accessKey, secretKey, bucketNameSSEwithKMS, "testFileKMSSSE","70e14595-237e-496a-8c9c-4463c375c1a7", "sse-using-kms-policy", Regions.US_EAST_2);

        CreateBucket createBucketCSEwithKMS= new CreateBucket();
        createBucketCSEwithKMS.createBucketS3("test-cse-with-kms", Regions.US_EAST_2);
        String bucketNameCSEwithKMS= createBucketCSEwithKMS.getBucketName();

        ClientSideEncryptionUsingKMS clientSideEncryptionUsingKMS= new ClientSideEncryptionUsingKMS();
        clientSideEncryptionUsingKMS.clientSideEncryptionUsingKMSS3(bucketNameCSEwithKMS, "testFileKMSCSE", "70e14595-237e-496a-8c9c-4463c375c1a7", Regions.US_EAST_2);
    }
}
