/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;


/**
 * File: DPSimulator.java
 * Author: Josh Stack
 * Purpose: Satisfy the requirements for CMSC 412 Final Project
 * Date: March 4, 2020
 */
public class DPSimulator {
    
    //max number of virtual pages
    static final int MAX_VP = 10;
    //max number of physical pages
    static final int MAX_PP = 7;
    
    public static void main(String[] args){
        //read in physical frame numbers
        int numOfPhyFrames = readCmdLineArg(args);
        System.out.println("Number of page frames: " + numOfPhyFrames);
        
        //set up for main looooop
        Scanner stdIn = new Scanner(System.in);
        String line; //input from user
        ArrayList<Integer> refString = null;
        MemorySim simulator;
        
        //begin the main loop
        while(true){
            System.out.println();
            System.out.println("Please choose from the following options:");
            System.out.println("0 - Exit");
            System.out.println("1 - Read reference string");
            System.out.println("2 - Generate reference String");
            System.out.println("3 - Display current reference string");
            System.out.println("4 - Simulate FIFO");
            System.out.println("5 - Simulate OPT");
            System.out.println("6 - Simulate LRU");
            System.out.println("7 - Simulate LFU");
            System.out.println();
            
            //read input 
            line = stdIn.next();
            stdIn.nextLine();
            switch (line){
                
                //exit
                case "0":
                    System.out.println("Closing program");
                    System.exit(0);
                    break;
                    
                //read reference string    
                case "1":
                    refString = readRefString(stdIn);
                    //confirm
                    stringConfirm(refString);
                    break;
                    
                //generate reference string
                case "2":
                    System.out.println("How long do you want the reference string to be?");
                    int stringSize = getStringSize(stdIn);
                    //generate the string
                    refString = generateString(stringSize, MAX_VP);
                    //confirm
                    stringConfirm(refString);
                    break;
                    
                //display current reference string
                case "3":
                    if(refString != null){
                        System.out.print("Current reference String: ");
                        int i;
                        for (i = 0; i < refString.size() - 1; i++){
                            System.out.print(refString.get(i) + ", ");
                        }
                        System.out.print(refString.get(i));
                        System.out.print(".");
                    }else{
                        System.out.println("Error: no reference string entered");
                    }
                    break;
                    
                //Simulate FIFO
                case "4":
                    //check ref string has been set
                    if(rsIsSet(refString)){
                       //create simulation conditions, run it, print it
                       simulator = new MemorySim (refString, numOfPhyFrames,MAX_VP);
                       simulator.generate("FIFO");
                       simulator.printFrameInfo();
                    }
                    break;
                    
                //Simulate OPT
                case "5":
                    if(rsIsSet(refString)){
                        //create simulation conditions, run it, print it
                        simulator = new MemorySim(refString, numOfPhyFrames,MAX_VP);
                        simulator.generate("OPT");
                        simulator.printFrameInfo();
                    }
                    break;
                    
                //Simulate LRU
                case "6":
                    if (rsIsSet(refString)){
                        //create simulation conditions, run it, print it
                        simulator = new MemorySim(refString, numOfPhyFrames, MAX_VP);
                        simulator.generate("LRU");
                        simulator.printFrameInfo();
                    }
                    break;
                    
                //Simulate LFU
                case "7":
                    if (rsIsSet(refString)){
                        //create simulation conditions, run it, print in
                        simulator = new MemorySim(refString, numOfPhyFrames, MAX_VP);
                        simulator.generate("LFU");
                        simulator.printFrameInfo();
                    }
                    break;
                    
                default: 
                    break;       
                    
            }//end switch
        }//end while(true)
    }//end main
    
    private static int readCmdLineArg(String[] args){
        if(args.length < 1){
            System.out.println("Error: need to pass exactly 1 cmd line argument for num of physical frames");
            System.exit(-1);
        }
        if(args.length > 1){
            System.out.println("Warning: Too many cmd line arguments.");
        }
        //n represents number of physical page frames
        int n = 1;
        
        //try to parse int; catch exceptions
        try{
            n = Integer.parseInt(args[0]);
        } catch(NumberFormatException e){
            System.out.println("Error: Cmd line arg must be an int");
            System.exit(-1);
        }
        
        //check if n is between 0 and N-1
        if(n < 1 || n > MAX_PP){
            System.out.println("Error: Must be between 1 and " + (MAX_PP) + " physyical frames");
            System.exit(-1);
        }
        
        //everything worked out ok, return n
        return n;
    }
    
    static ArrayList<Integer> readRefString(Scanner in){
        System.out.println("Enter a series of numbers: ");
        //create RefString
        ArrayList<Integer> list = new ArrayList<Integer>();
        do{
            //read in a line
            String line = in.nextLine();
            //create a scanner to operate on that line 
            Scanner stdInput = new Scanner(line);
            //extract the inits
            String temp;
            int tempInt = -1;
            boolean isInt;
            while (stdInput.hasNext()){
                temp = stdInput.next();
                isInt = false;
                try{
                    tempInt = Integer.parseInt(temp);
                    isInt = true;
                } catch(NumberFormatException e) { 
                    System.out.println("Warning: You entered a non-int; \"" + temp + "\" ignored");
                }
                //ensure that the numbers entered are between 0-9
                if (isInt &&(tempInt < 0 || tempInt >= MAX_VP)){
                    System.out.println("Warning: Numbers must be between 0 and "+ (MAX_VP -1) + "; \"" + temp + "\" ignored");
                } else if (isInt){
                    list.add(tempInt);
                }
            }
            //ensure at least 1 valid int is entered
            if (list.size() < 1){
                System.out.println("Error: Must be atleast one int (0-9). Try again");
            }
        
        } while (list.size() < 1);
        return list;
    }
    
    static int getStringSize(Scanner in){
        //read in a line; parse an int
        int stringSize = 0;
        while(stringSize < 1){
            try{
                stringSize = in.nextInt();
            }
            catch (InputMismatchException e){
                System.out.println("You must enter an integer.");
            }
            in.nextLine();
            if(stringSize < 1){
                System.out.println("You must enter a positive integer.");
            }
        }
        return stringSize;
    }
    
    static ArrayList<Integer> generateString(int n, int max){
        //Note: max is exclusive
        //validate input
        if(n < 1){
            System.out.println("Error: cannot create a reference string shorter than 1.");
            return null;
        }
        
        Random rand = new Random();
        
        //create ArrayList for ints
        ArrayList<Integer> ar = new ArrayList<Integer>();
        //generate n random numbers and add them to the list
        for (int i = 0; i < n; i++){
            ar.add(rand.nextInt(max));
        }
        
        //use the arrayList to create a refString
        ArrayList<Integer> rs = ar;
        return rs;
    }
    
    static void stringConfirm(ArrayList<Integer> rs){
        if (rs != null){
            System.out.print("Valid ref. string: ");
            int i;
            for (i = 0; i < rs.size() -1; i++){
                System.out.print(rs.get(i) + ", ");
            }
            System.out.print(rs.get(i));
            System.out.print(".");
        } else{
            System.out.println("Invalid reference string. Please try again.");
        }
    }
    
    static boolean rsIsSet(ArrayList<Integer> rs){
        if (rs != null){
            return true;
        }
        System.out.println("Error: reference string not yet entered/generated!");
        return false;
    }
}