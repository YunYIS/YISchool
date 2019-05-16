package com.example.yischool.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static final String LOG_TAG = "FileUtils";

    //获取什么类型的文件
    public static final int GET_FILE_TYPE_NEW = 1;//新建一个输出文件
    public static final int GET_FILE_TYPE_EXIST = 2;//寻找已存在文件


    /**
     * 创建并写入文件（或是覆盖文件）
     * @param file
     * @param bytes
     * @return
     */
    public static File createFile(File file, byte[] bytes){

        FileOutputStream fileOS = null;
        try {
            fileOS = new FileOutputStream(file);
            fileOS.write(bytes);
        } catch (IOException e) {
            Log.d(LOG_TAG, "file create/write error");
            e.printStackTrace();
        } finally {
            try {
                fileOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //文件创建成功
        if(file.exists()){
            return file;
        }else{
            return null;
        }
    }

    /**
     * 创建并写入应用程序临时缓存文件(或是覆盖)（属于APP的CacheDir）
     * @param context
     * @param fileName 文件名
     * @param bytes 文件内容
     * @return file 或 null
     */
    public static File createFileInCache(Context context, String fileName, byte[] bytes){

        String status = Environment.getExternalStorageState();
        File retFile = null;
        //外部存储/SD卡存在，则存储在外部存储空间
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            String path = context.getExternalCacheDir().getAbsolutePath();
            retFile = createFile(new File(path, fileName), bytes);

        } else {//没有外部存储，则使用内部存储
            String path = context.getCacheDir().getAbsolutePath();
            retFile = createFile(new File(path, fileName),bytes);
        }
        return retFile;
    }

    /**
     *  获取app临时缓存文件
     * @param context
     * @param fileName
     * @param type 文件用途
     * @return
     */
    public static File getFileInCache(Context context, String fileName, int type){

        String status = Environment.getExternalStorageState();
        //外部存储/SD卡存在，则存储在外部存储空间
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            String path = context.getExternalCacheDir().getAbsolutePath();
            File file = new File(path, fileName);
            if(type == GET_FILE_TYPE_NEW){
                return file;
            }else if(type == GET_FILE_TYPE_EXIST){
                if(file.exists() && file.isFile()){
                    return file;
                }else {
                    return null;
                }
            }
        } else {//没有外部存储，则使用内部存储
            String path = context.getCacheDir().getAbsolutePath();
            File file = new File(path, fileName);
            if(type == GET_FILE_TYPE_NEW){
                return file;
            }else if(type == GET_FILE_TYPE_EXIST){
                if(file.exists() && file.isFile()){
                    return file;
                }else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 获取文件，如果不存在或不是一个File，则返回null
     * @param path
     * @return
     */
    public static File getFile(String path){

        File file = new File(path);
        if(file.exists() && file.isFile()){
            return file;
        }else{
            return null;
        }
    }


}
