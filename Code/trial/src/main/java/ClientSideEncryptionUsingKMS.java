import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ClientSideEncryptionUsingKMS {


    private String bucketName= null;
    private String destFileNAme= null;
    private Regions clientRegion= null;
    private String customerMasterKeyId= null;

    public void clientSideEncryptionUsingKMSS3(String bucketName, String destFileNAme, String customerMasterKeyId, Regions clientRegion) throws IOException {
        this.bucketName = bucketName;
        this.destFileNAme = destFileNAme;
        this.clientRegion = clientRegion;
        this.customerMasterKeyId = customerMasterKeyId;
        int readChunkSize = 4096;

        try {
            KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(customerMasterKeyId);

            //Exception in thread "main" java.lang.IllegalArgumentException: hostname cannot be null
            //Values of a enum Regions should be converted to Strings with its method getName(). Otherwise enum's method name() or toString() returns a value as a text (with capital letters and underscores)
            //https://stackoverflow.com/questions/50720197/java-lang-illegalargumentexception-hostname-cannot-be-null-when-trying-to-obtai

            CryptoConfiguration cryptoConfig = new CryptoConfiguration()
                    .withAwsKmsRegion(RegionUtils.getRegion(clientRegion.getName()));
            AmazonS3Encryption encryptionClient = AmazonS3EncryptionClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withEncryptionMaterials(materialProvider)
                    .withCryptoConfiguration(cryptoConfig)
                    .withRegion(clientRegion).build();

            // Upload an object using the encryption client.
            String origContent = "S3 Encrypted Object Using KMS-Managed Customer Master Key.";
            int origContentLength = origContent.length();
            encryptionClient.putObject(bucketName, destFileNAme, origContent);

            S3Object downloadedObject = encryptionClient.getObject(bucketName, destFileNAme);
            S3ObjectInputStream input = downloadedObject.getObjectContent();

            // Decrypt and read the object and close the input stream.
            byte[] readBuffer = new byte[readChunkSize];
            ByteArrayOutputStream baos = new ByteArrayOutputStream(readChunkSize);
            int bytesRead = 0;
            int decryptedContentLength = 0;

            while ((bytesRead = input.read(readBuffer)) != -1) {
                baos.write(readBuffer, 0, bytesRead);
                decryptedContentLength += bytesRead;
            }
            input.close();

            // Verify that the original and decrypted contents are the same size.
            System.out.println("Original content length: " + origContentLength);
            System.out.println("Decrypted content length: " + decryptedContentLength);
            encryptionClient.shutdown();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }
}
