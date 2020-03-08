/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * File: MemorySim.java
 * Author: Josh Stack
 * Purpose: Satisfy the requirements for CMSC 412 Final Project
 * Date: March 4, 2020
 */
public class MemorySim {
    ArrayList<Integer> rfString; 
    int[] rmPages;
    int[] pageCalled;
    boolean[] pageFaults;
    int rsLen;
    int numOfPhyFrames;
    int numOfVirPages;
    
    int [][] phyMemory;
    PhysicalFrame[] virtualFrames;
    String typeAlg;
    
    MemorySim(ArrayList<Integer> refs, int p, int v){
        rfString = refs;
        rsLen = rfString.size();
        rmPages = new int[rsLen];
        pageCalled = new int [rsLen];
        numOfPhyFrames = p;
        numOfVirPages = v;
        phyMemory = new int[rfString.size()][p];
        virtualFrames = new PhysicalFrame[v];
        pageFaults = new boolean[rsLen];
    }
    
    void generate(String alg){
        initialize();
        typeAlg = alg;
        int currentFrame = 0;
        int insertFrame;
        int empty;
        int replaceFrame;
        int[] listOfFrames;
        int inMemory;
        //While loop step through each call of the simulation
        while (currentFrame < rsLen){
            insertFrame = rfString.get(currentFrame);
            if(alg == "LRU"){
                virtualFrames[insertFrame].setLastUse(currentFrame);
            }else if (alg == "LFU"){
                virtualFrames[insertFrame].incrementTimesUsed();
            }
            empty = findIndex(phyMemory[currentFrame], -1);
            //If the page needed is already in physical memory...
            inMemory = findIndex(phyMemory[currentFrame], insertFrame);
            if(inMemory != -1){
                pageCalled[currentFrame] = inMemory;
                //no page fault!
                pageFaults[currentFrame] = false;
            }
            //if page is not in physicalMemory but there is space for it..
            else if (empty >= 0){
                pageCalled[currentFrame] = empty;
                phyMemory[currentFrame][empty] = insertFrame;
                virtualFrames[insertFrame].setInserted(currentFrame);
            }   
            //not in memory and no empty space
            else{
                switch(alg){
                    case "FIFO":
                        //find oldest frame
                        replaceFrame = findOldest(phyMemory[currentFrame]);
                        //update insertion time
                        virtualFrames[insertFrame].setInserted(currentFrame);
                        break;
                        
                    case "OPT":
                        //calculate next uses
                        calculateNextUses(currentFrame);
                        //find the least optimal page
                        replaceFrame = findLeastOptimal(phyMemory[currentFrame]);
                        break;
                        
                    case "LFU":
                        //find least recently used
                        replaceFrame = findLfu(phyMemory[currentFrame]);
                        break;
                        
                    case "LRU": 
                        //find least recently used 
                        replaceFrame = findLru(phyMemory[currentFrame]);
                        //update information for last use of the frame just called
                        break;
                    default: 
                        System.out.println("Error: Algorithm not recognized.");
                        return;
                        
                        
                }
                //record removed frame 
                rmPages[currentFrame] = phyMemory[currentFrame][replaceFrame];
                //record new frame spot
                pageCalled[currentFrame] = replaceFrame;
                
                //put the new frame in that spot 
                phyMemory[currentFrame][replaceFrame] = insertFrame;
             
            }
            
            //make the physical memory for the next call a copy of the physical
            //memory at the end of this call
            if((currentFrame + 1)< rsLen){
                for (int i = 0; i < numOfPhyFrames; i++){
                    phyMemory[currentFrame + 1][i] = phyMemory[currentFrame][i];
                }
            }
            currentFrame += 1;
        }
    }
    
    
    //find the first inserted Frame, given an array of frame numbers
    int findOldest(int[] a){
        int oldest = virtualFrames[a[0]].getInserted();
        int oldestIndex = 0;
        int checking;
        for (int i = 1; i < a.length; i++){
            checking = virtualFrames[a[i]].getInserted();
            if (checking < oldest){
                oldest = checking;
                oldestIndex = i;
            }
        }
        return oldestIndex;
    }
    
    
    //fubd least frequently used frame given an array containing frame numbers
    int findLfu(int[] a){
        int lfuIndex = 0;
        int lfuTimesUsed = virtualFrames[a[lfuIndex]].getTimesUsed();
        for (int i = 1; i < a.length; i++){
            int temp = a[i];
            int tempTimesUsed = virtualFrames[a[i]].getTimesUsed();
            
            if (tempTimesUsed < lfuTimesUsed){
                lfuIndex = i;
                lfuTimesUsed = tempTimesUsed;
            }
        }
        return lfuIndex;
    }
    
    
    //find least recently used frame given an array containing frame numbers
    int findLru(int[] a){
        int lruIndex = 0;
        int lruLastUse = virtualFrames[a[lruIndex]].getLastUse();
        
        for(int i = 1; i < a.length; i++){
            int temp = a[i];
            int tempLastUse = virtualFrames[a[i]].getLastUse();
            
            if (tempLastUse < lruLastUse){
                lruIndex = i;
                lruLastUse = tempLastUse;
            }
        }
        return lruIndex;
    }
    
    
    //find least optimal frame
    int findLeastOptimal(int[] a){
        int leastOptimal = a[0];
        int index = 0;
        int leastOptNextUse = virtualFrames[leastOptimal].getNextUse();
        for (int i = 1; i < a.length; i++){
            int temp = a[i];
            int tempNextUse = virtualFrames[temp].getNextUse();
            if (tempNextUse > leastOptNextUse){
                leastOptimal = temp;
                leastOptNextUse = virtualFrames[leastOptimal].getNextUse();
                index = i;
            }
        }
        //return least optimal index
        return index;
    }
    
    
    void calculateNextUses(int n){
        for (int i = 0; i < numOfVirPages; i++){
            virtualFrames[i].setNextUse(rsLen + 1);
        }
        //then it works backwards from the end
        for (int i = rsLen - 1; i >= n; i--){
            int called = rfString.get(i);
            virtualFrames[called].setNextUse(i);
        }
    }
    
