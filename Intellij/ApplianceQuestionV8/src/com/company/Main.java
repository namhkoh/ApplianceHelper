package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String question;
        String filename;
        ResponseObject response;

//        //Simple Test
//        filename = "video_demo_data.txt";
//        question = "How do I set the clock on my microwave?";
//        question = "How do I melt butter in my microwave?";
//        question = "How do I cook fries in my oven?";

        filename = "appliance-data.txt";
        question = "How can I buy a WireRack for my oven?";


        response = MainManager.getAnswer(question, filename);
        response.printSteps();

//        //User input Test
//        ResponseObject question_response = null;
//        ResponseObject dialog_reponse;
//        try {
//            while (true) {
//                System.out.print("Enter instructions: ");
//                question = in.nextLine();
//                if (question.equals("exit")) {
//                    break;
//                } else {
//                    response = MainManager.getAnswer(question, filename);
//                    if (response.isIs_dialog()) {
//                        dialog_reponse = response;
//                        System.out.println("Dialog: " + dialog_reponse.isIs_dialog());
//                        System.out.println("Dialog Command" + dialog_reponse.getDialog_command());
//                    } else {
//                        question_response = response;
//                        System.out.println("Dialog: " + question_response.isIs_dialog());
//                        question_response.printSteps();
//                    }
//                }
//            }
//        } finally {
//            question_response.printResponseSolution();
//            System.out.println("Exiting program after printing out current question_response.");
//        }


    }
}
