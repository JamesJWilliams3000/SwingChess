# SwingChess
A simple chess AI, made in Swing, around October 2020.

The GUI side of the program is contained in 4 classes: ChessApp, ChessBoardJPanel, TileJButton, and StretchIcon. 
 ![image|100](https://user-images.githubusercontent.com/112959298/207011449-8c1cbdb2-75e6-4568-a14b-a3d0f9e378c5.png)

1 The ChessApp at its minimum size of 300x300
 ![image|300](https://user-images.githubusercontent.com/112959298/207011480-254972ca-7012-4791-ae1b-4bf5896a9bd2.png)

2 When resized, the board resizes to the smallest dimension and stays a square.

The board also highlights any possible spots the piece can move to – which can be toggled on or off.
 ChessBoardJPanel is responsible for communicating with the chessboard – on both the AI side and human side. Once the player has made a move, ChessBoardJPanel moves the piece and then calls on the AI to make its move.
Onto the non-GUI side - each piece of the board is represented as its own class, all subclasses of Piece. The Chessboard itself is represented in two different ways – as an array of Piece, and as an array of bits. I’ll start by explaining how the array of bits work. My Chess app uses the Java BitSet class – specifically a BitSet of 64 bits representing the 64 tiles, where  0 is an empty tile and 1 is an occupied tile.

 ![image](https://user-images.githubusercontent.com/112959298/207011562-1a0a2d93-2541-4e6c-9048-3cbc17eaee60.png)

4 BitSet: a set representing the starting board, where 1 is a piece.

The BitSets are handled in three different classes: ChessBitSet, BitBoard, and BitBoardTables. ChessBitSet is a class that extends BitSet and only allows 64 bits, with some methods for modifying a set. BitSets are being used here for generating legal moves. Using the logical operators, and, or, and xor, BitSets can easily be used to find possible moves.

 ![image](https://user-images.githubusercontent.com/112959298/207011582-3c455c62-fb9c-4522-805a-6b3203c9afe0.png)

For example, here is a random board, the black bishop marked with a ‘B’. The ChessBitSet labeled SOUTHEAST shows the possible tiles that the bishop can move in the south east direction, assuming no pieces blocking the way.

 ![image](https://user-images.githubusercontent.com/112959298/207011596-faec9f4e-6f62-4f12-abc2-82fffa4396a2.png)

Both black and white pieces can prevent the bishop from moving further, so here we do an AND operation on the board ChessBitSet and the southeast ChessBitSet, giving us the tiles where pieces are in the way. We then get the index of the tile initially blocking the path, here being a white pawn. Since the bishop can move no further, we get the south east direction from the pawns position, as this represents all the blocked tiles.

 ![image](https://user-images.githubusercontent.com/112959298/207011627-8bdb7c38-aa19-4350-a930-33787326a397.png)

To get the tiles that the bishop can move to, we XOR the bishop’s south east ChessBitSet and the pawn’s south east ChessBitSet, giving us the tiles that are not blocked. Note: the pawn that was blocking is actually in this last ChessBitSet, because it is a white piece and therefore capturable.
The class BitBoard represents the entire chessboard as a collection of ChessBitSets. It contains a ChessBitSet of all pieces, another ChessBitSet of only white pieces, and a final ChessBitSet of only black pieces. It takes care of moving, capturing, and un-capturing pieces on its ChessBitSets.
BitBoardTables is a collection static ChessBitSets that are pre-calculated, to make move generation quicker. It contains arrays of ChessBitSet arrays for each direction at a given position, for example:
`BitBoardTables.getRay(SOUTH_EAST, 16);`

![image](https://user-images.githubusercontent.com/112959298/207011669-70d17dba-0986-4a86-a67f-9ea451e03856.png)

Returns a ChessBitSet where the indexes in the south east direction of index 16 are set to true, as shown on the left.

It also contains arrays for the knight’s moves, and both the pawn’s moves and the pawn’s attacks.

 ![image](https://user-images.githubusercontent.com/112959298/207011687-dbf20322-480d-4d63-8b40-91db2abf3cae.png)

5 What each array contains at index 16

Onto the Chessboard class, which contains the array of Pieces, as well as all of the methods for manipulating the board. There are two move() methods, one meant for the human player, and the other for the AI.  The ChessBitSets calculated by the pieces are pseudo-legal; they don’t see if it leaves the player in check or checkmate. Therefor when the AI makes a move, it has to check if the move put itself in check, if so, the method returns false and the move can’t be used. When a piece is captured, it is removed from both the BitBoard and the array of pieces. Lastly is generates a list of moves based on each pieces ChessBitSet, as well as a unique list of moves that the player must make if it is in check. It also has methods returning certain values for the AI board evaluator, such as mobility and capture opportunities. 

Lastly, the AI is contained in two classes: Evaluator and Move. Move contains the piece being moved, the start position and the end position. It also keeps track of the piece being captured, if possible. It uses the ChessBitSet to generate an `ArrayList<Move>`. It iterates through a ChessBitSet, and where an index is set to true is a possible end position for that piece. A particular ChessBitSet would look like this:

 ![image](https://user-images.githubusercontent.com/112959298/207011731-d1dbe1cd-8100-4ba5-ba51-09bf2963a481.png)

The first set is the board, the second is the ChessBitSet of all possible places the black bishop can move, and lastly, the ArrayList<Move> generated from the second ChessBitSet.

The Evaluator class extends Thread, because the Evaluator is called in the GUI after the human player makes a move. In order for the GUI to update the board immediately, the Evaluator has to be run on a separate thread from the GUI. The Evaluator iterates through a game tree 4 plies deep, making and un-making moves for each player. It searches through the tree using both minimax and an alpha-beta algorithm. At the root the Evaluator calls the max method, and for each (un-pruned) move, the board recursively calls its max and min methods, until it either hits a its last ply or it has a checkmate. At the end it finally scores the resulting board, returning an int value. For each move at the root, the max value is saved, alongside its corresponding root move. 

For its evaluation method, it scores the board in several ways: 

Material – each piece type has its own score, for example pawn has a score if 100, and queen a score of 900.

Mobility – its counts the number of tiles each piece can move to, so boards with more mobility are favored.

![image](https://user-images.githubusercontent.com/112959298/207011759-e99220e9-b743-4062-83a2-61df5e6732a0.png)

Piece-Squares – Evaluator has several int arrays containing a score for each piece on the board, so that certain pieces favor certain areas of the board. For example:
The table on the left contains the values for knights, so that knights disfavor the edge tiles and favor the center tiles.

Capturing – players gain a larger score if they can capture other pieces.

The evaluate() method scores each player based on these categories, and then subtracts the white player’s score from the black player’s, and that score is returned to the minimax methods. When the Evaluator is done searching through the game tree, it returns the best move to ChessBoardJPanel, which is then responsible for sending the move to the Chessboard.

After each player makes a move, ChessBoardJPanel checks to see if a King was captured and if the game is over. If so, it lets the player know if it won/lost and if the player wants to play again. ChessBoardJPanel is also responsible for pawn promotion; if the player can promote a pawn, it sends a screen asking which piece to promote it to.

 ![image](https://user-images.githubusercontent.com/112959298/207011791-2bf52d1c-74d1-44bb-b671-0fc3899d8e4f.png)


