package com.ristory;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA256 手机号批量加密
 * @author Ristory
 *
 */
public class SHA256 {

    public static String fileMD5_LAST = "";
    public static String fileMD5_NOW = "";
    public static String inPath = "/home/deployer/updo-master/freedom/upload/in.txt";
    public static String outPath = "/home/deployer/updo-master/freedom/upload/out.txt";
//    public static String inPath = "/Users/ristory/Desktop/sha256/in.txt";
//    public static String outPath = "/Users/ristory/Desktop/sha256/out.txt";



    /**
     * 利用java原生的摘要实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(encodeStr);
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
//1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }


    public static String BufferedReaderDemo(String path) throws IOException{
        File file=new File(path);
        File file2=new File("/Users/ristory/Desktop/sha256/out.txt");

        if(!file.exists()||file.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br=new BufferedReader(new FileReader(file));
        //BufferedWriter bw=new BufferedWriter();
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();
        while(temp!=null){
            sb.append(temp+" ");
            temp=br.readLine();
            getSHA256StrJava(temp);
        }
        return sb.toString();
    }



    public static void Write()
    {
        try
        {
            //使用BufferedReader和BufferedWriter进行文件复制（操作的是字符,以行为单位读入字符）



            FileReader fr=new FileReader(inPath);
            BufferedReader br=new BufferedReader(fr);
            FileWriter fw=new FileWriter(outPath);
            BufferedWriter bw=new BufferedWriter(fw);

            String s=br.readLine();
            while(null!=s)
            {
                bw.write(getSHA256StrJava(s));
                //由于BufferedReader的rendLIne()是不读入换行符的，所以写入换行时须用newLine()方法
                bw.newLine();
                //read=fis.read(b);
                s=br.readLine();
            }

            br.close();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }



    public static void main(String args[]){
        do {
            try{
                sleep(10*1000);
                FileInputStream fis = new FileInputStream(inPath);
                fileMD5_NOW = DigestUtils.md5Hex(fis);
                fis.close();

                File file = new File(outPath);
                if(file.exists()&&fileMD5_NOW.equals(fileMD5_LAST)){
                    continue;
                }else {
                    fileMD5_LAST = fileMD5_NOW;
                    Write();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }while(true);

        //BufferedReaderDemo("/Users/ristory/Desktop/sha256/in.txt");
    }

    public static void sleep(int millis){
        try{
            Thread.sleep(millis);
        }catch(InterruptedException e){
            e.printStackTrace();
        }


    }


}