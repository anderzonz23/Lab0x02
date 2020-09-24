package com.company;
import java.lang.management.*;
import java.util.*;
import java.nio.charset.*;

public class Main {

    public static int N = 1;
    public static int k = 6;
    public static int d = 3;
    public static int minV = 1;
    public static int maxV = 255;
    public static boolean Sorted;
    public static long timeBefore,timeAfter,timeTotal;

    public static void main(String[] args) {
        String[] List = GenerateTestList(N, k, minV, maxV);
        //PrintList(List,N);
        Sorted = IsSorted(List, N, k);


        timeBefore = getCpuTime();
        //BubbleSort(List,N,k,minV,maxV);
        //System.out.println("Sorting with Merge Sort");
        //MergeSort(List,0,List.length-1);
        //System.out.println("Sorting with Quick Sort");
        //QuickSort(List, 0, List.length-1);
        List = RadixSort(List,N,k,d);
        timeAfter = getCpuTime();
        timeTotal = timeAfter - timeBefore;





        Sorted = IsSorted(List, N, k);
        //PrintList(List,N);
        System.out.println("Sorting time: " + timeTotal);
    }

    public static String[] GenerateTestList(int N, int k, int minV, int maxV){

        System.out.println("Generating List of length " + N + ", key width of " + k);

        String[] List = new String[N];

        for(int i = 0; i < N; i++){

            List[i] = GenerateString(k,minV,maxV);
            //System.out.println(List[i]);
        }

        return List;
    }

    public static String GenerateString(int k, int minV, int maxV){

        // From: https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/ //

        byte[] array = new byte[256];
        new Random().nextBytes(array);
        String RandomString = new String(array, StandardCharsets.UTF_8);
        StringBuilder r = new StringBuilder();

        for(int i = 0; i < RandomString.length(); i++){

            char ch = RandomString.charAt(i);

            if((ch >= minV) && (ch <= maxV) && (k>0)){

                r.append(ch);
                k--;
            }
        }
        r.append('\0');


        return r.toString();
    }

    public static boolean IsSorted(String[] List, int N, int k){

        for (int i=1; i < N; i++) {
            if(List[i].compareTo(List[i-1]) <= 0) {
                System.out.println("Verifying... NOT Sorted!");
                return false;
            }
        }

        System.out.println("Verifying... Sorted!");

        return true;
    }

    public static String[] RadixSort(String[] List, int N, int k, int d){

        System.out.println("Sorting with Radix Sort");

        String[] Input = List;                          //make a copy of the original input list
        String[] Output = new String[List.length];      //allocate 2nd string array of the same size
        int chunk = (int)Math.pow(256,d);               //calculate how many buckets will be needed based on d
        int[] Counts = new int[chunk];                  //allocate int array used for buckets
        int portion = 0;                                //used later to calculate which bucket to access
        int Z = k-1;                                    //Z is used to point at which part of the strings we are currently accessing
        int i,j,m;                                      // loop variables


        while(Z >= 0) {                                 //once Z drops below zero, we know we have gone through every chunk of the string

                for (i = 0; i < N; i++) {               //loop through every string in the array

                    /*If d is 3 and there are at least 3 characters left in the string, then grab 3 characters and calculate the bucket index
                    If d is 2 and there are at least 2 characters left in the string, or if d is 3 but there are only 2 characters left, then grab 2..
                    In all other situations, only grab 1 character and calculate the bucket index*/

                    if((d == 3) && (Z-d > -2))
                        portion = ((((int)List[i].charAt(Z-2))*65536) + (((int)List[i].charAt(Z-1))*256) + ((int)List[i].charAt(Z)));
                    else if (((d == 2) && (Z-k > -3) ) || ((d == 3) && (Z-d == -2)) )
                        portion = ((((int)List[i].charAt(Z-1))*256) + ((int)List[i].charAt(Z)));
                    else
                        portion = (int)List[i].charAt(Z);

                    Counts[portion]++;      //increment the corresponding bucket
                }

                for (j = 1; j < Counts.length; j++) {

                    Counts[j] = Counts[j - 1] + Counts[j];              //Calculate the Prefix Sum using the bucket array

                }

                for (m = N - 1; m >= 0; m--) {                  //starting with the end of the String array, work your way back

                    //calculate the bucket index by the same method explained above on lines 76-78

                    if((d == 3) && (Z-d > -2))
                        portion = ((((int)List[m].charAt(Z-2))*65536) + (((int)List[m].charAt(Z-1))*256) + ((int)List[m].charAt(Z)));
                    else if (((d == 2) && (Z-k > -3) ) || ((d == 3) && (Z-d == -2)) )
                        portion = ((((int)List[m].charAt(Z-1))*256) + ((int)List[m].charAt(Z)));
                    else
                        portion = (int)List[m].charAt(Z);

                    /*Using the calculated bucket index, find the value contained in that bucket. Subtract 1, and that is
                    the index that the current string should be moved to. Then decrement that bucket value by one, this
                    is so if you hit on the same bucket more than once, the corresponding strings won't be put in the same place */

                    Output[Counts[portion] - 1] = List[m];
                    Counts[portion]--;
                }

                Arrays.fill(Counts, 0);                 //Reset the buckets to 0 for the next iteration
                Input = Output;                             //Copy the new version of the string array back to the Input array
                Z=Z-d;                                      //Calculate where in the strings to look next by decrementing Z by the size of d.
        }
        return Input;
    }


