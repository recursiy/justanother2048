package ru.recursiy.justanother2048;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

/**
 * interface to track game state changes
 */
interface GameStateObserver
{
    enum Status {
        OK, FINISHED
    }

    /**
     * Method will be call when scores are changed
     * @param scores new scores value
     */
    void onScoresChanged(int scores);

    /**
     * Method will be call when player do some move
     */
    void onMove();

    /**
     * Method will be call when status of game changes
     */
    void onStatusChanged(Status newStatus);

}

/**
 * Class, representing all logic for the game
 */
public class Game2048  implements Parcelable {
    private enum Destination {
        LEFT, RIGHT, UP, DOWN
    }

    private final int FieldSize = 4;

    final Random random = new Random();

    private int[][] field;
    int oldScores, newScores;

    GameStateObserver observer;

    Game2048()
    {
    }

    public void init(GameStateObserver observer)
    {
        field = new int[FieldSize][FieldSize];
        int InitialRandomValuesCount = 3;
        makeRandomField(InitialRandomValuesCount);
        oldScores = newScores = 0;
        initObserver(observer);
    }

    public void initObserver(GameStateObserver observer)
    {
        this.observer = observer;
        if (observer != null)
        {
            observer.onScoresChanged(newScores);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(oldScores);
        parcel.writeInt(newScores);
        for(int i=0; i<FieldSize; ++i)
        {
            parcel.writeIntArray(field[i]);
        }
    }

    public static final Parcelable.Creator<Game2048> CREATOR = new Parcelable.Creator<Game2048>() {
        public Game2048 createFromParcel(Parcel in) {
            return new Game2048(in);
        }

        public Game2048[] newArray(int size) {
            return new Game2048[size];
        }
    };

    public Game2048(Parcel parcel) {
        oldScores = parcel.readInt();
        newScores = parcel.readInt();
        for(int i=0; i<FieldSize; ++i)
        {
            parcel.readIntArray(field[i]);
        }
    }

    public int[][] getField()
    {
        return field;
    }

    public int getFieldSize()
    {
        return FieldSize;
    }

    private int makeRandomField()
    {
        return makeRandomField(1);
    }
    private int makeRandomField(int fieldCount)
    {
        int zeroValuesFieldsCount = 0;
        for(int i=0; i<FieldSize; ++i)
        {
            for(int j=0; j<FieldSize; ++j)
            {
                if(field[i][j] == 0) zeroValuesFieldsCount++;
            }
        }

        if (zeroValuesFieldsCount < fieldCount) return 0;
        for(int generated = 0; generated<fieldCount; ++generated)
        {
            int i, j;
            do {
                i = random.nextInt(4);
                j = random.nextInt(4);
            } while(field[i][j] != 0);
            int value = random.nextInt(7);
            if (value < 4) value = 2;
            else if (value < 6) value = 4;
            else value = 8;
            field[i][j] = value;
        }
        return zeroValuesFieldsCount - fieldCount;
    }

    public void slideLeft() {
        slide(Destination.LEFT);
    }

    public void slideRight() {
        slide(Destination.RIGHT);
    }

    public void slideDown() {
        slide(Destination.DOWN);
    }

    public void slideUp() {
        slide(Destination.UP);
    }

    private void slide(Destination dest)
    {
        int start, stop, step;
        switch(dest)
        {
            case LEFT:
            case UP:
                start = 0;
                stop = 3;
                step = 1;
                break;
            case RIGHT:
            case DOWN:
                start=3;
                stop=0;
                step=-1;
                break;
            default:
                throw new UnknownError("Unexpected value");
        }

        boolean moved = false;
        switch(dest)
        {
            case LEFT:
            case RIGHT:
                moved = slideHorizontal(start, stop, step);
                break;
            case DOWN:
            case UP:
                moved = slideVertical(start, stop, step);
                break;
        }

        if(moved)
        {
            int zeroFieldsCount = makeRandomField();
            if (observer != null)
            {
                observer.onMove();
                if (zeroFieldsCount == 0)
                {
                    observer.onStatusChanged(GameStateObserver.Status.FINISHED);
                }
            }
            if (newScores != oldScores)
            {
                oldScores = newScores;
                if (observer != null)
                {
                    observer.onScoresChanged(newScores);
                }
            }
        }
    }

    private boolean slideHorizontal(int start, int stop, int step) {
        boolean moved = false;
        for(int i=0; i<FieldSize; ++i)
        {
            int moveTo = start;
            for(int j=start+step; j!=stop+step; j+=step)
            {
                if(field[i][j] == 0)
                {
                    continue; //skip to next iteration
                }
                if(field[i][moveTo] == field[i][j])
                {
                    moved = true;
                    field[i][moveTo] += field[i][j];
                    field[i][j]=0;
                    newScores += field[i][moveTo];
                    moveTo += step;
                }
                else if(field[i][moveTo] == 0)
                {
                    moved = true;
                    field[i][moveTo] = field[i][j];
                    field[i][j]=0;
                }
                else if(field[i][moveTo] != field[i][j])
                {
                    moveTo += step;
                    if(moveTo != j)
                    {
                        j-=step;
                    }
                }
            }
        }
        return moved;
    }

    private boolean slideVertical(int start, int stop, int step) {
        boolean moved = false;
        for(int j=0; j<FieldSize; ++j)
        {
            int moveTo = start;
            for(int i=start+step; i!=stop+step; i+=step)
            {
                if(field[i][j] == 0)
                {
                    continue; //skip to next iteration
                }
                if(field[moveTo][j] == field[i][j])
                {
                    moved = true;
                    field[moveTo][j] += field[i][j];
                    field[i][j]=0;
                    newScores += field[moveTo][j];
                    moveTo += step;
                }
                else if(field[moveTo][j] == 0)
                {
                    moved = true;
                    field[moveTo][j] = field[i][j];
                    field[i][j]=0;
                }
                else if(field[moveTo][j] != field[i][j])
                {
                    moveTo += step;
                    if(moveTo != i)
                    {
                        i-=step;
                    }
                }
            }
        }
        return moved;
    }

}
