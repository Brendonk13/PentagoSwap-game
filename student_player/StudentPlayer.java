package student_player;

import boardgame.Move;  

import java.util.List;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoBoardState;

/*
* @author Brendon Keirle, 260685377
* 
* Process: chooseMove is called when the opponents turn is over.
* call negaMaxRoot with maxDepth = 3 to search 3 ply ahead
* branches pruned according to alpha beta pruning algorithm as well as moves which result in the opponent winning.
* evaluate board after each swap to check if we have swapped the opponent into a win
*
*
*/




public class StudentPlayer extends PentagoPlayer {

    public StudentPlayer() {
        super("260685377");
    }

    public static double heuristic(BitSet myBoard, BitSet theirBoard, int depth) {
        BitSet[] myWins = Shifts.doShifts(myBoard, 5, true);
        BitSet[] theirWins = Shifts.doShifts(theirBoard, 5, true);

        int me_win = myWins[0].isEmpty() && myWins[1].isEmpty() && myWins[2].isEmpty() && myWins[3].isEmpty() ? 0 : 1000;

        int them_win = theirWins[0].isEmpty() && theirWins[1].isEmpty() && theirWins[2].isEmpty() && theirWins[3].isEmpty() ? 0 : -1000;

        if (me_win != 0 || them_win != 0)
            return me_win + them_win;      // == 0 if draw. 
        
        int[] myCounts = BitBoard.getNumAligned(myBoard);
        int[] theirCounts = BitBoard.getNumAligned(theirBoard);
        

        double ret = (12 * myCounts[0] + 12 * myCounts[1] + 18*myCounts[2] + 18*myCounts[3]);
        ret = ret - 18 * theirCounts[1] - 18*theirCounts[2] - 22*theirCounts[3] - 22*theirCounts[3];// this was good
        // double ret = me_win + them_win + myCounts[0]*myCounts[0] +
        // myCounts[1]*myCounts[1] - theirCounts[1]*theirCounts[1];//)/depth;

        return ret;
    }


    public Move chooseMove(PentagoBoardState boardState) {

        boolean isWhitePlayer = boardState.getTurnPlayer() == 0 ? true : false; // this returns 0 if we're white player.
        // PentagoMove move = (PentagoMove) boardState.getRandomMove();

        
        
        PentagoBoardState.Piece [][] board = new PentagoBoardState.Piece[6][6];
        
        //-------------MUST BE CHANGED !!!!!!!
        //PentagoBoardState.Piece[][] board = boardState.getBoard(); // need to change this to a loop where I call getPieceAt to get the board
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {        
                board[row][col] = boardState.getPieceAt(row, col);
            }
        }
        
        
        BitSet[] state = MyTools.createBitboard(board, isWhitePlayer); 

        // state[0] is a BitSet object with 1's where my pieces are
        // state[1] contains 1's for both players boards, is used as a mask to get the oppositions board.

        // ie: theirBoard = state[0].xor(state[1]) = state[1].xor(state[0])


        PentagoMove move = (PentagoMove) negaMaxRoot(state, 3); 
        // PentagoMove move = (PentagoMove) boardState.getRandomMove();

