package com.ndsucsci;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/9/15.
 */
public class DataMessage {

    byte[] dataSize;
    int dataLengthReceived;
    ArrayList<Byte> allData;

    public DataMessage() {
        dataSize = new byte[4];
        dataLengthReceived = 0;
        allData = new ArrayList<>();
    }

    public void receivedBytes(byte[] bytes, int dataLength) {

        int processedBytes = 0;

        while(processedBytes < dataLength && dataLengthReceived < 4) {
            dataSize[dataLengthReceived] = bytes[processedBytes];
            dataLengthReceived ++;
            processedBytes ++;
        }

        while(processedBytes < dataLength) {
            allData.add(bytes[processedBytes]);
            processedBytes ++;
        }

    }

    public ArrayList<Byte> getData() {
        if(!receivedRequest()) {
            return null;
        }
        return allData;
    }

    public byte[] getDataBytes() {
        if(!receivedRequest()) {
            return null;
        }

        byte[] data = new byte[getData().size()];
        for(int i=0;i<data.length;i++) {
            data[i] = getData().get(i);
        }
        return data;
    }

    public boolean receivedRequest() {
        if(dataLengthReceived < 4) {
            return false;
        }

        int length = java.nio.ByteBuffer.wrap(dataSize).getInt();
        int dataReceived = allData.size() - 4;
        System.out.println("Received " + dataReceived + "/" + length);

        return length == dataReceived;
    }

    public static byte[] createMessage(byte[] data) {
        byte[] message = new byte[data.length + 4];

        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(data.length).array();

        for(int i=0;i<4;i++) {
            message[i] = lengthBytes[i];
        }

        for(int i=lengthBytes.length;i<4 + data.length;i++) {
            message[i] = data[i - 4];
        }

        return message;
    }

    public static ArrayList<byte[]> getBlocks(byte[] data) {
        ArrayList<byte[]> blocks = new ArrayList<>();

        int processingData = 0;

        while(processingData < data.length) {

            // Get block length
            byte[] blockLength = new byte[4];
            blockLength[0] = data[processingData];
            processingData ++;
            blockLength[1] = data[processingData];
            processingData ++;
            blockLength[2] = data[processingData];
            processingData ++;
            blockLength[3] = data[processingData];
            processingData ++;

            int blockSize = java.nio.ByteBuffer.wrap(blockLength).getInt();

            byte[] block = new byte[blockSize];
            for(int i=0;i<block.length;i++) {
                block[i] = data[processingData];
                processingData ++;
            }
            blocks.add(block);
        }

        return blocks;
    }

    public static byte[] createBlocks(ArrayList<byte[]> blocks) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        for(int i=0;i<blocks.size();i++) {

            byte[] blockLength = ByteBuffer.allocate(4).putInt(blocks.get(i).length).array();
            outputStream.write(blockLength);
            outputStream.write(blocks.get(i));
        }

        return outputStream.toByteArray();
    }

}
