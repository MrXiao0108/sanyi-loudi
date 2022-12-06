package com.dzics.udp.netty.handler;

/**
 * @author ZhangChengJun
 * Date 2021/3/26.
 * @since
 */
public class ByteUtil {
    /**
     * @param buffer
     * @param sset
     * @param eset
     * @return
     */
    public static byte sum(byte[] buffer, int sset, int eset) {
        int sumInt = 0;
        for (int i = sset; i <= eset; i++) {
            sumInt += (buffer[i] & 0xFF);
        }
        byte sumByte = (byte) ((sumInt + 52) % 127);
        return sumByte;
    }

    public static int getInt(byte[] bytes, int index) {
        int i0 = bytes[index + 1] & 0xFF;
        int i1 = (bytes[index + 2] & 0xFF) << 8;
        int i2 = (bytes[index + 3] & 0xFF) << 16;
        int i3 = (bytes[index + 4] & 0xFF) << 24;
        return i0 | i1 | i2 | i3;
    }


    public static byte[] tolh(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
}
