package com.bluefarid.bankprocessor.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ImageUpload {

    private static String getElement(String response, String index) {
        String chars = response.split(index)[1];
        StringBuilder token = new StringBuilder();
        for (int i = 3; chars.toCharArray()[i] != '\"'; i++){
            token.append(chars.charAt(i));
        }
        return token.toString();
    }
    private static String getScore(String response, String index) {
        String chars = response.split(index)[1];
        StringBuilder token = new StringBuilder();
        for (int i = 2; chars.toCharArray()[i] != '}'; i++){
            token.append(chars.charAt(i));
        }
        return token.toString();
    }

    public String faceDetection(String filePath) throws IOException {
        String credentialsToEncode = "acc_047e6b10355a6b2" + ":" + "017cf0bccf6475bbf3b630de19fb5775";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        // Change the file path here
        File fileToUpload = new File(filePath);

        String endpoint = "/faces/detections";

        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "Image Upload";

        URL urlObject = new URL("https://api.imagga.com/v2" + endpoint + "?return_face_id=1");
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + fileToUpload.getName() + "\"" + crlf);
        request.writeBytes(crlf);


        InputStream inputStream = new FileInputStream(fileToUpload);
        int bytesRead;
        byte[] dataBuffer = new byte[1024];
        while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
            request.write(dataBuffer, 0, bytesRead);
        }

        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
        request.close();

        InputStream responseStream = new BufferedInputStream(connection.getInputStream());

        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

        String line = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        responseStreamReader.close();

        String response = stringBuilder.toString();
        return getElement(response, "face_id");
    }

    public boolean similarity(String firstFaceId, String secondFaceId) throws IOException {
        String credentialsToEncode = "acc_047e6b10355a6b2" + ":" + "017cf0bccf6475bbf3b630de19fb5775";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String endpoint_url = "https://api.imagga.com/v2/faces/similarity";

        String url = endpoint_url + "?face_id=" + firstFaceId + "&second_face_id=" + secondFaceId;
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", "Basic " + basicAuth);

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String score = getScore(connectionInput.readLine(), "score");
        return Double.parseDouble(score) > 80;
    }
}