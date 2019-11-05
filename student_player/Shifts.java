package student_player;

import java.util.BitSet; 

public class Shifts {

	
	public static BitSet swapMyQuadrants(BitSet state, int idx_1, int idx_2, String swapType) {

		BitSet swapped = (BitSet) state.clone();             		// need to clear quadrants getting swapped.
		for (int i = 0; i<3;i++) {	
			swapped.clear(idx_1, idx_1+3);
			swapped.clear(idx_2, idx_2+3);
	
			idx_1 += 7;                                           // next cleared column is to the left of the one just cleared.
			idx_2 += 7;                                           
		}
		
		idx_1 -= 21;             // reset idx's to their previous values before loop
		idx_2 -= 21;
		int i = 0;
		
		for(int cp1 = idx_1,  cp2 = idx_2;   i<3;   cp1 += 4,  cp2 += 4,   i++) {
			
			swapped.set(cp1, cp1+1, state.get(cp2) ); 
			swapped.set(cp2, cp2+ 1, state.get(cp1));
			cp1++;
			cp2++;
			
			swapped.set(cp1, cp1+1, state.get(cp2) ); 
			swapped.set(cp2, cp2+ 1, state.get(cp1));
			cp1++;
			cp2++;
			
			swapped.set(cp1, cp1+1, state.get(cp2) );
			swapped.set(cp2, cp2+ 1, state.get(cp1));
			cp1++;
			cp2++;
		}

		return swapped;
		
	}
	
	public static int numL_diag(int iter, boolean ret12) {
		
		switch(iter) {

		case 0:
			if (ret12)  return 12;
			else        return 6;
		
		case 1:
			return 6;
			
		default:
			return 6;
		}
	}
	
	public static int numR_diag(int iter, boolean ret16) { 
		switch(iter) {
					
		case 0:
			if (ret16)  return 16;
			else        return 8;
			
		case 1:
			return 8;
		
		default:
			return 8;
		}
	}
	
	public static BitSet[] doShifts(BitSet state, int size, boolean getDiags) {
		if (!getDiags) {
			switch(size) {
			
			case 3:
				return getShifted(state, 7, 1, false, false, false);
			
			case 4:
				return getShifted(state, 14, 2, false, false, false);
				
			case 5: 
				return getShifted(state, 14, 2, false, false, true);
				
			default:
				return getShifted(state, 7, 1, false, false, false);
			}
		}
		else {
			switch(size) {
			
			case 3:
				return getShifted(state, 7, 1, true, false, false);
			
			case 4:
				return getShifted(state, 14, 2, true, false, false);
				
			case 5: 
				return getShifted(state, 14, 2, true, false, true);
				
			default:
				return getShifted(state, 7, 1, true, false, false);   // default is to do case 3
			}
			
		}
	}
	

	 public static BitSet[] getShifted(BitSet state, int num_horiz, int num_vert, boolean do_diag, boolean type, boolean do3Shifts) {
		 	
	    	
		 	BitSet horizShifted = singleShift(state, 7, type);
		 	
		 	if (!horizShifted.isEmpty())
		 		horizShifted = singleShift(horizShifted, num_horiz, type);
		 	
		 	if (do3Shifts && !horizShifted.isEmpty())
		 		horizShifted = singleShift(horizShifted, 7, type);
		 	
		 	
		 	
	 		BitSet vertShifted = singleShift(state, 1, false);
	 		
	 		if(!vertShifted.isEmpty())
	 			vertShifted = singleShift(vertShifted, num_vert, false);
	 		
	 		if(do3Shifts && !vertShifted.isEmpty())
	 			vertShifted = singleShift(vertShifted, 1, false); 
		 	

		 	if (do_diag) {
		 		
	 			BitSet L_diag = singleShift(state, 6, false);
	 			BitSet R_diag = singleShift(state, 8, false);
	 			
		 		for(int i = 0; i< 2; i++) {
		 			
		 			if (i == 1 && !do3Shifts) break;
		 			
		 			if (!L_diag.isEmpty())
		 				L_diag = singleShift(L_diag, numL_diag(i, do3Shifts), false);
		 			
		 			if (!R_diag.isEmpty())
		 				R_diag = singleShift(R_diag, numR_diag(i, do3Shifts), false);
		 			
		 		}
		 		BitSet[] ret = {horizShifted, vertShifted, L_diag, R_diag};
		 		return ret;
		 				
		 	}
		 	else {
		 		BitSet[] ret = {horizShifted, vertShifted};
		 		return ret;
		 	}
	    }
	 
	 
	 
	 
	 public static BitSet singleShift(BitSet state, int num_shift, boolean printShifts) {

	    BitSet state_cp = new BitSet(42);
	    		
	    int idx =  state.nextSetBit(0); 
	    int newSpot = 0;	
	    while(idx >= 0) {                           // this loop does state_cp = state >> fstShift	
	    	newSpot = idx + num_shift;
	    	
	    	if (newSpot < 42 && Math.abs(7-newSpot)%7 > 0 ) 
	    		state_cp.set(newSpot); 
	    	
	    	idx = state.nextSetBit(idx+1);
	    }
	    	
	   	state_cp.and(state);                          // state_cp = state & (state >> num_shift) 
	   	
	   	
	   	if (printShifts) {
	   		System.out.println("this is the horiz state after 1 shift and a AND");
	   		MyTools.printBitSet(state_cp);
	   		System.out.println("");	   		
	   	}
	   	
   		return state_cp;
	 }
	
}
