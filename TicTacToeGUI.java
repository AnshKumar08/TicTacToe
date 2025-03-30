import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI extends JFrame implements ActionListener {
    private static final int SIDE = 3;
    private static final char HUMANMOVE = 'X';
    private static final char COMPUTERMOVE = 'O';

    private char[][] board = new char[SIDE][SIDE];
    private JButton[][] buttons = new JButton[SIDE][SIDE];
    private JButton restartButton;
    private boolean isHumanTurn;

    public TicTacToeGUI() {
        setTitle("Tic Tac Toe - AI");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(SIDE, SIDE));
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.LIGHT_GRAY);
                buttons[i][j].addActionListener(this);
                gridPanel.add(buttons[i][j]);
                board[i][j] = '*';
            }
        }

        restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setBackground(Color.ORANGE);
        restartButton.addActionListener(e -> resetGame());

        add(gridPanel, BorderLayout.CENTER);
        add(restartButton, BorderLayout.SOUTH);

        askStartingPlayer();
    }

    private void askStartingPlayer() {
        int choice = JOptionPane.showOptionDialog(this,
                "Who should start first?",
                "Choose Starting Player",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"Human", "Computer"}, "Human");

        isHumanTurn = (choice == 0);

        if (!isHumanTurn) {
            SwingUtilities.invokeLater(this::computerMove);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isHumanTurn) return;

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (e.getSource() == buttons[i][j] && board[i][j] == '*') {
                    buttons[i][j].setText(String.valueOf(HUMANMOVE));
                    buttons[i][j].setForeground(Color.BLUE);
                    board[i][j] = HUMANMOVE;
                    isHumanTurn = false;

                    if (checkWin(HUMANMOVE)) {
                        showMessage("You Win!");
                        return;
                    } else if (isDraw()) {
                        showMessage("It's a Draw!");
                        return;
                    }

                    SwingUtilities.invokeLater(this::computerMove);
                    return;
                }
            }
        }
    }

    private void computerMove() {
        int bestMove = findBestMove();
        int row = bestMove / SIDE;
        int col = bestMove % SIDE;

        buttons[row][col].setText(String.valueOf(COMPUTERMOVE));
        buttons[row][col].setForeground(Color.RED);
        board[row][col] = COMPUTERMOVE;
        isHumanTurn = true;

        if (checkWin(COMPUTERMOVE)) {
            showMessage("Computer Wins!");
        } else if (isDraw()) {
            showMessage("It's a Draw!");
        }
    }

    private boolean checkWin(char player) {
        for (int i = 0; i < SIDE; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isDraw() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (board[i][j] == '*') return false;
            }
        }
        return true;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
        resetGame();
    }

    private void resetGame() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                board[i][j] = '*';
                buttons[i][j].setText("");
                buttons[i][j].setBackground(Color.LIGHT_GRAY);
            }
        }
        askStartingPlayer();
    }

    private int findBestMove() {
        int bestScore = -1000;
        int bestMove = -1;

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (board[i][j] == '*') {
                    board[i][j] = COMPUTERMOVE;
                    int score = minimax(0, false);
                    board[i][j] = '*';

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = i * SIDE + j;
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(int depth, boolean isAI) {
        if (checkWin(COMPUTERMOVE)) return 10;
        if (checkWin(HUMANMOVE)) return -10;
        if (isDraw()) return 0;

        int bestScore = isAI ? -1000 : 1000;

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (board[i][j] == '*') {
                    board[i][j] = isAI ? COMPUTERMOVE : HUMANMOVE;
                    int score = minimax(depth + 1, !isAI);
                    board[i][j] = '*';

                    if (isAI) bestScore = Math.max(bestScore, score);
                    else bestScore = Math.min(bestScore, score);
                }
            }
        }
        return bestScore;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeGUI().setVisible(true));
    }
}
