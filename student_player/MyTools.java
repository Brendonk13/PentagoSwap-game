package student_player;

import boardgame.Move;     

import java.util.BitSet;
import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoBoardState;

public class MyTools {
    
    
    public static BitSet[] createBitboard(Piece[][] board, boolean whitePlayer) {
        BitSet mine = new BitSet(42);
        BitSet mask = new BitSet(42);
        int bitCount = 0;
        for(int j = 5; j >= 0; j--) { // their j == 6!!
            bitCount++;
            for(int i = 0; i < 6; i++ ) { // loop 6 values since we add 0 to top of each row each time.
                
                boolean empty = board[i][j] == PentagoBoardState.Piece.EMPTY ? true : false;
                if (!empty) {
                    boolean whitePiece = board[i][j] == PentagoBoardState.Piece.WHITE ? true : false;
                    mask.set(bitCount);
                    if (whitePlayer){
                        if (whitePiece) { mine.set(bitCount); }
                    }
                    else { // we're not the white player.
                        if (!whitePiece) { mine.set(bitCount); }
                    }
                }
            bitCount++;
            }
        }
        
        BitSet[] ret = {mine, mask};
        return ret;
    }
    
    public static void printBitSet(BitSet bs) {
        String [] rows = new String[7];
        char[] tmp = new char[7];
        
        for (int i = 0; i < 7 ; i++) {
            
            for (int j = 0; j < 6; j++) {
                
                int pos = j*7 + i%7;
                tmp[j] = bs.get(pos) ? '1' : '0';   
            }
            
            rows[i] = ""; 
            for(int j = 6; j >=0 ; j--) {
                rows[i] += tmp[j];
            }
            
            System.out.println("row "+ i +" : "+ rows[i]);
        }
    }  
    

    public static Move createMove(int idx, int swapCode) {
        int column = 0;
        
        if(0 < idx && idx < 7)          column = 5;  //// got idx == 7 from a move, did TL,TR also
        
        else if (7 < idx && idx < 14)   column = 4;
        
        else if ( 14 < idx && idx < 21) column = 3;
        
        else if (21 < idx && idx < 28)  column = 2;
        
        else if (28 < idx && idx < 35)  column = 1;
        
        else if (35 < idx && idx < 42)  column = 0;
        
        else {          
            column = 10;
        }
        
        idx = (idx %7) - 1;
        String swap = swapToString(swapCode);
        String arg = "" + idx + " " + column + " " + swap + "260685377";
        //String arg = "" + 0 + " " + 5 + " " + swap + "260685377";
        
        return new PentagoMove(arg);
    }

    
    public static int checkForWin(BitSet state, BitSet theirBoard) { // return 1 for win, -1 for lose
        
        BitSet[] myWins = Shifts.doShifts(state, 5, true);
        BitSet[] theirWins = Shifts.doShifts(theirBoard, 5, true);


        boolean iWon = !myWins[0].isEmpty() || !myWins[1].isEmpty() || !myWins[2].isEmpty() || !myWins[3].isEmpty();
        boolean theyWon = !theirWins[0].isEmpty() || !theirWins[1].isEmpty() || !theirWins[2].isEmpty() || !theirWins[3].isEmpty();
        
        if (iWon && theyWon) {
            //draw, we both have a win
            return 0;
        }
        else if (iWon) {
            return 3000;    
        }
        else if (theyWon){
            return -3000;
        }
        else {
            return 13;
        }
    }
    
    
    public static int getSwappedIdx(int swapCode, int old_idx) {
        int newPos = old_idx;
        switch(swapCode) {
        
        case 0: 
            if ( 0 < old_idx && old_idx < 4 || 7 < old_idx && old_idx < 11 || 14 < old_idx && old_idx  < 18 ) //TR
                newPos = old_idx + 21;
            
            if ( 21 < old_idx && old_idx < 25 || 28 < old_idx && old_idx < 32 || 35 < old_idx && old_idx  < 39 ) //TL
                newPos = old_idx - 21;
            
            break;
            
        case 1:
            if ( 21 < old_idx && old_idx < 25 || 28 < old_idx && old_idx < 32 || 35 < old_idx && old_idx  < 39 ) //TL
                newPos = old_idx - 18;
            
            if ( 3 < old_idx && old_idx < 7 || 10 < old_idx && old_idx < 14 || 17 < old_idx && old_idx  < 21 ) //BR
                newPos = old_idx + 18;
            
            break;
            
        case 2:
            if ( 21 < old_idx && old_idx < 25 || 28 < old_idx && old_idx < 32 || 35 < old_idx && old_idx  < 39 ) //TL
                newPos = old_idx + 3;
            
            if ( 24 < old_idx && old_idx < 28 || 31 < old_idx && old_idx < 35 || 38 < old_idx && old_idx  < 42 ) //BL
                newPos = old_idx - 3;
            
            break;
            
        case 3:
            if ( 0 < old_idx && old_idx < 4 || 7 < old_idx && old_idx < 11 || 14 < old_idx && old_idx  < 18 ) //TR
                newPos = old_idx + 3;
            
            if ( 3 < old_idx && old_idx < 7 || 10 < old_idx && old_idx < 14 || 17 < old_idx && old_idx  < 21 ) //BR
                newPos = old_idx - 3;
            
            break;
            
        case 4:
            if ( 0 < old_idx && old_idx < 4 || 7 < old_idx && old_idx < 11 || 14 < old_idx && old_idx  < 18 ) //TR
                newPos = old_idx + 24;
            
            if ( 24 < old_idx && old_idx < 28 || 31 < old_idx && old_idx < 35 || 38 < old_idx && old_idx  < 42 ) //BL
                newPos = old_idx - 24;
            
            break;
            
        case 5: 
            if ( 24 < old_idx && old_idx < 28 || 31 < old_idx && old_idx < 35 || 38 < old_idx && old_idx  < 42 ) //BL
                newPos = old_idx - 21;
            
            if ( 3 < old_idx && old_idx < 7 || 10 < old_idx && old_idx < 14 || 17 < old_idx && old_idx  < 21 ) //BR
                newPos = old_idx + 21;
            
        }
        
        return newPos;
    }
    

    
    
    public static String swapToString(int swapCode) {  
        String ret;
        switch (swapCode) {
        
        case 0:
            ret = "TL TR ";
            return ret;
        case 1:
            ret = "TL BR ";
            return ret;
        case 2:
            ret = "TL BL ";
            return ret;
        case 3:
            ret = "TR BR ";
            return ret;
        case 4:
            ret = "TR BL ";
            return ret;
        case 5:
            ret = "BL BR ";
            return ret;
        default:
            return "error";
        }
    }

    
}//end class