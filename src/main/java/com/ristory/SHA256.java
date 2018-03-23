package com.ristory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA256 手机号批量加密
 * @author Ristory
 *
 */
public class SHA256 {
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
            FileReader fr=new FileReader("/home/deployer/updo-master/freedom/upload/in.txt");
            BufferedReader br=new BufferedReader(fr);
            FileWriter fw=new FileWriter("/home/deployer/updo-master/freedom/upload/out.txt");
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



    public static void main(String args[]) throws InterruptedException {
        do {
            Write();
            Thread.sleep(5000);
        }while(true);

        //BufferedReaderDemo("/Users/ristory/Desktop/sha256/in.txt");
    }

}