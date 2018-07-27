

/**
 * Created by Adrien on 14/07/2018.
 */
public class Game {

    private int _height;
    private int _width;
    public Flappy _flappy;
    public Brain _brain;

    public Game(int width, int height, int flappyx, int flappyradius)
    {
        _width = width;
        _height = height;
        _flappy = new Flappy(_height, flappyx, flappyradius);
        _brain = new Brain();
    }

    public long fitness(int mid)
    {
        int sc = (int)(_flappy.getScore() * 1000);
        int diff = (int)(_height - Math.abs(_flappy.getCenterY() - mid) *10);
        return (long)(Math.pow(sc,2) + diff);
    }

    public String score()
    {
        return Integer.toString((int)( _flappy.getScore() *100));
    }

}
