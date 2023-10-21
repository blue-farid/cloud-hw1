package com.bluefarid.bankprocessor.integration.consumer;

import com.bluefarid.bankprocessor.domain.Customer;
import com.bluefarid.bankprocessor.domain.CustomerState;
import com.bluefarid.bankprocessor.repository.CustomerRepository;
import com.bluefarid.bankprocessor.s3.ObjectStorage;
import com.bluefarid.bankprocessor.util.ImageUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMqConsumer {
    private final CustomerRepository repository;
    private final ObjectStorage storage;
    private final JavaMailSender mailSender;
    private final ImageUpload imageUpload;

    private static void convertBytesToFile(byte[] fileBytes, String filePath) throws IOException {
        File file = new File(filePath);

        // Write byte array to the file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileBytes);
        }
    }

    @RabbitListener(queues = "rabbitmq")
    public void receiveMessage(String username) throws IOException {
        log.info("NEW MESSAGE : ".concat(username));
        Optional<Customer> customer = repository.findByUsername(username);
        if (customer.isEmpty())
            throw new IllegalStateException("Customer not found exception!");

        try {
            convertBytesToFile(storage.getFile("bank-processor", customer.get().getImage1()), customer.get().getImage1());
            convertBytesToFile(storage.getFile("bank-processor", customer.get().getImage2()),
                    customer.get().getImage2());
            System.out.println("File converted successfully!");
        } catch (IOException e) {
            System.err.println("Failed to convert bytes to file: " + e.getMessage());
        }

        String firstFaceId = imageUpload.faceDetection(customer.get().getImage1());
        String secondFaceId = imageUpload.faceDetection(customer.get().getImage2());
        if (imageUpload.similarity(firstFaceId, secondFaceId)) {
            customer.get().setState(CustomerState.ACCEPT);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setText("Your request has been accepted!");
            message.setFrom("Bank!");
            message.setTo(customer.get().getEmail());
            mailSender.send(message);
        } else {
            customer.get().setState(CustomerState.REJECT);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setText("Your request has been rejected!");
            message.setFrom("Bank!");
            message.setTo(customer.get().getEmail());
            mailSender.send(message);
        }

        repository.save(customer.get());

    }
}