    //initilize all the arrays used in generate()
    void initialize(){
        //set page faults to false
        for (int i = 0; i < pageFaults.length; i++)
            pageFaults[i] = true;
        
        //setremoved to -1s
        for (int i = 0; i < rmPages.length; i++)
            rmPages[i] = -1;
        
        //set pages changed to -1s
        for (int i = 0; i < pageCalled.length; i++)
            pageCalled[i] = -1;
        
        //set clean array of Frames
        for (int i = 0; i < numOfVirPages; i++)
            virtualFrames[i] = new PhysicalFrame(i);
        
        //clean array of slices
        for (int i = 0; i < rsLen; i++)
            for (int j = 0; j < numOfPhyFrames; j++)
                phyMemory[i][j] = -1;
        
        typeAlg = "";
    }
    
    //print the results of the simulation, one step at a time
    void printFrameInfo(){
        System.out.println("Memory information: ");
        System.out.println("Algorithm type: " + typeAlg);
        System.out.println("Length of ref. string: " + rsLen);
        System.out.println("Number of virtual pages: " + numOfVirPages);
        System.out.println("Number of physical pages: " + numOfPhyFrames);
        System.out.println("------");
        System.out.println("Press enter to step through snapshots of physical memory");
        System.out.println("Or, enter \"q\" at anytime to return to main menu.");
        
        Scanner sc = new Scanner(System.in);
        int steppingSlice = 0;
        String prompt;
        int frameNum;
        int removedInt;
        while(steppingSlice < rsLen){
            prompt = sc.nextLine();
            if (prompt.equals("q")){
                System.out.println("Exitting printout...");
                break;
            }
            System.out.println("Snapshot at step " + (steppingSlice + 1) + ":");
            System.out.println("Program called virtual frame # "
                            + rfString.get(steppingSlice));
            for (int i = 0; i < numOfPhyFrames; i++){
                System.out.print("Physical frame " + i + ":");
                frameNum = phyMemory[steppingSlice][i];
                if (frameNum >= 0){
                    if (i == pageCalled[steppingSlice]){
                        System.out.println("[" + frameNum + "]");
                    }else{
                        System.out.println(" " + frameNum);
                    }
                }else{
                    System.out.println("x");
                }
            }
            
            removedInt = rmPages[steppingSlice];
            System.out.println("Page faults: " + (pageFaults[steppingSlice] ? "Yes." : "No."));
            System.out.println("Victim frames: " + (removedInt == -1 ? "None." : removedInt));
            steppingSlice += 1;
        }
        System.out.print("Simulation finsihed. Press enter to continue.");
        sc.nextLine();
    }
    
    
    int findIndex(int[] a, int n){
        for (int i = 0; i < a.length; i++){
            if (a[i] == n){
                return i;
            }
        }
        return -1;
    }
}
