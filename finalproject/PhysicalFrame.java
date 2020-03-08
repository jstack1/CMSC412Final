/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;


/**
 * File: PhysicalFrame.java
 * Author: Josh Stack
 * Purpose: Satisfy the requirements for CMSC 412 Final Project
 * Date: March 4, 2020
 */
public class PhysicalFrame {

    int id;
    int inserted;
    int next;
    int last;
    int timesUsed; 
    
    PhysicalFrame(int n){
        id = n;
        inserted = -1;
        next = -1;
        last = -1;
        timesUsed = 0;
    }
    
    void setNum(int n){
        id = n;
    }
    int getNum(){
        return id;
    }
    void setInserted(int n){
        inserted = n;
    }
    int getInserted(){
        return inserted;
    }
    void setNextUse(int n){
        next = n;
    }
    int getNextUse(){
        return next;
    }
    void setLastUse(int n){
        last = n;
    }
    int getLastUse(){
        return last;
    }
    void incrementTimesUsed(){
        timesUsed += 1;
    }
    int getTimesUsed(){
        return timesUsed;
    }
}

