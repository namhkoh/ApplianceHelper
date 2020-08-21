package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

//        File directory = new File (".");
//        try {
//            System.out.println ("Current directory's canonical path: "
//                    + directory.getCanonicalPath());
//            System.out.println ("Current directory's absolute  path: "
//                    + directory.getAbsolutePath());
//        }catch(Exception e) {
//            System.out.println("Exceptione is ="+e.getMessage());
//        }
//
//        try{
//            //Process p = Runtime.getRuntime().exec("cmd /c slot_filling_and_intent_detection_of_SLU/run/atis_with_bert.sh");
//            Process p = Runtime.getRuntime().exec("cmd /c run\\atis_with_bert.sh");
//            InputStream is = p.getInputStream();
//            int i = 0;
//            StringBuffer sb = new StringBuffer();
//            while((i=is.read()) != -1)
//                sb.append((char)i);
//            System.out.println(sb.toString());
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//
//        File tmpDir = new File("slot_filling_and_intent_detection_of_SLU\\run\\atis_with_bert.sh");
//        boolean exists = tmpDir.exists();
//        System.out.println(exists);


//        Scanner in = new Scanner(System.in);
//
//        String question;
//        String filename;
//        String input;
//
//        filename = "appliance-data.txt";
//        //question = "How can I clean the cooktop-seal on my oven?";
//        //question = "What does c-F0 code mean on my oven?";
//        //question = "Is glass an appropriate cookware for the oven?";
//        question = "How do I set the clock on the oven?";
//
//
//
//        //Returns the solution
//        ResponseObject response;
//
//            response = MainManager.getAnswer(question, filename);
//            if (response != null) {
//                response.printResponseSolution();
//            }



//        ResponseObject question_response = null;
//        ResponseObject dialog_reponse;
//        while(true) {
//            System.out.print("Enter instructions: ");
//            question = in.nextLine();
//            if(question.equals("exit")) {
//                break;
//            } else{
//                System.out.println("Inside");
//                response = MainManager.getAnswer(question, filename);
//                if(response.isIs_dialog()){
//                    System.out.println("Dialog");
//                    dialog_reponse = response;
//                    System.out.println(dialog_reponse.isIs_dialog() + " " + dialog_reponse.getDialog_command());
//                }else{
//                    System.out.println("Not a dialog");
//                    question_response = response;
//                    question_response.printSteps();
//                }
//            }
//        }
//
//        //Prints the solution.
//        question_response.printResponseSolution();

        String[] words = {"will", "i", "be", "able", "to", "use", "porcelain", "in", "the", "microwavawe"};
        int[] lens = {10};
        String tokenizer = null;
        boolean cls_token_at_end = false;
        String cls_token = null;
        String sep_token = null;
        int cls_token_segment_id = 0;
        boolean pad_on_left = false;
        int pad_token_segment_id = 0;
        String device = "cpu";
        PrepareInputs.prepare_inputs_for_bert_xlnet(words,lens,tokenizer,cls_token_at_end,cls_token,sep_token,cls_token_segment_id,pad_on_left,pad_token_segment_id,device);


    }


}
