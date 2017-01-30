package com.stuartsullivan.ir.utils;

/**
 * Created by stuart on 1/26/17.
 */
public class SimpleListInt implements Comparable<SimpleListInt> {
    private int[] values;
    private int length = 0;

    public SimpleListInt() {
        this.values = new int[8];
    }

    public SimpleListInt(int len) {
        this.values = new int[len];
    }

    public SimpleListInt(int[] values) {
        this.values = values;
        for(int val: this.values) {
            if(val == 0) break;
            this.length++;
        }
    }

    public SimpleListInt(int[] values, int length) {
        this.values = values;
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public int get(int index) {
        return this.values[index];
    }

    public void put(int index, int value) {
        this.values[index] = value;
    }

    public void add(int value) {
        if(this.length == this.values.length) {
            grow();
        }
        this.values[this.length] = value;
        this.length += 1;
    }

    public int[] getValues() {
        return values;
    }

    private void grow() {
        int[] newList = new int[this.values.length*2];
        for(int i = 0; i < this.values.length; i++) {
            newList[i] = this.values[i];
        }
        this.values = newList;
    }

    // http://stackoverflow.com/questions/18895915/how-to-sort-an-array-of-objects-in-java
    public int compareTo(SimpleListInt simpleListInt) {
        return this.length < simpleListInt.length ? -1 : this.length > simpleListInt.length ? 1 : 0;
    }

    @Override
    public String toString() {
        return this.values.toString();
    }
}
