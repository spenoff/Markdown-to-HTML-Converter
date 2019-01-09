/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package markdownhtmlconverter;

/**
 *
 * @author spencernisonoff
 */
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.*;
import java.io.File;


public class MarkdownHtmlConverter {
    public static final String TEST_DIR = "./test/";
    public static final String OUT_DIR  = "./out/";
    
    public static String translateEmphasis(String str) {
        return "<em>" + str + "</em>";
    }
    public static String translateStrongEmphasis(String str){
        return "<strong>" + str + "</strong>";
    }
    public static String translateHyperlink(String link, String url){
        return "<a href=\"" + url + "\">" + link + "</a>";
    }
    public static String translateImage(String altText, String imgPath, String imgTitle){
        return "<img src=\"" + imgPath + "\" alt=\"" + altText + "\" title=\"" + imgTitle + "\">";
    }
    public static String translateCode(String text){
        return "<code>" + text + "</code>";
    }
    public static String translateListItem(String text){
        return "<li>" + text + "</li>";
    }
    public static boolean onlyWhiteSpace(String s){
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) != '\u0020'){
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        boolean fileFound = false;
        String inputName = "";
        while(!fileFound) {
           System.out.print("Enter the name of a plain input text file: ");
           inputName = in.next();
           File file = new File(TEST_DIR + inputName);
           if (file.exists()) {
               fileFound = true;
           } else {
               fileFound = false;
               System.out.println("File not found. Try again.");
           }
        }
        System.out.print("Enter the name of a plain output text file: ");
        String outputName = in.next();
        Scanner input;
        FileWriter output;
        try {
            File f = new File(TEST_DIR + inputName);
            File f2 = new File(OUT_DIR + outputName);


            input = new Scanner(f);
            output = new FileWriter(f2);
            output.write("<!DOCTYPE html>\n<html>\n<head>\n<title>Results of Markdown Translation</title>\n</head>\n<body>\n");
            boolean list = false;
            int numBlanks = 0;
            output.write("<p>\n");

            PrintLoop(input, list, numBlanks, output);
            output.close();
        }
        catch(Exception e){
            //e.printStackTrace();
            System.out.println("File not found.");
        }

    }


    private static void PrintLoop(Scanner input, boolean list, int numBlanks, FileWriter output) throws Exception{
        while(input.hasNextLine()){
            String s = input.nextLine();

            if(s.length() > 0 && s.charAt(0) == '+'){
                //Bulleted List
                String s2;
                if(!list){
                    output.write("<ul>\n");
                    list = true;
                }
                s2 = s.substring(1);
                s = translateListItem(s2);
            }
            else if(list){ //end list
                output.write("</ul>");
                list = false;
            }
            String t = ""; //temporary string that s will eventually be set equal to
            for(int i = 0; i < s.length(); i++){
                if(s.charAt(i) == '!'){
                    i++;
                    if(s.charAt(i)=='['){ //at this point I would assume there is an image

                        String altText = "";
                        String imgPath = "";
                        String titleText = "";
                        for(int j = ++i; s.charAt(j) != ']'; j++){
                            altText += s.charAt(j);
                            i++;
                        }
                        i += 2;
                        if (s.indexOf(" \"") >= i) {
                            while (i != s.indexOf(" \"")) {
                                imgPath += "" + s.charAt(i);
                                i++;
                            }
                            i++;
                        } else {
                            while (i != s.indexOf("\"")) {
                                imgPath += "" + s.charAt(i);
                                i++;
                            }
                        }
                        i++;
                        for(int j = i; j < s.indexOf("\"", s.indexOf("\"") + 1); j++){
                            titleText += s.charAt(j);
                            i++;
                        }
                        i++; //skips the closing parenthesis
                        t += translateImage(altText, imgPath, titleText);
                        continue;
                    }
                }
                if(s.charAt(i) == '[') { //at this point I would assume that there is a hyperlink
                    String linkText = "";
                    String uRL = "";
                    for (int j = ++i; s.charAt(j) != ']'; j++) {
                        linkText += s.charAt(j);
                        i++;
                    }
                    i += 1;
                    for (int j = ++i; s.charAt(j) != ')'; j++) {
                        uRL += s.charAt(j);
                        i++;
                    }
                    i++; //skips the closing parenthesis
                    t += translateHyperlink(linkText, uRL);
                    continue;
                }
                t += s.charAt(i);
            }
            s = t;

            if (s.contains("**")) {
                //Strong Emphasis
                int i = 0;
                String t1 = "";
                String t2 = "";
                while (!s.substring(i, i + 1).equals("*")) {
                    t1 += s.substring(i, ++i);
                }
                int j = i + 2;
                while (!s.substring(j, j + 1).equals("*")) {
                    t2 += s.substring(j, ++j);
                }
                j+=2;
                t1 += translateStrongEmphasis(t2);

                while(j < s.length()){
                    t1 += "" + s.charAt(j);
                    j++;
                }
                s = t1;

            }  if (s.contains("*")) {
                //Emphasis
                int i = 0;
                String t1 = "";
                String t2 = "";
                while (!s.substring(i, i + 1).equals("*")) {
                    t1 += s.substring(i, ++i);
                }
                int j = i + 1;
                while (!s.substring(j, j + 1).equals("*")) {
                    t2 += s.substring(j, ++j);
                }
                j++;
                t1 += translateEmphasis(t2);
                while(j < s.length()){
                    t1 += "" + s.charAt(j);
                    j++;
                }

                s = t1;

            }  if (s.contains("`") && s.indexOf("`", s.indexOf("`")+1) != -1){
                //code
                int i = 0;
                String t1 = "";
                String t2 = "";
                while (!s.substring(i, i + 1).equals("`")) {
                    t1 += s.substring(i, ++i);
                }
                int j = i + 1;
                while (!s.substring(j, j + 1).equals("`")) {
                    t2 += s.substring(j, ++j);
                }
                t1 += translateCode(t2);
                j++;
                while(j < s.length()){
                    t1 += "" + s.charAt(j);
                    j++;
                }
                s = t1;
            }
            if (s.equals("") || onlyWhiteSpace(s) || s == null) {
                output.write("</p>\n\n<p>\n");
            }
            output.write(s+"\n");
        }

        if(list){
            output.write("</ul>\n");
        }
        output.write("</p>\n</body>\n</html>");

    }

}
