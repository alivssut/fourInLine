package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {

    public static int PLAYER_WIN = -10;
    public static int COMPUTER_WIN = 10;
    public static int DRAW = 0;
    public static String PLAYER = "O";
    public static String COMPUTER = "X";
    public static String DEFAULT = "_";
    public static String PLAYER_STYLE = "-fx-stroke: black;-fx-fill: blue";
    public static String COMPUTER_STYLE = "-fx-stroke: black;-fx-fill: red";
    public static String DEFAULT_STYLE = "-fx-stroke: black;-fx-fill: white";
    public static String state = PLAYER;
    public static String statePane = PLAYER_STYLE;
    public static int BOARD_SIZE = 8;
    public static int gameState = DRAW;
    public static int INFINITE = 10000;
    public static int MAX_DEPTH = 6;
    public static Circle boardHomes[][];
    public static Pane boardPane;
    public static Button columnButtons[];
    public static Dialog<String> dialog;
    public static TextField endText = new TextField();
    public static ButtonType retry;
    public static MenuBar menuBar;
    public static Menu menu;
    public static MenuItem gameItem, negamaxGame, minmaxGame;
    public static Label stateLabel;
    public static boolean isNegamax = false;
    public static Label algorithmNameLabel;
    public static AtomicInteger counter;


    public static boolean columnIsFill(String board[][], int column) {
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            if (board[i][column].equals(DEFAULT)) {
                return false;
            }
        }
        return true;
    }

    public static boolean allColumnIsFill(String board[][]) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!columnIsFill(board, i)) {
                return false;
            }
        }
        return true;
    }

    public static void move(String board[][], int column, String type, boolean paneFill) {
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            if (board[i][column].equals(DEFAULT)) {
                board[i][column] = type;
                if (paneFill) boardHomes[i][column].setStyle(statePane);
                break;
            }
        }
    }

    public static void lastMove(String board[][], int column) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!board[i][column].equals(DEFAULT)) {
                board[i][column] = DEFAULT;
                break;
            }
        }
    }

    public static int evaluate(String board[][]) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            int player = 0, computer = 0;
            for (int j = 0; j < BOARD_SIZE - 3; j++) {
                if (board[i][j].equals(board[i][j + 1]) && board[i][j + 1].equals(board[i][j + 2]) && board[i][j + 2].equals(board[i][j + 3])) {
                    if (board[i][j].equals(PLAYER)) {
                        return PLAYER_WIN;
                    } else if (board[i][j].equals(COMPUTER)) {
                        return COMPUTER_WIN;
                    }
                }
            }
        }

        for (int j = 0; j < BOARD_SIZE; j++) {
            int player = 0, computer = 0;
            for (int i = 0; i < BOARD_SIZE - 3; i++) {
                if (board[i][j].equals(board[i + 1][j]) && board[i + 1][j].equals(board[i + 2][j]) && board[i + 2][j].equals(board[i + 3][j])) {
                    if (board[i][j].equals(PLAYER)) {
                        return PLAYER_WIN;
                    } else if (board[i][j].equals(COMPUTER)) {
                        return COMPUTER_WIN;
                    }
                }
            }
        }

        for (int i = 0; i < BOARD_SIZE - 3; i++) {
            for (int j = 0; j < BOARD_SIZE - 3; j++) {
                if (board[i][j].equals(board[i + 1][j + 1]) && board[i + 1][j + 1].equals(board[i + 2][j + 2]) && board[i + 2][j + 2].equals(board[i + 3][j + 3])) {
                    if (board[i][j].equals(PLAYER)) {
                        return PLAYER_WIN;
                    } else if (board[i][j].equals(COMPUTER)) {
                        return COMPUTER_WIN;
                    }
                }
                if (board[i][j + 3].equals(board[i + 1][j + 2]) && board[i + 1][j + 2].equals(board[i + 2][j + 1]) && board[i + 2][j + 1].equals(board[i + 3][j])) {
                    if (board[i][j + 3].equals(PLAYER)) {
                        return PLAYER_WIN;
                    } else if (board[i][j + 3].equals(COMPUTER)) {
                        return COMPUTER_WIN;
                    }
                }
            }
        }

        return DRAW;
    }

    public static int negamax(String board[][], int depth, boolean isMax, int alpha, int beta) {
        int score = evaluate(board);

        if (score != DRAW || allColumnIsFill(board) || depth == 0) {
            return score;
        }

        int bestValue = Integer.MIN_VALUE;

        for (int i = 0; i < BOARD_SIZE; i++) {

            if (isMax)
                move(board, i, COMPUTER, false);
            else
                move(board, i, PLAYER, false);
            int value = -negamax(board, depth - 1, !isMax, -beta, -alpha);
            lastMove(board, i);

            if (value > bestValue) {
                bestValue = value;
            }

            if (bestValue > alpha) {
                alpha = bestValue;
            }

            if (bestValue >= beta) {
                break;
            }
        }
        return alpha;
    }

    public static int minmax(String board[][], int depth, boolean isMax, int alpha, int beta) {
        int score = evaluate(board);

        //printBoard(board);
        if (score != DRAW || allColumnIsFill(board) || depth > MAX_DEPTH) {
//            printBoard(board);
            return score;
        }


        if (isMax) {
            int best = -INFINITE;

            for (int i = 0; i < BOARD_SIZE; i++) {
                if (!columnIsFill(board, i)) {
                    move(board, i, COMPUTER, false);
                    best = Math.max(best, minmax(board, depth + 1, !isMax, alpha, beta));
                    lastMove(board, i);
                    alpha = Math.max(alpha, best);
//                    System.out.println("max : "+beta + " "+ alpha);

                    // Alpha Beta Pruning
                    if (beta <= alpha) {
//                        System.out.println("alpha");
                        break;
                    }
                }
            }

            return best;

        } else {
            int best = INFINITE;

            for (int i = 0; i < BOARD_SIZE; i++) {
                if (!columnIsFill(board, i)) {
                    move(board, i, PLAYER, false);
                    best = Math.min(best, minmax(board, depth + 1, !isMax, alpha, beta));
                    lastMove(board, i);
                    beta = Math.min(beta, best);
//                    System.out.println("min : "+beta + " "+ alpha);

                    // Alpha Beta Pruning
                    if (beta <= alpha) {
//                        System.out.println("beta");
                        break;
                    }
                }
            }

            return best;
        }
    }

    public static boolean end(String board[][]) {
        int score = evaluate(board);

        if (score == PLAYER_WIN) {
            gameState = PLAYER_WIN;
            System.out.println("PLAYER WIN");
            dialog.setContentText("PLAYER WIN");
            dialog.show();
            boardLockUnlock(true);
            return true;
        } else if (score == COMPUTER_WIN) {
            gameState = COMPUTER_WIN;
            System.out.println("COMPUTER WIN");
            dialog.setContentText("COMPUTER WIN");
            dialog.show();
            boardLockUnlock(true);
            return true;
        } else if (allColumnIsFill(board)) {
            System.out.println("DRAW");
            dialog.setContentText("DRAW");
            dialog.show();
            boardLockUnlock(true);
            return true;
        }

        return false;
    }

    public static void boardLockUnlock(boolean lock) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            columnButtons[i].setDisable(lock);
            columnButtons[i].setVisible(!lock);
        }
    }

    public static int bestMove(String board[][], int counter) {
        Random rand = new Random();

        if (counter == 0) return rand.nextInt(BOARD_SIZE);

        int bestMove = -INFINITE;
        int bestMove_neg = INFINITE;
        int c = rand.nextInt(BOARD_SIZE);
        ArrayList <Pair <Integer,Integer> > movesScores = new ArrayList <Pair <Integer,Integer> > ();
        ArrayList<Integer> bestColumns = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!columnIsFill(board, i)) {
                if (isNegamax) {
                    move(board, i, state, false);
                    int moveVal = -1 * negamax(board, MAX_DEPTH, false, -INFINITE, INFINITE);
                    movesScores.add(new Pair <Integer,Integer> (i, moveVal));
                    lastMove(board, i);

                    if (moveVal > bestMove) {
                        bestMove = moveVal;
                        c = i;
                    }
                    System.out.println(moveVal);
                } else {
                    move(board, i, state, false);
                    int moveVal = minmax(board, 0, false, -INFINITE, INFINITE);
                    movesScores.add(new Pair <Integer,Integer> (i, moveVal));
                    lastMove(board, i);

                    if (moveVal > bestMove) {
                        bestMove = moveVal;
                        c = i;
                    }
                    System.out.println(moveVal);
                }

            }
        }
        for (int i = 0; i < movesScores.size(); i++) {
            if (movesScores.get(i).getValue() == bestMove){
                bestColumns.add(movesScores.get(i).getKey());
            }
        }

        return bestColumns.get(rand.nextInt(bestColumns.size()));
    }

    public static void printBoard(String board[][]) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void resetGame(String[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardHomes[i][j].setStyle(DEFAULT_STYLE);
                board[i][j] = DEFAULT;
            }
        }
        state = PLAYER;
        statePane = PLAYER_STYLE;
        gameState = DRAW;
        isNegamax = true;
        algorithmNameLabel.setText("negamax");
        boardLockUnlock(false);
        counter.set(0);
    }

    public static void makeBoard(AnchorPane pane) {
        boardHomes = new Circle[BOARD_SIZE][BOARD_SIZE];
        boardPane = new Pane();
        menuBar = new MenuBar();
        menu = new Menu("Game");
        gameItem = new MenuItem("reset");
        negamaxGame = new MenuItem("negamax");
        minmaxGame = new MenuItem("minmax");
        stateLabel = new Label("PLAYER");
        algorithmNameLabel = new Label("negamax");
        counter = new AtomicInteger();

        columnButtons = new Button[BOARD_SIZE];
        Double x = 30.0, y = 30.0, buttonStep = 90.0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardHomes[i][j] = new Circle(25);
                boardHomes[i][j].setLayoutX(x);
                boardHomes[i][j].setLayoutY(y);
                boardHomes[i][j].setStyle("-fx-stroke: black;-fx-fill: white");
                boardPane.getChildren().add(boardHomes[i][j]);
                x += 55.0;
            }
            x = 30.0;
            y += 55.0;
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            columnButtons[i] = new Button(i + "");
            columnButtons[i].setLayoutX(buttonStep);
            columnButtons[i].setLayoutY(47);
            pane.getChildren().add(columnButtons[i]);
            buttonStep += 55;
        }

        algorithmNameLabel.setLayoutX(275);
        algorithmNameLabel.setLayoutY(15);

        stateLabel.setLayoutX(250);
        stateLabel.setLayoutY(535);
        stateLabel.setPrefWidth(100);
        stateLabel.setPrefHeight(50);
        stateLabel.setStyle("-fx-background-color: blue");

        menu.getItems().add(gameItem);
        menu.getItems().add(negamaxGame);
        menu.getItems().add(minmaxGame);
        menuBar.getMenus().add(menu);

        dialog = new Dialog<String>();
        retry = new ButtonType("Retry", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().add(retry);

        boardPane.setLayoutX(75);
        boardPane.setLayoutY(75);
        boardPane.setStyle("-fx-background-color: coral");
        boardPane.setPrefHeight(450);
        boardPane.setPrefWidth(450);

        pane.getChildren().add(algorithmNameLabel);
        pane.getChildren().add(stateLabel);
        pane.getChildren().add(menuBar);
        pane.getChildren().add(boardPane);
    }

    public void restart(Stage primaryStage, String board[][]) throws Exception {
        start(primaryStage);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane pane = new AnchorPane();

        String board[][] = new String[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = DEFAULT;
            }
        }

        makeBoard(pane);

        gameItem.setOnAction(event -> {
            try {
                restart(primaryStage, board);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        minmaxGame.setOnAction(event -> {
            isNegamax = false;
            algorithmNameLabel.setText("minmax");
        });
        negamaxGame.setOnAction(event -> {
            isNegamax = true;
            algorithmNameLabel.setText("negamax");
        });

        for (int i = 0; i < BOARD_SIZE; i++) {
            int finalI = i;
            columnButtons[i].pressedProperty().addListener((observable, wasPressed, pressed) -> {
                if (wasPressed && state.equals(COMPUTER) && !allColumnIsFill(board)) {
                    System.out.println("computer " + counter.get());
                    statePane = COMPUTER_STYLE;
                    move(board, bestMove(board, counter.get()), state, true);
                    printBoard(board);

                    stateLabel.setText("PLAYER");
                    stateLabel.setStyle("-fx-background-color: blue");
                    counter.getAndIncrement();
                    state = PLAYER;
                    end(board);
                } else if (!wasPressed && !columnIsFill(board, finalI) && state.equals(PLAYER)) {
                    System.out.println("player " + counter.get());
                    statePane = PLAYER_STYLE;
                    move(board, finalI, state, true);

                    printBoard(board);

                    stateLabel.setText("COMPUTER");
                    stateLabel.setStyle("-fx-background-color: red");

                    state = COMPUTER;

                    end(board);
                }
            });
        }

        primaryStage.setScene(new Scene(pane, 600, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

