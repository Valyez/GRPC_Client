package org.example;

import com.example.grpc.ImageRequest;
import com.example.grpc.ImageResponse;
import com.example.grpc.ImageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class Client {
    public static void main(String[] args) throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
                .usePlaintext().build();

        ImageServiceGrpc.ImageServiceBlockingStub stub =
                ImageServiceGrpc.newBlockingStub(channel);

        ImageRequest request = ImageRequest
                .newBuilder().setCamId(0).build();

        Iterator<ImageResponse> responses = stub.play(request);

        JFrame window = new JFrame();
        JLabel screen = new JLabel();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        ImageIcon ic = new ImageIcon(ImageIO.read(new File("src/main/resources/loadImage.png")));
        screen.setIcon(ic);
        window.setContentPane(screen);
        window.pack();
        int i = 99;

        while(responses.hasNext()) {
            i++;
            ImageResponse response = responses.next();
            ic = new ImageIcon(response.getFrames().toByteArray());
            screen.setIcon(ic);
            window.setContentPane(screen);
            window.pack();
            if (i % 100 == 0) {
                System.out.println(response.getDate() + " Температура:" + response.getTemperature() + "°C");
            }
        }
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSED));
        channel.shutdownNow();
    }

}