    public static void QuickSort(String[] List, int bot, int top){

        if(bot < top){                                              //if list is big enough to be partitioned

            int PartitionIndex = Partition(List, bot, top);         //run a quicksort partition on the current list and return the pivot's final location

            QuickSort(List, bot, PartitionIndex - 1);           //quicksort the first half of the newest version of the list
            QuickSort(List, PartitionIndex, top);                   //quicksort the second half of the newest version of the list
        }

    }

    public static int Partition(String[] List, int bot, int pivot){

        int i = bot - 1;
        String Swap;

        for(int j = bot; j < pivot; j++){
            if(List[j].compareTo(List[pivot]) < 0){         //if j's element is smaller than the pivot element: increase i, then swap i and j's elements
                i++;
                Swap = List[j];
                List[j] = List[i];
                List[i] = Swap;
            }
        }

        Swap = List[pivot];                             // swap the pivot element with the element in front of i.
        List[pivot] = List[i+1];
        List[i+1] = Swap;

        return i+1;                                     // return the new index that pivot was swapped to
    }

    public static void MergeSort(String[] List, int l, int r){
    // referenced geeksforgeeks.org for parts of this algorithm

        if (l < r){                                 //ensures the list can be split into 2
            int m = (l+r)/2;                        // finds the middle point of the list

            MergeSort(List, l, m);                  //recursively calls itself using the first half of the list
            MergeSort(List, m+1, r);             //recursively calls itself using the second half of the list

            Merge(List,l,m,r);                      //merges the List on this particular recursive call
        }
    }

    public static void Merge(String[] List, int l, int m, int r){

        int LSize = m - l + 1;                      //size of left half
        int RSize = r - m;                          //size of right half

        String L[] = new String[LSize];             //creates string arrays of those sizes
        String R[] = new String[RSize];

        System.arraycopy(List,l, L, 0, LSize);          //copies left half of main string array
        System.arraycopy(List,m+1,R,0,RSize);    //copies right half of main string array

        int i = 0;
        int j = 0;
        int k = l;

        while (i < LSize && j < RSize){
            if(L[i].compareTo(R[j]) <= 0){                 //if string in L is less than or equal to string in R, copy L string to List array
                List[k] = L[i];
                i++;
            }
            else{
                List[k] = R[j];                             //R string is less, so copy it to List array
                j++;
            }
            k++;
        }

        while (i<LSize){                                    //grab any extra strings for odd numbered arrays
            List[k] = L[i];
            i++;
            k++;
        }

        while (j<RSize){
            List[k] = R[j];
            j++;
            k++;
        }
    }

    public static void BubbleSort(String[] List, int N, int k, int minV, int maxV){

        System.out.println("Sorting with Bubble Sort");

        String[] SortedList = List;     //make a copy of provided list

        int i;

        String t;                       //temporary string to help with swapping
        int z = N;
        while(N > 1) {

            for (i = 0; i < N - 1; i++) {
                if ((SortedList[i].compareTo(SortedList[i+1])) > 0) {       //compareTo will return something greater than zero if the first string is greater than the second
                    t = SortedList[i + 1];                                  // copy 2nd string to temp string holder
                    SortedList[i + 1] = SortedList[i];                      // replace 2nd string with 1st string
                    SortedList[i] = t;                                      // replace 1st string with 2nd string
                }

            }
            N--;
        }



    }

    public static void PrintList(String[] List, int N){

        for (int i=0; i < N; i++) {
            System.out.print(List[i]+" ");                            // prints List
        }
        System.out.println();

    }













    // Get CPU time in nanoseconds since the program(thread) started.
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadCpuTime( ) : 0L;
    }
}
