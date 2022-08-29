package ca.bart.tp1nicolaspoulin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Save
    private final String KEY_NUMBER_MINES = "KEY_NUMBER_MINES";
    private final String KEY_MESSAGE_MINES = "KEY_MESSAGE_MINES";
    private final String KEY_GRID_PRESSED = "KEY_GRID_PRESSED";
    private final String KEY_GRID_MINE = "KEY_GRID_MINE";
    private final String KEY_GRID_FLAG = "KEY_GRID_FLAG";
    private final String KEY_GRID_NUMBER = "KEY_GRID_NUMBER";

    // Mines
    private final String PREFIX_MINE_MESSAGE = "Remaining mines : ";
    private final int MAX_MINES = 10;

    // State Game Prefix
    private final String PREFIX_WIN = "Congrats! You win! :) ";
    private final String PREFIX_LOST = "Game over! You lost! :(";

    // Dimension
    private final int MAX_X = 10;
    private final int MAX_Y = 10;
    private int m_RemainingMines = MAX_MINES;

    // Layout's variables
    private Button[][] m_GridButton = new Button[MAX_X][MAX_Y];
    private Button m_BtnReset;
    private TextView m_Information;

    // Game's variables
    private boolean[][] m_GridMine = new boolean[MAX_X][MAX_Y]; // false = nothing / true = Mine
    private boolean[][] m_GridFlag = new boolean[MAX_X][MAX_Y]; // false = nothing / true = Flag
    private boolean[][] m_GridPressed = new boolean[MAX_X][MAX_Y]; // false = nothing / true = Pressed
    private int[][] m_GridNumber = new int[MAX_X][MAX_Y];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_BtnReset = findViewById(R.id.BtnReset);
        m_Information = findViewById(R.id.info);

        m_BtnReset.setOnClickListener(v -> {
            reset();
        });

        reset();
    }

    private void reset() {
        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                String t_ID = "Btn" + i + j;
                int resID = getResources().getIdentifier(t_ID, "id", getPackageName());
                m_GridButton[i][j] = findViewById(resID);
                m_GridButton[i][j].setBackgroundResource(R.drawable.full_case);
                m_GridButton[i][j].setEnabled(true);
                m_GridButton[i][j].setText("");

                final int x = i;
                final int y = j;

                m_GridButton[i][j].setOnClickListener(v -> {
                    onClickButton(x, y);
                });
                m_GridButton[i][j].setOnLongClickListener(v -> {
                    onLongClickButton(x, y);
                    return true;
                });

                m_GridPressed[i][j] = false;
                m_GridMine[i][j] = false;
                m_GridFlag[i][j] = false;
            }
        }

        generateMines();
        generateNumberFromBomb();

        m_RemainingMines = MAX_MINES;
        m_Information.setText(PREFIX_MINE_MESSAGE + m_RemainingMines);
    }

    private void generateMines() {
        int countPlaceMines = 0;

        while (countPlaceMines != MAX_MINES) {
            int t_RandX = (int) (Math.random() * MAX_X);
            int t_RandY = (int) (Math.random() * MAX_Y);

            if (!m_GridMine[t_RandX][t_RandY]) {
                m_GridMine[t_RandX][t_RandY] = true;
                ++countPlaceMines;
            }
        }
    }

    private void onClickButton(int a_x, int a_y) {
        String t_ID = "Btn" + a_x + a_y;
        int resID = getResources().getIdentifier(t_ID, "id", getPackageName());
        Button t_TempButton = findViewById(resID);

        if (m_GridMine[a_x][a_y] && !m_GridFlag[a_x][a_y]) {
            t_TempButton.setBackgroundResource(R.drawable.mine);
            m_GridPressed[a_x][a_y] = true;
            t_TempButton.setEnabled(false);
            gameOver(PREFIX_LOST);
        } else if (!m_GridFlag[a_x][a_y]) {
            t_TempButton.setBackgroundResource(R.drawable.default_case);
            setNumberButton(a_x, a_y);

            if (m_GridNumber[a_x][a_y] == 0) {
                multiPressedButton(a_x, a_y);
            }

            m_GridPressed[a_x][a_y] = true;
            t_TempButton.setEnabled(false);
        }

        checkWin();
    }

    private void multiPressedButton(int a_x, int a_y) {
        if (a_x - 1 >= 0 && a_y - 1 >= 0) {
            if (m_GridNumber[a_x - 1][a_y - 1] == 0 && !m_GridPressed[a_x - 1][a_y - 1]) {
                changeStateButton(a_x - 1, a_y - 1);
                multiPressedButton(a_x - 1, a_y - 1);
            } else {
                changeStateButton(a_x - 1, a_y - 1);
            }
        }

        if (a_y - 1 >= 0) {
            if (m_GridNumber[a_x][a_y - 1] == 0 && !m_GridPressed[a_x][a_y - 1]) {
                changeStateButton(a_x, a_y - 1);
                multiPressedButton(a_x, a_y - 1);
            } else {
                changeStateButton(a_x, a_y - 1);
            }
        }

        if (a_x + 1 < MAX_X && a_y - 1 >= 0) {
            if (m_GridNumber[a_x + 1][a_y - 1] == 0 && !m_GridPressed[a_x + 1][a_y - 1]) {
                changeStateButton(a_x + 1, a_y - 1);
                multiPressedButton(a_x + 1, a_y - 1);
            } else {
                changeStateButton(a_x + 1, a_y - 1);
            }
        }

        if (a_x - 1 >= 0) {
            if (m_GridNumber[a_x - 1][a_y] == 0 && !m_GridPressed[a_x - 1][a_y]) {
                changeStateButton(a_x - 1, a_y);
                multiPressedButton(a_x - 1, a_y);
            } else {
                changeStateButton(a_x - 1, a_y);
            }
        }

        if (a_x + 1 < MAX_X) {
            if (m_GridNumber[a_x + 1][a_y] == 0 && !m_GridPressed[a_x + 1][a_y]) {
                changeStateButton(a_x + 1, a_y);
                multiPressedButton(a_x + 1, a_y);
            } else {
                changeStateButton(a_x + 1, a_y);
            }
        }

        if (a_x - 1 >= 0 && a_y + 1 < MAX_Y) {
            if (m_GridNumber[a_x - 1][a_y + 1] == 0 && !m_GridPressed[a_x - 1][a_y + 1]) {
                changeStateButton(a_x - 1, a_y + 1);
                multiPressedButton(a_x - 1, a_y + 1);
            } else {
                changeStateButton(a_x - 1, a_y + 1);
            }
        }

        if (a_y + 1 < MAX_Y) {
            if (m_GridNumber[a_x][a_y + 1] == 0 && !m_GridPressed[a_x][a_y + 1]) {
                changeStateButton(a_x, a_y + 1);
                multiPressedButton(a_x, a_y + 1);
            } else {
                changeStateButton(a_x, a_y + 1);
            }
        }

        if (a_x + 1 < MAX_X && a_y + 1 < MAX_Y) {
            if (m_GridNumber[a_x + 1][a_y + 1] == 0 && !m_GridPressed[a_x + 1][a_y + 1]) {
                changeStateButton(a_x + 1, a_y + 1);
                multiPressedButton(a_x + 1, a_y + 1);
            } else {
                changeStateButton(a_x + 1, a_y + 1);
            }
        }
    }

    private void changeStateButton(int a_x, int a_y) {
        if(m_GridFlag[a_x][a_y])
        {
            m_GridFlag[a_x][a_y] = false;
            m_Information.setText(PREFIX_MINE_MESSAGE + ++m_RemainingMines);
        }

        setNumberButton(a_x, a_y);
        m_GridButton[a_x][a_y].setBackgroundResource(R.drawable.default_case);
        m_GridPressed[a_x][a_y] = true;
        m_GridButton[a_x][a_y].setEnabled(false);
    }

    private void onLongClickButton(int a_x, int a_y) {
        String t_ID = "Btn" + a_x + a_y;
        int resID = getResources().getIdentifier(t_ID, "id", getPackageName());
        Button t_TempButton = findViewById(resID);

        if (m_GridFlag[a_x][a_y]) {
            t_TempButton.setBackgroundResource(R.drawable.full_case);
            m_GridFlag[a_x][a_y] = false;
            m_Information.setText(PREFIX_MINE_MESSAGE + ++m_RemainingMines);
        } else {
            t_TempButton.setBackgroundResource(R.drawable.flag);
            m_GridFlag[a_x][a_y] = true;
            m_Information.setText(PREFIX_MINE_MESSAGE + --m_RemainingMines);
        }

        checkWin();
    }

    @Override
    protected void onSaveInstanceState(Bundle a_OutState) {
        super.onSaveInstanceState(a_OutState);

        // Save grid
        for (int i = 0; i < m_GridPressed.length; i++) {
            a_OutState.putBooleanArray(KEY_GRID_PRESSED + i, m_GridPressed[i]);
        }

        for (int i = 0; i < m_GridMine.length; i++) {
            a_OutState.putBooleanArray(KEY_GRID_MINE + i, m_GridMine[i]);
        }

        for (int i = 0; i < m_GridFlag.length; i++) {
            a_OutState.putBooleanArray(KEY_GRID_FLAG + i, m_GridFlag[i]);
        }
        for (int i = 0; i < m_GridNumber.length; i++) {
            a_OutState.putIntArray(KEY_GRID_NUMBER + i, m_GridNumber[i]);
        }

        // Save info
        String t_Message = m_Information.getText().toString();
        a_OutState.putString(KEY_MESSAGE_MINES, t_Message);

        a_OutState.putInt(KEY_NUMBER_MINES, m_RemainingMines);
    }

    @Override
    protected void onRestoreInstanceState(Bundle a_SavedInstanceState) {
        super.onRestoreInstanceState(a_SavedInstanceState);

        for (int i = 0; i < m_GridPressed.length; i++) {
            m_GridPressed[i] = a_SavedInstanceState.getBooleanArray(KEY_GRID_PRESSED + i);
        }

        for (int i = 0; i < m_GridMine.length; i++) {
            m_GridMine[i] = a_SavedInstanceState.getBooleanArray(KEY_GRID_MINE + i);
        }

        for (int i = 0; i < m_GridFlag.length; i++) {
            m_GridFlag[i] = a_SavedInstanceState.getBooleanArray(KEY_GRID_FLAG + i);
        }

        for (int i = 0; i < m_GridNumber.length; i++) {
            m_GridNumber[i] = a_SavedInstanceState.getIntArray(KEY_GRID_NUMBER + i);
        }

        RestoreGrid();

        String t_Message = a_SavedInstanceState.getString(KEY_MESSAGE_MINES, null);
        m_Information.setText(t_Message);

        m_RemainingMines = a_SavedInstanceState.getInt(KEY_NUMBER_MINES);

        if (m_Information.getText() == PREFIX_LOST) {
            gameOver(PREFIX_LOST);
        } else if (m_Information.getText() == PREFIX_WIN) {
            gameOver(PREFIX_WIN);
        }
    }

    private void RestoreGrid() {
        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                if (m_GridFlag[i][j]) // Check if flag
                {
                    m_GridButton[i][j].setBackgroundResource(R.drawable.flag);
                } else if (m_GridPressed[i][j] && m_GridMine[i][j])  // Check if pressed + bomb
                {
                    m_GridButton[i][j].setBackgroundResource(R.drawable.mine);
                } else if (m_GridPressed[i][j]) // Check if pressed
                {
                    m_GridButton[i][j].setBackgroundResource(R.drawable.default_case);

                    if (m_GridNumber[i][j] != 0) {
                        setNumberButton(i, j);
                    }
                } else {
                    m_GridButton[i][j].setBackgroundResource(R.drawable.full_case);
                }
            }
        }
    }

    private void setNumberButton(int a_x, int a_y) {
        if (m_GridNumber[a_x][a_y] != 0) {
            m_GridButton[a_x][a_y].setText(m_GridNumber[a_x][a_y] + "");
        }

        switch (m_GridNumber[a_x][a_y]) {
            case 1:
                m_GridButton[a_x][a_y].setTextColor(0xFF0079FF); // Blue
                break;
            case 2:
                m_GridButton[a_x][a_y].setTextColor(0xFF00FF00); // Green
                break;
            case 3:
                m_GridButton[a_x][a_y].setTextColor(0xFFDB5018); // Red
                break;
            case 4:
                m_GridButton[a_x][a_y].setTextColor(0xFF000058); // Dark Blue
                break;
            case 5:
                m_GridButton[a_x][a_y].setTextColor(0xFF7C0000); // Brown
                break;
            case 6:
                m_GridButton[a_x][a_y].setTextColor(0xFF99CCFF); // Turquoise
                break;
            case 7:
                m_GridButton[a_x][a_y].setTextColor(0xFF933BBD); // Mauve
                break;
            case 8:
                m_GridButton[a_x][a_y].setTextColor(0xFF171719); // Black
                break;
        }
    }

    private void generateNumberFromBomb() {
        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                int nbBomb = 0;

                if (m_GridMine[i][j]) {
                    m_GridNumber[i][j] = 0;
                    continue;
                }

                if (i - 1 >= 0 && j - 1 >= 0) {
                    nbBomb = m_GridMine[i - 1][j - 1] ? ++nbBomb : nbBomb;
                }

                if (j - 1 >= 0) {
                    nbBomb = m_GridMine[i][j - 1] ? ++nbBomb : nbBomb;
                }

                if (i + 1 < MAX_X && j - 1 >= 0) {
                    nbBomb = m_GridMine[i + 1][j - 1] ? ++nbBomb : nbBomb;
                }

                if (i - 1 >= 0) {
                    nbBomb = m_GridMine[i - 1][j] ? ++nbBomb : nbBomb;
                }

                if (i + 1 < MAX_X) {
                    nbBomb = m_GridMine[i + 1][j] ? ++nbBomb : nbBomb;
                }

                if (i - 1 >= 0 && j + 1 < MAX_Y) {
                    nbBomb = m_GridMine[i - 1][j + 1] ? ++nbBomb : nbBomb;
                }

                if (j + 1 < MAX_Y) {
                    nbBomb = m_GridMine[i][j + 1] ? ++nbBomb : nbBomb;
                }

                if (i + 1 < MAX_X && j + 1 < MAX_Y) {
                    nbBomb = m_GridMine[i + 1][j + 1] ? ++nbBomb : nbBomb;
                }

                m_GridNumber[i][j] = nbBomb;
            }
        }
    }

    private void gameOver(String a_State) {
        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                m_GridButton[i][j].setEnabled(false);

                if (!m_GridMine[i][j] && m_GridFlag[i][j]) {
                    m_GridButton[i][j].setBackgroundResource(R.drawable.flagerror);
                } else if (m_GridMine[i][j] && m_GridFlag[i][j]) {
                    m_GridButton[i][j].setBackgroundResource(R.drawable.flag);
                } else if (m_GridMine[i][j]) {
                    m_GridButton[i][j].setBackgroundResource(R.drawable.mine);
                    m_GridButton[i][j].setText("");
                }
            }
        }

        m_Information.setText(a_State);
    }

    private void checkWin() {
        int countGoodFlag = 0; // Count the number of flag that are on a mine
        int countPressed = 0; // Count the number of button pressed

        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                if (m_GridFlag[i][j] && m_GridMine[i][j]) {
                    countGoodFlag++;
                }
                else if (m_GridPressed[i][j]) {
                    countPressed++;
                }
                else if (m_GridFlag[i][j] && !m_GridMine[i][j])
                {
                    countGoodFlag--;
                }
            }
        }

        if (countPressed == 90) {
            for (int i = 0; i < MAX_X; i++) {
                for (int j = 0; j < MAX_Y; j++) {
                    if (m_GridMine[i][j]) {
                        m_GridButton[i][j].setBackgroundResource(R.drawable.full_case);
                    }
                }
            }

            gameOver(PREFIX_WIN);
        }

        if (countGoodFlag == MAX_MINES) {
            gameOver(PREFIX_WIN);
        }
    }
}