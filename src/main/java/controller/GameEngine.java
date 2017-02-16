package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.java.model.*;
import main.java.model.pieces.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Jesper Nylend on 10.02.2017.
 * s305070
 */
public class GameEngine implements Initializable, IControls {
    private static final double IMAGE_SIZE = 98;
    private static final int CELL_SIZE = 100;
    private static final double PADDING = 1.0;
    private Board board;
    private Player p1;
    private Player p2;
    private boolean whiteToMove = true;
    private Piece selectedPiece;


    @FXML
    Canvas cv;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ArrayList<Piece> white = new ArrayList<>();
        ArrayList<Piece> black = new ArrayList<>();
        Bishop bishop = new Bishop(PieceColor.WHITE, new Square(4, 4));
        Bishop bishop1 = new Bishop(PieceColor.BLACK, new Square(2, 2));
        Queen queen = new Queen(PieceColor.WHITE, new Square(5, 5));
        white.add(queen);
        white.add(bishop);
        black.add(bishop1);

        board = new Board(white, black);

        p1 = new HumanPlayer("White", PieceColor.WHITE, this);
        p2 = new HumanPlayer("Black", PieceColor.WHITE, this);

        GraphicsContext gc = cv.getGraphicsContext2D();

        cv.setOnMouseClicked(event -> {
                    // TODO there is a bug here! Have to check what is inside the square instead. (try to click on bishop, and then on queen two times)
                    if (selectedPiece == null) {
                        System.out.println("trykket");
                        selectPieceToMove(event.getX(), event.getY(), gc);
                    } else {
                        drawMove(event.getX(), event.getY());
                    }
                }
        );

        initDraw(gc);
        draw(gc);
    }

    private void reset() {
        p1 = new HumanPlayer("White", PieceColor.WHITE, this);
        p2 = new HumanPlayer("Black", PieceColor.WHITE, this);
        board.resetBoard();
        whiteToMove = true;
    }


    private void initDraw(GraphicsContext gc) {
        for (int c = 0; c < 9; c++) {
            for (int r = 0; r < 9; r++) {
                gc.strokeLine(r * CELL_SIZE, 0, r * CELL_SIZE, CELL_SIZE * 8);
            }
        }
        for (int c = 0; c < 9; c++) {
            for (int r = 0; r < 9; r++) {
                gc.strokeLine(0, r * CELL_SIZE, CELL_SIZE * 8, r * CELL_SIZE);
            }
        }
        for (int c = 0; c < CELL_SIZE * 9; c += CELL_SIZE) {
            if ((c - CELL_SIZE) % (CELL_SIZE * 2) != 0) {
                for (int r = 0; r < CELL_SIZE * 9; r += CELL_SIZE) {
                    if (r % (CELL_SIZE * 2) != 0) {
                        gc.setFill(Color.GREY);
                        gc.fillRect(c + 1, r + 1, IMAGE_SIZE, IMAGE_SIZE);
                    }
                }
            } else {
                for (int r = 0; r < CELL_SIZE * 9; r += CELL_SIZE) {
                    if (r % (CELL_SIZE * 2) == 0) {
                        gc.setFill(Color.GREY);
                        gc.fillRect(c + 1, r + 1, IMAGE_SIZE, IMAGE_SIZE);
                    }
                }
            }
        }
    }

    public void doMove(Move move) {
        board.movePiece(move);
        whiteToMove = !whiteToMove;
    }

    private void draw(GraphicsContext gc) {
        ArrayList<Piece> whitePieces = board.getWhitePieces();
        ArrayList<Piece> blackPieces = board.getBlackPieces();

        for (Piece piece : whitePieces) {
            drawPiece(piece, gc);
        }
        for (Piece piece : blackPieces) {
            drawPiece(piece, gc);
        }
    }

    private void drawPiece(Piece piece, GraphicsContext gc) {
        if (piece instanceof Pawn) {
            gc.drawImage(new Image("pieces/" + piece.getColorString() + "_pawn.png"), (piece.getSquare().getColumn() * CELL_SIZE) + PADDING, (piece.getSquare().getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
        } else if (piece instanceof Bishop) {
            gc.drawImage(new Image("pieces/" + piece.getColorString() + "_bishop.png"), (piece.getSquare().getColumn() * CELL_SIZE) + PADDING, (piece.getSquare().getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
        } else if (piece instanceof Knight) {
            gc.drawImage(new Image("pieces/" + piece.getColorString() + "_knight.png"), (piece.getSquare().getColumn() * CELL_SIZE) + PADDING, (piece.getSquare().getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
        } else if (piece instanceof Rook) {
            gc.drawImage(new Image("pieces/" + piece.getColorString() + "_rook.png"), (piece.getSquare().getColumn() * CELL_SIZE) + PADDING, (piece.getSquare().getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
        } else if (piece instanceof Queen) {
            gc.drawImage(new Image("pieces/" + piece.getColorString() + "_queen.png"), (piece.getSquare().getColumn() * CELL_SIZE) + PADDING, (piece.getSquare().getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
        } else {
            gc.drawImage(new Image("pieces/" + piece.getColorString() + "_king.png"), (piece.getSquare().getColumn() * CELL_SIZE) + PADDING, (piece.getSquare().getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
        }
    }

    private void selectPieceToMove(double x, double y, GraphicsContext gc) {
        int row = (int) y / CELL_SIZE;
        int column = (int) x / CELL_SIZE;
        Square tmp = new Square(row, column);
        System.out.println(tmp);
        if (whiteToMove) {
            System.out.println("White to move");
            for (Piece piece : board.getWhitePieces()) {
                System.out.println(piece);
                if (tmp.getRow() == piece.getSquare().getRow() && tmp.getColumn() == piece.getSquare().getColumn()) {
                    selectedPiece = piece;
                    System.out.println(selectedPiece.toString());
                    drawPossibleMoves(selectedPiece, gc);
                }
            }
        } else {
            for (Piece piece : board.getBlackPieces()) {
                if (tmp.getRow() == piece.getSquare().getRow() && tmp.getColumn() == piece.getSquare().getColumn()) {
                    selectedPiece = piece;
                    drawPossibleMoves(selectedPiece, gc);
                }
            }
        }
    }

    //TODO: implement this method
    private void drawMove(double x, double y) {


        selectedPiece = null;
    }

    //TODO: Fix drawing on other pieces && new PNG
    private void drawPossibleMoves(Piece piece, GraphicsContext gc) {
        PieceColor opponentColor = (piece.getColor() == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        ArrayList<Square> possibleMoves = piece.validMoves(board.generateBitmapPositions(), board.generateBitmapAttackingPositions(opponentColor));
        int[][] bitmapPositions = board.generateBitmapPositions();

        for (Square validSquare : possibleMoves) {
            int vx = validSquare.getColumn();
            int vy = validSquare.getRow();
            if (bitmapPositions[vy][vx] != 0) {
                gc.drawImage(new Image("attack_sprite.png"), (validSquare.getColumn() * CELL_SIZE) + PADDING, (validSquare.getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
            } else {
                gc.drawImage(new Image("move_sprite.png"), (validSquare.getColumn() * CELL_SIZE) + PADDING, (validSquare.getRow() * CELL_SIZE) + PADDING, IMAGE_SIZE, IMAGE_SIZE);
            }
        }
    }
}
