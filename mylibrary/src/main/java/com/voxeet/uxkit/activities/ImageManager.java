/**----------------------------------------------------------------------------------
* Microsoft Developer & Platform Evangelism
*
* Copyright (c) Microsoft Corporation. All rights reserved.
*
* THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, 
* EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES 
* OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
*----------------------------------------------------------------------------------
* The example companies, organizations, products, domain names,	
* e-mail addresses, logos, people, places, and events depicted
* herein are fictitious.  No association with any real company,
* organization, product, domain name, email address, logo, person,
* places, or events is intended or should be inferred.
*----------------------------------------------------------------------------------
**/

package com.voxeet.uxkit.activities;

import android.util.Log;

import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.voxeet.BuildConfig;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.SecureRandom;
import java.util.LinkedList;

public class ImageManager {
    /*
    **Only use Shared Key authentication for testing purposes!** 
    Your account name and account key, which give full read/write access to the associated Storage account, 
    will be distributed to every person that downloads your app. 
    This is **not** a good practice as you risk having your key compromised by untrusted clients. 
    Please consult following documents to understand and use Shared Access Signatures instead.
    https://docs.microsoft.com/en-us/rest/api/storageservices/delegating-access-with-a-shared-access-signature 
    and https://docs.microsoft.com/en-us/azure/storage/common/storage-dotnet-shared-access-signature-part-1 
    */
//    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
//            + "AccountName=[ACCOUNT_NAME];"
//            + "AccountKey=[ACCOUNT_KEY]";
    public static final String storageConnectionString = "https://samirteststorage.blob.core.windows.net/?sv=2019-10-10&ss=bfqt&srt=sco&sp=rwdlacupx&se=2022-07-15T20:12:42Z&st=2020-07-15T12:12:42Z&spr=https&sig=iHo12URSbPeMY%2BCDNMDnZvRFkodqAwIAO5%2FWM2jX2%2Bc%3D";
  //  https://teledentix.blob.core.windows.net/?sv=2019-12-12&ss=b&srt=c&sp=rwdx&se=2025-09-16T23:54:08Z&st=2020-09-16T15:54:08Z&spr=https&sig=w7hujzJOqZ9aTiEetATM%2BssFjeuFcA8W4sQm82FqYQY%3D

   public static final String bLobUrl="https://teledentix.blob.core.windows.net/"+ BuildConfig.Container;
   public static final String blobToken="?sv=2019-12-12&ss=b&srt=co&sp=rwdlx&se=2025-09-16T13:14:05Z&st=2020-09-17T05:14:05Z&spr=https&sig=2pcSFBzbRXO1PVZTeGW12UMs8LKIRnukiBa4Mt0n%2Fn0%3D";

    StorageCredentialsSharedAccessSignature SAS;
    private static CloudBlobContainer getContainer() throws Exception {
        // Retrieve storage account from connection-string.
        StorageCredentialsSharedAccessSignature SAS = new StorageCredentialsSharedAccessSignature(blobToken);
/*        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference("conf-attach");*/

        CloudBlobContainer container = new CloudBlobContainer(URI.create(bLobUrl),SAS);


        return container;
    }

    public static String UploadImage(String name, InputStream image, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer();

        container.createIfNotExists();

        CloudBlockBlob imageBlob = container.getBlockBlobReference(name);
        imageBlob.upload(image, imageLength);

        return name;

    }

    public static String[] ListImages() throws Exception {
        CloudBlobContainer container = getContainer();

        Iterable<ListBlobItem> blobs = container.listBlobs();

        LinkedList<String> blobNames = new LinkedList<>();
        for(ListBlobItem blob: blobs) {
            blobNames.add(((CloudBlockBlob) blob).getName());
            Log.e("Url", "getimages"+((CloudBlockBlob) blob).getUri());
        }

        return blobNames.toArray(new String[blobNames.size()]);
    }

    public static void GetImage(String name, OutputStream imageStream, long imageLength) throws Exception {
        CloudBlobContainer container = getContainer();

        CloudBlockBlob blob = container.getBlockBlobReference(name);

        if(blob.exists()){
            blob.downloadAttributes();

            imageLength = blob.getProperties().getLength();

            blob.download(imageStream);
        }
    }

    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    static String randomString(int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( validChars.charAt( rnd.nextInt(validChars.length()) ) );
        return sb.toString();
    }

}
