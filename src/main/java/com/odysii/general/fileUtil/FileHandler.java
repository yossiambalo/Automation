package com.odysii.general.fileUtil;

import java.io.*;

public class FileHandler {

    public static void deleteFile(String deleteFile){
        try{

            File file = new File(deleteFile);

            if (file.exists()){
                if(file.delete()){
                    System.out.println(file.getName() + " is deleted!");
                }else{
                    System.out.println("Delete operation is failed.");
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void copyFile(String source, String destination){

        InputStream inStream = null;
        OutputStream outStream = null;

        try{

            File from = new File(source);
            File to = new File(destination);

            if (!to.exists()) {
                inStream = new FileInputStream(from);
                outStream = new FileOutputStream(to);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }

                inStream.close();
                outStream.close();

                System.out.println("File is copied successful!");
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static boolean compareFiles(String file1, String file2){
        BufferedReader reader1 = null;
        BufferedReader reader2 = null;
        String line1 = "" , line2 = "";
        boolean areEqual = true;
       try {
           reader1 = new BufferedReader(new FileReader(file1));
           reader2 = new BufferedReader(new FileReader(file2));
           line1 = reader1.readLine();
           line2 = reader2.readLine();

        int lineNum = 1;

        while (line1 != null || line2 != null)
        {
            if(line1 == null || line2 == null)
            {
                areEqual = false;

                break;
            }
            else if(!line1.equalsIgnoreCase(line2))
            {
                areEqual = false;

                break;
            }

            line1 = reader1.readLine();

            line2 = reader2.readLine();

            lineNum++;
        }
       }catch (IOException e){
           e.fillInStackTrace();
       }

        if(areEqual)
        {
            return true;
        }
        else
        {
           return false;
        }

    }

}
