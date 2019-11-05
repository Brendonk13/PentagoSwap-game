package student_player;

import java.util.BitSet;

public class BitBoard{
	
	// this function swaps quadrants in state according to the swapCode
	public static BitSet getSwappedBoard(BitSet state, int swapCode) { 
		
		BitSet ret = new BitSet(42); 

        switch(swapCode) { // swapCode is 'j' loop counter variable in negaMax/negaMaxRoot
        case 0:
        	ret = Shifts.swapMyQuadrants(state, 22, 1, "top left, top right");  // swap top left, top right
        	break;

        case 1:
        	ret = Shifts.swapMyQuadrants(state, 22, 4, "top left, bottom right");  // swap top left, bottom right
        	break;
        	
        case 2:
        	ret = Shifts.swapMyQuadrants(state, 22, 25, "top left, bottom left");  // swap top left, bottom left
        	break;
        	
       	case 3:
       		ret = Shifts.swapMyQuadrants(state, 1, 4, "top right, bottom right");  // swap top right, bottom right
        	break;
        	
        case 4:
        	ret = Shifts.swapMyQuadrants(state, 1, 25, "top right, bottom left");  // swap top right, bottom left
        	break;
        		
        case 5:
        	ret = Shifts.swapMyQuadrants(state, 25, 4, "bottom left, bottom right");  // swap bottom left, bottom right
        	break;
        }
       
        return ret;
    }

	    
	 
	 public static int countVert(BitSet vert, int types, int loopNum) {
		 int vertCount = 0;
		 switch(types) {
		 
		 case 2:
		
			if (vert.get(17 - loopNum*7) ^ vert.get(16 - loopNum*7))  vertCount++;
			if (vert.get(20 - loopNum*7) ^ vert.get(19 - loopNum*7))  vertCount++;
			if (vert.get(38 - loopNum*7) ^ vert.get(37 - loopNum*7))  vertCount++;
			if (vert.get(41 - loopNum*7) ^ vert.get(40 - loopNum*7))  vertCount++;
			break;
		
		 case 3:

			if (vert.get(17 - loopNum*7))  vertCount++;
			if (vert.get(20 - loopNum*7))  vertCount++;
			if (vert.get(38 - loopNum*7))  vertCount++;
			if (vert.get(41 - loopNum*7))  vertCount++;
			break;
			
		 }
		 
		 return vertCount;
	 }
	 
	 public static int countHoriz(BitSet horiz, int types, int loopNum) {
		 
		int horizCount = 0;
		switch(types) {  // if types = 2, then we return number of horiz sequences of two's, types = 3 returns number of sequences of three's
		 
		case 2:

			if (horiz.get(15 + loopNum) ^ horiz.get( 8 + loopNum))  horizCount++;
			if (horiz.get(18 + loopNum) ^ horiz.get(11 + loopNum))  horizCount++;
			if (horiz.get(36 + loopNum) ^ horiz.get(29 + loopNum))  horizCount++;
			if (horiz.get(39 + loopNum) ^ horiz.get(32 + loopNum))  horizCount++;
			break;
			
		case 3:

			if (horiz.get(15 + loopNum))  horizCount++;
			if (horiz.get(18 + loopNum))  horizCount++;
			if (horiz.get(36 + loopNum))  horizCount++;
			if (horiz.get(39 + loopNum))  horizCount++;
			break;

		}

		return horizCount;
	 }
	 
	 
	 
	 public static int[] getNumAligned(BitSet state) { // maybe pass in the three in a rows computed in getWin call.

		 BitSet vert2 = Shifts.singleShift(state, 1 ,false);
		 BitSet horiz2 = Shifts.singleShift(state, 7, false);
		 
		 if (vert2.isEmpty() && horiz2.isEmpty()) { // if there are no 2 in a rows in this move then there are no three's either, this is a bad move.
			 int[] ret = {0, 0, 0, 0}; //-----------------maybe I should return dif values if empty to score this worse?
			 return ret;
		 }
		 
		 int numAlignedHoriz = 0;
		 int numAlignedVert = 0;
		 int numHoriz3 = 0;
		 int numVert3 = 0;
		 
		 BitSet horiz3 =  Shifts.singleShift(horiz2, 7, false);
		 BitSet vert3 = Shifts.singleShift(vert2, 1, false);
		 
		 		 
		 if (horiz3.isEmpty() && vert3.isEmpty()) {
			 //System.out.println("found no threes with move placed: "+ ind+ " and with swap: "+ Swapc);
			 int[] ret = {0,0,0,0};
			 return ret;
		 }
			  
		 for(int i=0; i < 3; i++) {
			 
			if (!horiz3.isEmpty()) {
				int tmp3 = countHoriz(horiz3, 3, i);
				
				if(tmp3 > 0) {        // If there are no sequence of three's then we do not search for aligned sequences of two's 

					numHoriz3 += tmp3;
					int tmp2 = countHoriz(horiz2, 2, i);
					
					if (tmp2 > 0)                     
						numAlignedHoriz += tmp2 + tmp3;      
				}
			}
			
			if (!vert3.isEmpty()) {
				int tmp3 = countVert(vert3, 3, i);
				
				if (tmp3 > 0) {         // If there are no sequence of three's then we do not search for aligned sequences of two's 
					
					numVert3 += tmp3;
					int tmp2 = countVert(vert2, 2, i);
					
					if (tmp2 > 0)
						numAlignedVert += tmp2 + tmp3;
				}
			} 
		}
	
		 
		 int[] ret = {numAlignedHoriz, numAlignedVert, numHoriz3, numVert3};

		 return ret;
	}

}//end class