        return move; // Return your move to be processed by the server.
    }


    public static Move negaMaxRoot(BitSet[] state, int maxDepth) {
        int bestPlacementIdx = 1;
        int bestSwapCode = 0;

        double max = -4000;
        double score = 0;

        BitSet swapped;
        BitSet swapped_mask;
        int placed_idx;
        int rand;
        int lastSwapCode;
        int swapped_idx;

        double tmp = Math.random() * 6;
        int dir = tmp < 3 ? 1 : -1;
        lastSwapCode = (int) tmp;                  // 0 <= lastSwapCode <= 5

        int j = lastSwapCode;
        boolean b = true;
        while ((((j = (j + dir) % 6) != lastSwapCode) || b) && b) {
            if (j <= -1) {                   // then just did zero
                if (lastSwapCode == 5) {
                    break;
                }
                j = 5;
            }
            
            if (j == lastSwapCode)
                b = false;

            swapped = BitBoard.getSwappedBoard(state[0], j);

            BitSet theirSwapped = (BitSet) swapped.clone();

            swapped_mask = BitBoard.getSwappedBoard(state[1], j);
            
            theirSwapped.xor(swapped_mask);

            String jj = MyTools.swapToString(j);

            int winCode = MyTools.checkForWin(swapped, theirSwapped);
            
            if (winCode == -3000) 
                continue;
            

            BitSet accum = (BitSet) state[1].clone();

            for (int i = 0; i < 6; i++) {  // need to set sentinel row to all 1's so that these are not considered as a move.
                accum.set(i * 7);
            }

            // when board is full, (ie have tried all moves) then accum is all 1's
            // this corresponds with when the flipped accum is all zero's. This is the basis of my move generation loop. 
            accum.flip(0, 42);         


            while (!accum.isEmpty()) {                                 // while we haven't tried all moves

                // flip to normal accum 
                accum.flip(0, 42);                                 
                
                rand = (int) (Math.random() * 42);

                while ((placed_idx = accum.previousClearBit(rand)) == -1) { ///// neeed to find an index in the
                                                                            ///// swapped_mask
                    rand = 41;
                }
                

                accum.set(placed_idx);

                accum.flip(0, 42);      // refer to first accum.flip outside of this loop for explanation of why this is done.

                swapped_idx = MyTools.getSwappedIdx(j, placed_idx); // placed_idx is the position we place a piece in Original board

                
                swapped_mask.set(swapped_idx); // must be the case that swapped_mask d.n accumulate sets.
           

                 score = - negaMax(theirSwapped, swapped_mask, 1, maxDepth, -1000, 1000);
                 
                 if (score == 3000) {
                     System.out.println("found a win!");
                     //return MyTools.createMove(placed_idx, j);
                 }
                 
                 swapped_mask.clear(swapped_idx);
                 
            
                if (score > max) {
                    max = score;
                    bestPlacementIdx = placed_idx; // placed_idx is the position we placed tile in unSwapped board.
                    bestSwapCode = j;
                }

            } // swap loop

        } // moves loop
        return MyTools.createMove(bestPlacementIdx, bestSwapCode);
    }

    public static double negaMax(BitSet state, BitSet mask, int currDepth, int maxDepth, double alpha, double beta) {
        
        BitSet theirBoard = (BitSet) mask.clone();          // mask is all pieces on the board.
        theirBoard.xor(state);

        int winCode = MyTools.checkForWin(theirBoard, state);
        if (winCode != 13) { // then there is a draw or winning state in the calling functions move.
            
            if (currDepth != maxDepth)
                return -winCode;
            
            else 
                return -(winCode*0.98);    //goal is to not score moves I immediatly win on the same as ones 2 moves ahead
            
        }
        
        if (currDepth == maxDepth) 
            return -heuristic(theirBoard, state, currDepth); // reverse accum and state, to evaluate from calling fxns perspective
        

        double max = -4000;
        double score = 0;

        BitSet swapped;
        BitSet swapped_mask;
        int placed_idx;
        int rand;
        int lastSwapCode;
        int swapped_idx;

        double tmp = Math.random() * 6;
        int dir = tmp < 3 ? 1 : -1;
        lastSwapCode = (int) tmp; // 0 <= lastSwapCode <= 5

        int j = lastSwapCode;
        boolean b = true;
        while ((((j = (j + dir) % 6) != lastSwapCode) || b) && b) {
            if (j <= -1) { // just did zero
                if (lastSwapCode == 5) {
                    break;
                }
                j = 5;
            }

            if (j == lastSwapCode)
                b = false;

            swapped = BitBoard.getSwappedBoard(state, j);

            BitSet theirSwapped = BitBoard.getSwappedBoard(theirBoard, j);

            swapped_mask = BitBoard.getSwappedBoard(mask, j);
            

            winCode = MyTools.checkForWin(swapped, theirSwapped);
            
            if (winCode == -3000 ) 
                continue;
            

            BitSet accum = (BitSet) mask.clone(); // was cloning state[1]
            for (int i = 0; i < 6; i++) {
                accum.set(i * 7);
            }

            accum.flip(0, 42);


            while (!accum.isEmpty()) { // while we haven't tried all moves

                accum.flip(0, 42); // flip to normal accum.

                rand = (int) (Math.random() * 42);

                while ((placed_idx = accum.previousClearBit(rand)) == -1) { ///// neeed to find an index in the
                                                                            ///// swapped_mask
                    rand = 41;
                }

                accum.set(placed_idx);
                accum.flip(0, 42); // don't use again

                swapped_idx = MyTools.getSwappedIdx(j, placed_idx); // placed_idx is the position we place a piece in
                swapped_mask.set(swapped_idx);


                score = -negaMax(theirSwapped, swapped_mask, currDepth + 1, maxDepth, -beta, -alpha);
                
                swapped_mask.clear(swapped_idx);
                
                if (score == 3000)
                    return score;
                
                
                max = Math.max(max, score);
                alpha = Math.max(alpha, score);

                if (alpha >= beta) {
                    return alpha;
                }
            }
        }
        return max;
    }
    
}// end of class