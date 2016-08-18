package org.ppsspp.ppsspp.util;

import java.io.ByteArrayOutputStream;

/**
 * Created by jalen on 2016/8/18.
 */
public class SecurityUtils {
    public static  final String SECRET_KEY = "kyx@#pwd";
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    private final static String TAG = "SecurityUtils";
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] decrypt(byte[] cSrc) {
        int i, h, l, m, n;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (i = 0; i < cSrc.length; i = i + 2) {
            h = (cSrc[i] - 'x');
            l = (cSrc[i + 1] - 'z');
            m = (h << 4);
            n = (l & 0xf);
            out.write(m + n);
        }
        return out.toByteArray();
    }
}
