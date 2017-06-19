package com.overseateacher.crawler.S3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lu on 2017/1/3.
 */
public class S3Client {
    private static String accessKey = "AKIAIMLBVTSSJZF6HC4A";
    private static String secretKey = " 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";
    private static AmazonS3 s3client;
    private static AWSCredentials credentials;
    private static final String SUFFIX = "/";
    public S3Client(){
        credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3client = new AmazonS3Client(credentials);
    }

    public void createFolder(String folderName) {
        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(getBucket(),
                folderName + SUFFIX, emptyContent, metadata);
        // send request to S3 to create folder
        s3client.putObject(putObjectRequest);
    }

    public void uploadFile(String folder, String filename, File file){
        String fileName = folder + SUFFIX + filename;
        PutObjectResult result = s3client.putObject(new PutObjectRequest(getBucket(), fileName,file));
        System.out.println(result.toString());
    }

    public List<String> getObjectslistFromFolder(String bucketName, String folderKey) throws IOException {
        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(bucketName)
                        .withPrefix(folderKey + "/");
        List<String> keys = new ArrayList<>();
        ObjectListing objects = s3client.listObjects(listObjectsRequest);
        for (;;) {
            List<S3ObjectSummary> summaries = objects.getObjectSummaries();
            if (summaries.size() < 1) {
                break;
            }
            summaries.forEach(s -> keys.add(s.getKey()));
            objects = s3client.listNextBatchOfObjects(objects);
        }
        for(String key: keys){
            S3Object object = s3client.getObject(
                    new GetObjectRequest(bucketName, key));
            InputStream objectData = object.getObjectContent();

            objectData.close();
        }
        return keys;
    }

    public String getBucket(){
        List<Bucket> buckets = s3client.listBuckets();

        return "new-esl-crawler";
    }
}

