package com.stuartsullivan.ir.models;

import com.stuartsullivan.ir.utils.SimpleListInt;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by stuart on 1/26/17.
 */
public class PostingList {
    private SimpleListInt[] postings;
    private int length;
    private String path = null;

    public PostingList() {
        this.postings = new SimpleListInt[8];
    }

    public PostingList(String path) {
        this.path = path;
        this.postings = new SimpleListInt[8];
        this.loadPostingList();
    }

    public int getLength() {
        return this.length;
    }

    public SimpleListInt get(int index) {
        return this.postings[index];
    }

    public void put(int index, SimpleListInt list) {
        if(index > this.postings.length) {
            grow(index);
        }
        this.postings[index] = list;
    }

    public void add(int tokenId, int docId, int count) {
        if (this.length == this.postings.length) {
            grow(-1);
        }
        if (tokenId >= this.postings.length) {
            grow((int) Math.pow(2, Math.ceil(Math.log(tokenId + 1)/Math.log(2))));
        }
        if (this.postings[tokenId] == null) {
            this.postings[tokenId] = new SimpleListInt();
        }
        this.postings[tokenId].add(docId);
        this.postings[tokenId].add(count);
        this.length = Math.max(this.length, tokenId);
    }

    private void grow(int len) {
        if (len == -1) len = this.postings.length * 2;
        SimpleListInt[] newList = new SimpleListInt[len];
        for (int i = 0; i < this.postings.length; i++) {
            newList[i] = this.postings[i];
        }
        this.postings = newList;
    }

    // http://stackoverflow.com/questions/1086054/how-to-convert-int-to-byte
    private byte[] convertToByteArray(int index) {
        int[] list = this.postings[index].getValues();
        ByteBuffer byteBuffer = ByteBuffer.allocate(list.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(list);
        return byteBuffer.array();
    }

    public void loadPostingList() {
        //
        try {
            String postingListPath = this.path + "/postings/list.dat";
            String postingIndexPath = this.path + "/postings/index.dat";
            File index = new File(postingIndexPath);
            File list = new File(postingListPath);
            if (!index.exists() || !list.exists()) {
                return;
            }
            DataInputStream inIndex = new DataInputStream(new FileInputStream(index));
            DataInputStream inList = new DataInputStream(new FileInputStream(list));
            this.postings = new SimpleListInt[inIndex.available()/4];
            byte[] indexBytes = new byte[4];
            byte[] listBytes = new byte[0];
            int offset = 0;
            int lastList = 0;
            int id = 0;
            try {
                while((offset = inIndex.read(indexBytes)) != -1) {
                    offset = ByteBuffer.wrap(indexBytes).getInt();
                    listBytes = new byte[(offset - lastList)];
                    inList.read(listBytes);
                    lastList = offset;
                    // http://stackoverflow.com/questions/11437203/byte-array-to-int-array
                    IntBuffer intBuf = ByteBuffer.wrap(listBytes)
                                    .order(ByteOrder.BIG_ENDIAN)
                                    .asIntBuffer();
                    int[] array = new int[intBuf.remaining()];
                    intBuf.get(array);
                    SimpleListInt l = new SimpleListInt(array);
                    this.put(id, l);
                    id++;
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            inIndex.close();
            inList.close();;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePostingList() {
        try {
            String postingListPath = this.path + "/postings/list.dat";
            String postingIndexPath = this.path + "/postings/index.dat";
            File index = new File(postingIndexPath);
            if(!index.exists()) index.getParentFile().mkdir();
            File list = new File(postingListPath);
            if(!list.exists()) list.getParentFile().mkdir();
            DataOutputStream outIndex = new DataOutputStream(new FileOutputStream(index));
            DataOutputStream outList = new DataOutputStream(new FileOutputStream(list));
            int offsets = 0;
            for(int i = 0; i < this.length; i++) {
                byte[] listData = this.convertToByteArray(i);
                offsets += listData.length;
                outIndex.writeInt(offsets);
                outList.write(listData);
            }
            outIndex.close();
            outList.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}