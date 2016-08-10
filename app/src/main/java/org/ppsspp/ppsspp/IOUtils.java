package org.ppsspp.ppsspp;

import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IOUtils {
    public static String readFile(String filePath) {
        ByteArrayOutputStream bos = null;
        FileInputStream fis = null;
        File file = new File(filePath);
        try {
            bos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);
            byte[] buff = new byte[2048];
            for (int len = 0; (len = fis.read(buff)) != -1; ) {
                bos.write(buff, 0, len);
            }
            String result = bos.toString();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static void deleteFile(File file) {
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
            }
            file.delete();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static void deleteFileNotApk(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    deleteFileNotApk(files[i]);
                }
            }
        } else {
            String name = file.getName();
            if (!name.endsWith(".apk"))
                file.delete();
        }

    }

    public static void in2outThrowException(InputStream ins, OutputStream os) throws Exception {
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = ins.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
    }

    /**
     * inputstream转string
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static void in2out(InputStream ins, OutputStream os) {
        try {
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = ins.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static long getAvailableMemorySize(String path) {
        try {
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getAvailableExternalMemorySize() {
        if (isSDCardMouted()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static boolean isSDCardMouted() {
        try {
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
