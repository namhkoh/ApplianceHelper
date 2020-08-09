package com.unity3d.player;

import android.app.Application;

import com.company.MainManager;
import com.company.ResponseObject;

import java.util.Scanner;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        testerUnity();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public App() {
        super();
    }

    private void testerUnity(){
        Scanner in = new Scanner(System.in);
        String question;
        String filename;
        ResponseObject response;
        filename = "video_demo_data.txt";

//        //Simple Test
//        question = "How do I set the clock on my microwave?";
//        question = "How do I melt butter in my microwave?";
//        question = "How do I cook fries in my oven?";
//        response = MainManager.getAnswer(question, filename);
//        response.printSteps();

        //User input Test
        ResponseObject question_response = null;
        ResponseObject dialog_reponse;
        try {
            while (true) {
                System.out.print("Enter instructions: ");
                question = in.nextLine();
                if (question.equals("exit")) {
                    break;
                } else {
                    response = MainManager.getAnswer(question, filename);
                    if (response.isIs_dialog()) {
                        dialog_reponse = response;
                        System.out.println("Dialog: " + dialog_reponse.isIs_dialog());
                        System.out.println("Dialog Command" + dialog_reponse.getDialog_command());
                    } else {
                        question_response = response;
                        System.out.println("Dialog: " + question_response.isIs_dialog());
                        question_response.printSteps();
                    }
                }
            }
        } finally {
            question_response.printResponseSolution();
            System.out.println("Exiting program after printing out current question_response.");
        }
    }
}
